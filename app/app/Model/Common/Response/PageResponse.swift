struct PageResponse<T: Decodable>: Decodable {

    let payload: [T]
    let page: Int
    let hasNext: Bool
}
