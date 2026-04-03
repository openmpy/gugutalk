import SwiftUI
import Combine

@MainActor
final class LikeListViewModel: ObservableObject {

    private let socialService = SocialService.shared

    @Published var state: ViewState = .idle
    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true

    @Published var members: [SettingResponse] = []

    private var cursorId: Int64?
    private var cursorDateAt: String?

    func getLikedMember() async {
        guard case .loading = state else {
            state = .loading
            return await fetchFirstPage()
        }
    }

    private func fetchFirstPage() async {
        do {
            let response = try await socialService.getLikedMember(
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

    func loadMoreLikedMember() async throws {
        guard !isLoading, hasNext else { return }

        isLoading = true
        defer { isLoading = false }

        let response = try await socialService.getLikedMember(
            cursorId: cursorId,
            cursorDateAt: cursorDateAt
        )

        members.append(contentsOf: response.payload)
        cursorId = response.nextId
        cursorDateAt = response.nextDateAt
        hasNext = response.hasNext
    }
    
    func unlike(memberId: Int64) async throws {
        members.removeAll { $0.memberId == memberId }

        if members.isEmpty {
            state = .empty
        }
        _ = try await socialService.unlike(memberId: memberId)
    }
}
