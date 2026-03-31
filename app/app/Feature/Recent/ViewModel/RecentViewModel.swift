import SwiftUI
import Combine

@MainActor
final class RecentViewModel: ObservableObject {

    private let recentService = RecentService.shared
    private let memberService = MemberService.shared

    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true
    @Published var members: [MemberDiscoveryResponse] = []

    private var cursorId: Int64?
    private var cursorDateAt: String?

    func getRecentMembers(gender: String) async -> Result<Void, Error> {
        hasNext = true

        guard !isLoading else { return .success(()) }
        guard hasNext else { return .success(()) }

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
            return .success(())
        } catch {
            return .failure(error)
        }
    }

    func loadMoreGrantedMember(gender: String) async -> Result<Void, Error> {
        guard !isLoading else { return .success(()) }
        guard hasNext else { return .success(()) }

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
            return .success(())
        } catch {
            return .failure(error)
        }
    }

    func bump() async -> Result<Void, Error> {
        guard !isLoading else { return .failure(CancellationError()) }

        isLoading = true
        defer { isLoading = false }

        do {
            try await memberService.bump()
            return .success(())
        } catch {
            return .failure(error)
        }
    }
}
