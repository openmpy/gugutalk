import SwiftUI
import Combine

@MainActor
final class MemberSearchViewModel: ObservableObject {

    private let memberService = MemberService.shared

    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true
    @Published var members: [MemberDiscoveryResponse] = []

    private var keyword: String = ""
    private var cursorId: Int64?

    func search(keyword: String) async -> Result<Void, Error> {
        guard keyword.count >= 2 else {
            members = []
            return .success(())
        }

        self.keyword = keyword
        cursorId = nil
        hasNext = true

        guard !isLoading else { return .success(()) }

        isLoading = true
        defer { isLoading = false }

        do {
            let response = try await memberService.search(
                keyword: keyword,
                cursorId: nil
            )
            members = response.payload
            cursorId = response.nextId
            hasNext = response.hasNext
            return .success(())
        } catch {
            return .failure(error)
        }
    }

    func loadMore(keyword: String) async -> Result<Void, Error> {
        guard !isLoading else { return .success(()) }
        guard hasNext else { return .success(()) }

        isLoading = true
        defer { isLoading = false }

        do {
            let response = try await memberService.search(
                keyword: keyword,
                cursorId: cursorId
            )
            members.append(contentsOf: response.payload)
            cursorId = response.nextId
            hasNext = response.hasNext
            return .success(())
        } catch {
            return .failure(error)
        }
    }
}
