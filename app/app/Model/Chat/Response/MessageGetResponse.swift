struct MessageGetResponse: Codable, Identifiable {

    let messageId: Int64
    let chatRoomId: Int64
    let senderId: Int64
    let targetId: Int64
    let content: String
    let type: String
    let createdAt: String

    var id: Int64 { messageId }
}
