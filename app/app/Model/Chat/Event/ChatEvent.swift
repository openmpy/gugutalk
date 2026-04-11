struct ChatEvent<T: Decodable>: Decodable {
    
    let eventType: String
    let payload: T?
}
