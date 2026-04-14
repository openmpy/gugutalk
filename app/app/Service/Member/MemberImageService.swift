import Alamofire

final class MemberImageService {

    static let shared = MemberImageService()

    let baseURL = "http://192.168.0.15:8080/api"

    func getPresignedUrls(
        images: [MemberGetPresignedUrlRequest]
    ) async throws -> PresignedUrlsResponse {
        let url = "\(baseURL)/v1/members/images/presigned"
        let body = MemberGetPresignedUrlsRequest(images: images)

        return try await APISession.authenticated.request(
            url,
            method: .post,
            parameters: body,
            encoder: JSONParameterEncoder.default
        )
        .decodingWithErrorHandling(PresignedUrlsResponse.self)
    }

    func getPrivateImages(
        granterId: Int64
    ) async throws -> MemberGetPrivateImagesResponse {
        let url = "\(baseURL)/v1/members/private-images/\(granterId)"

        return try await APISession.authenticated.request(
            url,
            method: .get,
        )
        .decodingWithErrorHandling(MemberGetPrivateImagesResponse.self)
    }
}
