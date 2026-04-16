import SwiftUI
import Combine

@MainActor
final class NotificationRouter: ObservableObject {
    
    struct PendingChat: Equatable {
        
        let chatRoomId: Int64
        let memberId: Int64
    }
    
    static let shared = NotificationRouter()
    
    @Published var pendingChat: PendingChat?
    
    private init() {}
    
    func handle(userInfo: [AnyHashable: Any]) {
        guard userInfo["type"] as? String == "CHAT",
              let roomIdString = userInfo["chatRoomId"] as? String, let roomId = Int64(roomIdString),
              let memberIdString = userInfo["memberId"] as? String, let memberId = Int64(memberIdString)
        else { return }
        
        pendingChat = PendingChat(chatRoomId: roomId, memberId: memberId)
    }
}
