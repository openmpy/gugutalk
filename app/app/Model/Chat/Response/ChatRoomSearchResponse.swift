struct ChatRoomSearchResponse: Codable, Identifiable {

    let chatRoomId: Int64
    let targetId: Int64
    let nickname: String
    let profileUrl: String?
    let lastMessage: String?
    let sortAt: String
    let unreadCount: Int
    let similarityScore: Double

    var id: Int64 { chatRoomId }
}
