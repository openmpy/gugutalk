import SwiftUI
import Combine

@MainActor
final class ChatSearchViewModel: ObservableObject {

    private let chatRoomService = ChatRoomService.shared
    private let stomp = StompManager.shared

    @Published var state: MemberSearchViewState = .idle
    @Published var isPaging: Bool = false
    @Published var hasNext: Bool = true

    @Published var chatRooms: [ChatRoomSearchResponse] = []
    @Published var nickname: String = ""

    private var isLoading: Bool = false

    private var cursorId: Int64?
    private var cursorSimilarity: Double?

    func search() async {
        guard nickname.count >= 2 else {
            state = .idle
            return
        }
        guard !isLoading else { return }

        isLoading = true
        defer { isLoading = false }

        state = .loading

        do {
            let response = try await chatRoomService.search(
                nickname: nickname,
                cursorId: nil,
                cursorSimilarity: nil,
            )

            chatRooms = response.payload
            cursorId = response.nextId
            cursorSimilarity = response.nextSimilarity
            hasNext = response.hasNext

            state = chatRooms.isEmpty ? .empty : .data
        } catch {
            state = .error(error.localizedDescription)
        }
    }

    func loadMore() async throws {
        guard !isPaging, hasNext else { return }

        isPaging = true
        defer { isPaging = false }

        let response = try await chatRoomService.search(
            nickname: nickname,
            cursorId: cursorId,
            cursorSimilarity: cursorSimilarity
        )

        chatRooms.append(contentsOf: response.payload)
        cursorId = response.nextId
        cursorSimilarity = response.nextSimilarity
        hasNext = response.hasNext
    }
}
