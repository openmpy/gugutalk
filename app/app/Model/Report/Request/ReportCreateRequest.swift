struct ReportCreateRequest: Codable {

    let images: [ReportImageRequest]
    let type: String
    let reason: String?
}

struct ReportImageRequest: Codable {

    let index: Int
    let key: String
}
