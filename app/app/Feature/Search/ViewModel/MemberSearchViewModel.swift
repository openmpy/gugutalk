import SwiftUI
import Combine

@MainActor
final class MemberSearchViewModel: ObservableObject {

    private let memberService = MemberService.shared

    @Published var state: MemberSearchViewState = .idle
    @Published var isPaging: Bool = false
    @Published var hasNext: Bool = true

    @Published var members: [MemberSearchResponse] = []
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
            let response = try await memberService.search(
                nickname: nickname,
                cursorId: nil,
                cursorSimilarity: nil
            )

            members = response.payload
            cursorId = response.nextId
            cursorSimilarity = response.nextSimilarity
            hasNext = response.hasNext

            state = members.isEmpty ? .empty : .data
        } catch {
            state = .error(error.localizedDescription)
        }
    }

    func loadMore() async throws {
        guard !isPaging, hasNext else { return }

        isPaging = true
        defer { isPaging = false }

        let response = try await memberService.search(
            nickname: nickname,
            cursorId: cursorId,
            cursorSimilarity: cursorSimilarity
        )

        members.append(contentsOf: response.payload)
        cursorId = response.nextId
        cursorSimilarity = response.nextSimilarity
        hasNext = response.hasNext
    }
}
