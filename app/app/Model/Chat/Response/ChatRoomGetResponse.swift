struct ChatRoomGetResponse: Codable, Identifiable {

    let chatRoomId: Int64
    let nickname: String
    let profileUrl: String?
    let lastMessage: String?
    let lastMessageAt: String?
    let sortAt: String

    var id: Int64 { chatRoomId }
}
