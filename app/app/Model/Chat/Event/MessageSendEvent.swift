struct MessageSendEvent: Codable {
    
    let messageId: Int64
    let senderId: Int64
    let content: String
    let type: String
    let createdAt: String
}
