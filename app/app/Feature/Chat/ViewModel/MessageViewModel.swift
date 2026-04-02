import SwiftUI
import Combine

@MainActor
final class MessageViewModel: ObservableObject {

    private let messageService = MessageService.shared

    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true
    @Published var messages: [MessageGetResponse] = []

    private var cursorId: Int64?
    private var cursorDateAt: String?

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
}
