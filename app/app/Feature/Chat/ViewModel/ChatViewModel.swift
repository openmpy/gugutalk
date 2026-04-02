import SwiftUI
import Combine

@MainActor
final class ChatViewModel: ObservableObject {

    private let chatRoomService = ChatRoomService.shared

    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true
    @Published var chatRooms: [ChatRoomGetResponse] = []

    private var cursorId: Int64?
    private var cursorDateAt: String?

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
}
