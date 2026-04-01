import Alamofire

final class ReportService {

    static let shared = ReportService()

    let session = Session(interceptor: AuthInterceptor())
    let baseURL = "http://192.168.0.15:8080/api"

    func create(
        reportedId: Int64,
        images: [ReportImageRequest],
        type: String,
        reason: String?
    ) async throws {
        let url = "\(baseURL)/v1/reports/\(reportedId)"
        let body = ReportCreateRequest(
            images: images,
            type: type,
            reason: reason
        )

        return try await session.request(
            url,
            method: .put,
            parameters: body,
            encoder: JSONParameterEncoder.default
        )
        .validateWithErrorHandling()
    }
}
