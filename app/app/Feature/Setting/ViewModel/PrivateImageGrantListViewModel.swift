import SwiftUI
import Combine

@MainActor
final class PrivateImageGrantListViewModel: ObservableObject {

    private let privateImageGrantService = PrivateImageGrantService.shared

    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true
    @Published var members: [SettingResponse] = []

    private var cursorId: Int64?
    private var cursorDateAt: String?

    func getGrantedMember() async -> Result<Void, Error> {
        hasNext = true

        guard !isLoading else { return .failure(CancellationError()) }
        guard hasNext else { return .success(()) }

        isLoading = true
        defer { isLoading = false }

        do {
            let response = try await privateImageGrantService.getGrantedMember(
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

    func loadMoreGrantedMember() async -> Result<Void, Error> {
        guard !isLoading else { return .failure(CancellationError()) }
        guard hasNext else { return .success(()) }

        isLoading = true
        defer { isLoading = false }
        
        do {
            let response = try await privateImageGrantService.getGrantedMember(
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

    func revolke(memberId: Int64) async -> Result<Void, Error> {
        do {
            try await privateImageGrantService.revoke(memberId: memberId)
            members.removeAll { $0.memberId == memberId }
            return .success(())
        } catch {
            return .failure(error)
        }
    }
}
