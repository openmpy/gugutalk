import SwiftUI
import Combine

@MainActor
final class PrivateImageGrantListViewModel: ObservableObject {

    private let grantService = PrivateImageGrantService.shared

    @Published var state: SocialViewState = .idle
    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true

    @Published var members: [SettingResponse] = []

    private var cursorId: Int64?
    private var cursorDateAt: String?

    func getGrantedMember() async {
        guard case .loading = state else {
            state = .loading
            return await fetchFirstPage()
        }
    }

    private func fetchFirstPage() async {
        do {
            let response = try await grantService.getGrantedMember(
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

    func loadMoreGrantedMember() async throws {
        guard !isLoading, hasNext else { return }

        isLoading = true
        defer { isLoading = false }

        let response = try await grantService.getGrantedMember(
            cursorId: cursorId,
            cursorDateAt: cursorDateAt
        )

        members.append(contentsOf: response.payload)
        cursorId = response.nextId
        cursorDateAt = response.nextDateAt
        hasNext = response.hasNext
    }

    func revoke(memberId: Int64) async throws {
        members.removeAll { $0.memberId == memberId }

        if members.isEmpty {
            state = .empty
        }
        _ = try await grantService.revoke(memberId: memberId)
    }
}
