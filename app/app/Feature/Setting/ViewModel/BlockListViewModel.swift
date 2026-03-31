import SwiftUI
import Combine

@MainActor
final class BlockListViewModel: ObservableObject {
    
    private let socialService = SocialService.shared
    
    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true
    @Published var members: [SettingResponse] = []
    
    private var cursorId: Int64?
    private var cursorDateAt: String?
    
    func getBlockedMember() async -> Result<Void, Error> {
        hasNext = true

        guard !isLoading else { return .failure(CancellationError()) }
        guard hasNext else { return .success(()) }

        isLoading = true
        defer { isLoading = false }
        
        do {
            let response = try await socialService.getBlockedMember(
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
    
    func loadMoreBlockedMember() async -> Result<Void, Error> {
        guard !isLoading else { return .failure(CancellationError()) }
        guard hasNext else { return .success(()) }

        isLoading = true
        defer { isLoading = false }
        
        do {
            let response = try await socialService.getBlockedMember(
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
}
