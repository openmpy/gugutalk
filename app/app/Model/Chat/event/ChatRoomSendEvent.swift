struct ChatRoomSendEvent: Codable {

    let chatRoomId: Int64
    let senderId: Int64
    let profileUrl: String?
    let nickname: String
    let lastMessage: String?
    let type: String
    let lastMessageAt: String?
    let unreadCount: Int
}
