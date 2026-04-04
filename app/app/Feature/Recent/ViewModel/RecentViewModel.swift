import SwiftUI
import Combine

@MainActor
final class RecentViewModel: ObservableObject {

    private let recentService = RecentService.shared
    private let memberService = MemberService.shared

    @Published var state: RecentViewState = .idle
    @Published var isLoading: Bool = false
    @Published var isPaging: Bool = false
    @Published var hasNext: Bool = true

    @Published var members: [MemberDiscoveryResponse] = []
    @Published var selectGender: String = "ALL"
    @Published var comment: String = ""

    private var cursorId: Int64?
    private var cursorDateAt: String?

    func getRecentMembers() async {
        guard case .loading = state else {
            state = .loading
            return await fetchFirstPage()
        }
    }

    private func fetchFirstPage() async {
        do {
            let response = try await recentService.getRecentMembers(
                gender: selectGender,
                cursorId: nil,
                cursorDateAt: nil
            )

            members = response.payload
            cursorId = response.nextId
            cursorDateAt = response.nextDateAt
            hasNext = response.hasNext

            state = members.isEmpty ? .empty : .data
        } catch {
            state = .error(error.localizedDescription)
        }
    }

    func loadMoreRecentMembers() async throws {
        guard !isPaging, hasNext else { return }

        isPaging = true
        defer { isPaging = false }

        let response = try await recentService.getRecentMembers(
            gender: selectGender,
            cursorId: cursorId,
            cursorDateAt: cursorDateAt
        )

        members.append(contentsOf: response.payload)
        cursorId = response.nextId
        cursorDateAt = response.nextDateAt
        hasNext = response.hasNext
    }

    func updateComment() async throws {
        guard !isLoading else { return }
        guard !comment.isEmpty else {
            throw AppError("코멘트 내용을 입력해주세요.")
        }

        isLoading = true
        defer { isLoading = false }

        try await memberService.updateComment(comment: comment)
    }

    func bump(latitude: Double?, longitude: Double?) async throws {
        try await memberService.bump(latitude: latitude, longitude: longitude)
    }
}
