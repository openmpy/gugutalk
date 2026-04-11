struct CursorSimilarityResponse<T: Decodable>: Decodable {

    let payload: [T]
    let nextId: Int64?
    let nextSimilarity: Double?
    let hasNext: Bool
}
