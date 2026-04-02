struct ChatRoomGetResponse: Codable, Identifiable {

    let chatRoomId: Int64
    let memberId: Int64
    let profileUrl: String?
    let nickname: String
    let lastMessage: String?
    let lastMessageAt: String?

    var id: Int64 { chatRoomId }
}
