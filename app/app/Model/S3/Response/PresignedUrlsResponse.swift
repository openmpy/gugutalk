struct PresignedUrlsResponse: Codable {

    let presigned: [PresignedUrlResponse]
}

struct PresignedUrlResponse: Codable {

    let url: String
    let key: String
}

