import SwiftUI
import Combine

@MainActor
final class LikeListViewModel: ObservableObject {

    private let socialService = SocialService.shared

    @Published var showErrorAlert: Bool = false
    @Published var errorMessage: String = ""
    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true

    @Published var members: [SettingResponse] = []

    private var cursorId: Int64?
    private var cursorDateAt: String?

    func getLikedMember() async {
        hasNext = true

        guard !isLoading, hasNext else {
            return
        }

        isLoading = true
        defer { isLoading = false }

        do {
            let response = try await socialService.getLikedMember(
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

    func loadMoreLikedMember() async {
        guard !isLoading, hasNext else {
            return
        }

        isLoading = true
        defer { isLoading = false }

        do {
            let response = try await socialService.getLikedMember(
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
}
