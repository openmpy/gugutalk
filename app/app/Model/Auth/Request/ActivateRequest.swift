struct ActivateRequest: Codable {

    let images: [ActivateImageRequest]
    let nickname: String
    let birthYear: Int
    let bio: String?
}

struct ActivateImageRequest: Codable {

    let index: Int
    let key: String
}
