import SwiftUI
import Combine

@MainActor
final class MemberSearchViewModel: ObservableObject {

    private let memberService = MemberService.shared

    @Published var state: MemberSearchViewState = .idle
    @Published var isPaging: Bool = false
    @Published var hasNext: Bool = true

    @Published var members: [MemberDiscoveryResponse] = []
    @Published var keyword: String = ""

    private var isLoading: Bool = false
    private var cursorId: Int64?

    func search() async {
        guard keyword.count >= 2 else {
            state = .idle
            return
        }
        guard !isLoading else { return }

        isLoading = true
        defer { isLoading = false }

        state = .loading

        do {
            let response = try await memberService.search(
                keyword: keyword,
                cursorId: nil
            )

            members = response.payload
            cursorId = response.nextId
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
            keyword: keyword,
            cursorId: cursorId
        )

        members.append(contentsOf: response.payload)
        cursorId = response.nextId
        hasNext = response.hasNext
    }
}
