import Alamofire

final class ChatRoomService {

    static let shared = ChatRoomService()

    let session = Session(interceptor: AuthInterceptor())
    let baseURL = "http://192.168.0.15:8080/api"

    func createDirectRoom(
        targetId: Int64
    ) async throws -> ChatRoomCreateResponse {
        let url = "\(baseURL)/v1/chat-rooms?targetId=\(targetId)"

        return try await session.request(
            url,
            method: .post
        )
        .decodingWithErrorHandling(ChatRoomCreateResponse.self)
    }

    func gets(
        cursorId: Int64?,
        cursorDateAt: String?,
        size: Int = 20
    ) async throws -> CursorResponse<ChatRoomGetResponse> {
        let url = "\(baseURL)/v1/chat-rooms"

        var params: Parameters = [
            "size": size
        ]
        if cursorId != nil && cursorDateAt != nil {
            params["cursorId"] = cursorId
            params["cursorDate"] = cursorDateAt
        }

        return try await session.request(
            url,
            method: .get,
            parameters: params.compactMapValues { $0 }
        )
        .decodingWithErrorHandling(CursorResponse<ChatRoomGetResponse>.self)
    }
}
