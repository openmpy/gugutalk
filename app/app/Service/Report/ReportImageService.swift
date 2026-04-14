import Alamofire

final class ReportImageService {

    static let shared = ReportImageService()

    let baseURL = "http://192.168.0.15:8080/api"

    func getPresignedUrls(
        images: [ReportGetPresignedUrlRequest]
    ) async throws -> PresignedUrlsResponse {
        let url = "\(baseURL)/v1/reports/images/presigned"
        let body = ReportGetPresignedUrlsRequest(images: images)

        return try await APISession.authenticated.request(
            url,
            method: .post,
            parameters: body,
            encoder: JSONParameterEncoder.default
        )
        .decodingWithErrorHandling(PresignedUrlsResponse.self)
    }
}
