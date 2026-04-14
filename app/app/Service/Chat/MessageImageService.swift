import Alamofire

final class MessageImageService {

    static let shared = MessageImageService()

    let baseURL = "http://192.168.0.15:8080/api"

    func getPresignedUrls(
        chatRoomId: Int64,
        medias: [MessageGetPresignedUrlRequest]
    ) async throws -> PresignedUrlsResponse {
        let url = "\(baseURL)/v1/chat-rooms/\(chatRoomId)/presigned"
        let body = MessageGetPresignedUrlsRequest(medias: medias)

        return try await APISession.authenticated.request(
            url,
            method: .post,
            parameters: body,
            encoder: JSONParameterEncoder.default
        )
        .decodingWithErrorHandling(PresignedUrlsResponse.self)
    }
}
