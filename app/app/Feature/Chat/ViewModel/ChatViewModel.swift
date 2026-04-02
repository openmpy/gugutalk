import SwiftUI
import Combine

@MainActor
final class ChatViewModel: ObservableObject {

    private let chatRoomService = ChatRoomService.shared
    private let stomp = StompManager.shared

    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true
    @Published var chatRooms: [ChatRoomGetResponse] = []

    private var cursorId: Int64?
    private var cursorDateAt: String?
    private var cancellables = Set<AnyCancellable>()

    func gets() async -> Result<Void, Error> {
        hasNext = true

        guard !isLoading else { return .success(()) }
        guard hasNext else { return .success(()) }

        isLoading = true
        defer { isLoading = false }

        do {
            let response = try await chatRoomService.gets(
                cursorId: nil,
                cursorDateAt: nil
            )
            chatRooms = response.payload
            cursorId = response.nextId
            cursorDateAt = response.nextDateAt
            hasNext = response.hasNext
            return .success(())
        } catch {
            return .failure(error)
        }
    }

    func loadMore() async -> Result<Void, Error> {
        guard !isLoading else { return .success(()) }
        guard hasNext else { return .success(()) }

        isLoading = true
        defer { isLoading = false }

        do {
            let response = try await chatRoomService.gets(
                cursorId: cursorId,
                cursorDateAt: cursorDateAt
            )
            chatRooms.append(contentsOf: response.payload)
            cursorId = response.nextId
            cursorDateAt = response.nextDateAt
            hasNext = response.hasNext
            return .success(())
        } catch {
            return .failure(error)
        }
    }

    // MARK: - 이벤트

    func subscribe() {
        stomp.publisher(for: "/user/queue/chat-rooms")
            .compactMap {
                try? JSONDecoder().decode(ChatEvent<ChatRoomSendEvent>.self, from: Data($0.utf8))
            }
            .receive(on: DispatchQueue.main)
            .sink { [weak self] event in
                guard let self = self else { return }
                guard event.eventType == "SEND_CHAT_ROOM",
                      let payload = event.payload
                else { return }

                self.upsertChatRoom(payload)
            }
            .store(in: &cancellables)
    }

    func unsubscribe() {
        cancellables.removeAll()
    }

    private func upsertChatRoom(_ event: ChatRoomSendEvent) {
        let newRoom = ChatRoomGetResponse(
            chatRoomId: event.chatRoomId,
            nickname: event.nickname,
            profileUrl: event.profileUrl,
            lastMessage: event.lastMessage,
            lastMessageAt: event.lastMessageAt,
            sortAt: event.lastMessageAt ?? ""
        )

        if let index = chatRooms.firstIndex(where: { $0.chatRoomId == event.chatRoomId }) {
            chatRooms.remove(at: index)
        }
        chatRooms.insert(newRoom, at: 0)
    }
}
