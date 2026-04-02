import SwiftUI
import Combine

@MainActor
final class MessageViewModel: ObservableObject {

    private let messageService = MessageService.shared
    private let stomp = StompManager.shared

    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true
    @Published var messages: [MessageGetResponse] = []

    private var cursorId: Int64?
    private var cursorDateAt: String?
    private var cancellables = Set<AnyCancellable>()

    func gets(chatRoomId: Int64) async -> Result<Void, Error> {
        hasNext = true

        guard !isLoading else { return .success(()) }
        guard hasNext else { return .success(()) }

        isLoading = true
        defer { isLoading = false }

        do {
            let response = try await messageService.gets(
                chatRoomId: chatRoomId,
                cursorId: nil,
                cursorDateAt: nil
            )
            messages = response.payload
            cursorId = response.nextId
            cursorDateAt = response.nextDateAt
            hasNext = response.hasNext
            return .success(())
        } catch {
            return .failure(error)
        }
    }

    func loadMoreMessage(chatRoomId: Int64) async -> Result<Void, Error> {
        guard !isLoading else { return .success(()) }
        guard hasNext else { return .success(()) }

        isLoading = true
        defer { isLoading = false }

        do {
            let response = try await messageService.gets(
                chatRoomId: chatRoomId,
                cursorId: cursorId,
                cursorDateAt: cursorDateAt
            )
            messages.append(contentsOf: response.payload)
            cursorId = response.nextId
            cursorDateAt = response.nextDateAt
            hasNext = response.hasNext
            return .success(())
        } catch {
            return .failure(error)
        }
    }

    func sendMessage(chatRoomId: Int64, content: String) async -> Result<Void, Error> {
        guard let data = try? JSONEncoder().encode(
            MessageSendRequest(
                content: content,
                type: "TEXT"
            )
        ), let body = String(data: data, encoding: .utf8) else { return .failure(CancellationError()) }

        stomp.send(
            body: body,
            to: "/app/chat/\(chatRoomId)",
            headers: ["content-type": "application/json"],
        )
        return .success(())
    }

    func subscribeRoom(chatRoomId: Int64) {
        stomp.publisher(for: "/topic/chat/\(chatRoomId)")
            .compactMap { try? JSONDecoder().decode(MessageGetResponse.self, from: Data($0.utf8)) }
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in self?.messages.insert($0, at: 0) }
            .store(in: &cancellables)
    }

    func unsubscribeRoom() {
        cancellables.removeAll()
    }
}
