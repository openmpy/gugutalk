import Alamofire

final class ChatRoomService {

    static let shared = ChatRoomService()

    let session = Session(interceptor: AuthInterceptor())
    let baseURL = "http://192.168.0.15:8080/api"

    func create(
        targetId: Int64
    ) async throws -> ChatRoomCreateResponse {
        let url = "\(baseURL)/v1/chat-rooms?targetId=\(targetId)"

        return try await session.request(
            url,
            method: .post
        )
        .decodingWithErrorHandling(ChatRoomCreateResponse.self)
    }
}
