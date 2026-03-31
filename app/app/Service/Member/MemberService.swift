import Alamofire

final class MemberService {

    static let shared = MemberService()

    let session = Session(interceptor: AuthInterceptor())
    let baseURL = "http://192.168.0.15:8080/api"

    func getPresignedUrls(
        images: [MemberGetPresignedUrlRequest]
    ) async throws -> PresignedUrlsResponse {
        let url = "\(baseURL)/v1/members/images/presigned"
        let body = MemberGetPresignedUrlsRequest(images: images)

        return try await session.request(
            url,
            method: .post,
            parameters: body,
            encoder: JSONParameterEncoder.default
        )
        .decodingWithErrorHandling(PresignedUrlsResponse.self)
    }
}
