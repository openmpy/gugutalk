import SwiftUI
import Combine

@MainActor
final class BlockListViewModel: ObservableObject {
    
    private let socialService = SocialService.shared
    
    @Published var showErrorAlert: Bool = false
    @Published var errorMessage: String = ""
    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true
    
    @Published var members: [SettingResponse] = []
    
    private var cursorId: Int64?
    private var cursorDateAt: String?
    
    func getBlockedMember() async {
        hasNext = true
        
        guard !isLoading, hasNext else {
            return
        }
        
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
        } catch {
            showErrorAlert = true
            errorMessage = error.localizedDescription
        }
    }
    
    func loadMoreBlockedMember() async {
        guard !isLoading, hasNext else {
            return
        }
        
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
        } catch {
            showErrorAlert = true
            errorMessage = error.localizedDescription
        }
    }
}
