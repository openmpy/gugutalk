struct ReportGetPresignedUrlsRequest: Codable {

    let images: [ReportGetPresignedUrlRequest]
}

struct ReportGetPresignedUrlRequest: Codable {

    let contentType: String
}
