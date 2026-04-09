import SwiftUI
import Combine

@MainActor
final class BlockListViewModel: ObservableObject {

    private let socialService = SocialService.shared

    @Published var state: SocialViewState = .idle
    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true

    @Published var members: [SettingResponse] = []

    private var cursorId: Int64?

    func getBlockedMember() async {
        guard case .loading = state else {
            state = .loading
            return await fetchFirstPage()
        }
    }

    private func fetchFirstPage() async {
        do {
            let response = try await socialService.getBlockedMember(
                cursorId: nil,
            )

            members = response.payload
            cursorId = response.nextId
            hasNext = response.hasNext

            state = members.isEmpty ? .empty : .data

        } catch {
            state = .error(error.localizedDescription)
        }
    }

    func loadMoreBlockedMember() async throws {
        guard !isLoading, hasNext else { return }

        isLoading = true
        defer { isLoading = false }

        let response = try await socialService.getBlockedMember(
            cursorId: cursorId,
        )

        members.append(contentsOf: response.payload)
        cursorId = response.nextId
        hasNext = response.hasNext
    }
    
    func remove(memberId: Int64) async throws {
        members.removeAll { $0.memberId == memberId }

        if members.isEmpty {
            state = .empty
        }
        _ = try await socialService.unblock(memberId: memberId)
    }
}
