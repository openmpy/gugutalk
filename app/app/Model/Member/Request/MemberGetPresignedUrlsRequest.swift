struct MemberGetPresignedUrlsRequest: Codable {

    let images: [MemberGetPresignedUrlRequest]
}

struct MemberGetPresignedUrlRequest: Codable {

    let imageType: String
    let contentType: String
}
