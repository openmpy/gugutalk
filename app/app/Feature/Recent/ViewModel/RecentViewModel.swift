import SwiftUI
import Combine

@MainActor
final class RecentViewModel: ObservableObject {

    private let recentService = RecentService.shared
    private let memberService = MemberService.shared

    @Published var showErrorAlert: Bool = false
    @Published var errorMessage: String = ""
    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true

    @Published var members: [MemberDiscoveryResponse] = []

    private var cursorId: Int64?
    private var cursorDateAt: String?

    func getRecentMembers(gender: String) async {
        hasNext = true

        guard !isLoading, hasNext else {
            return
        }

        isLoading = true
        defer { isLoading = false }

        do {
            let response = try await recentService.getRecentMembers(
                gender: gender,
                cursorId: nil,
                cursorDateAt: nil
            )

            members = response.payload
            cursorId = response.nextId
            cursorDateAt = response.nextDateAt
            hasNext = response.hasNext
        } catch {
            showErrorAlert = true
            errorMessage = error.localizedDescription
        }
    }

    func loadMoreGrantedMember(gender: String) async {
        guard !isLoading, hasNext else {
            return
        }

        isLoading = true
        defer { isLoading = false }

        do {
            let response = try await recentService.getRecentMembers(
                gender: gender,
                cursorId: cursorId,
                cursorDateAt: cursorDateAt
            )

            members.append(contentsOf: response.payload)
            cursorId = response.nextId
            cursorDateAt = response.nextDateAt
            hasNext = response.hasNext
        } catch {
            showErrorAlert = true
            errorMessage = error.localizedDescription
        }
    }

    func bump() async {
        guard !isLoading else {
            return
        }

        isLoading = true
        defer { isLoading = false }

        do {
            try await memberService.bump()
        } catch {
            showErrorAlert = true
            errorMessage = error.localizedDescription
        }
    }
}
