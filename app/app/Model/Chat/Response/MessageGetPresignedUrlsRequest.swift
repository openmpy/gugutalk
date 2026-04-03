struct MessageGetPresignedUrlsRequest: Codable {

    let medias: [MessageGetPresignedUrlRequest]
}

struct MessageGetPresignedUrlRequest: Codable {

    let contentType: String
}
