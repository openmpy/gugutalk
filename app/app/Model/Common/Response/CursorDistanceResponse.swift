struct CursorDistanceResponse<T: Decodable>: Decodable {

    let payload: [T]
    let nextId: Int64?
    let nextDistance: Double?
    let hasNext: Bool
}
