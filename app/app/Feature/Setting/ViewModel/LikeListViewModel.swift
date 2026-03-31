import SwiftUI
import Combine

@MainActor
final class LikeListViewModel: ObservableObject {
    
    private let socialService = SocialService.shared
    
    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true
    @Published var members: [SettingResponse] = []
    
    private var cursorId: Int64?
    private var cursorDateAt: String?
    
    func getLikedMember() async -> Result<Void, Error> {
        hasNext = true

        guard !isLoading else { return .failure(CancellationError()) }
        guard hasNext else { return .success(()) }

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
            return .success(())
        } catch {
            return .failure(error)
        }
    }
    
    func loadMoreLikedMember() async -> Result<Void, Error> {
        guard !isLoading else { return .failure(CancellationError()) }
        guard hasNext else { return .success(()) }

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
            return .success(())
        } catch {
            return .failure(error)
        }
    }
}
