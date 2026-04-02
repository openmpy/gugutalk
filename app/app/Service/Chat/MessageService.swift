import Alamofire

final class MessageService {

    static let shared = MessageService()

    let session = Session(interceptor: AuthInterceptor())
    let baseURL = "http://192.168.0.15:8080/api"

    func gets(
        chatRoomId: Int64,
        cursorId: Int64?,
        cursorDateAt: String?,
        size: Int = 50
    ) async throws -> CursorResponse<MessageGetResponse> {
        let url = "\(baseURL)/v1/chat-rooms/\(chatRoomId)/messages"

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
        .decodingWithErrorHandling(CursorResponse<MessageGetResponse>.self)
    }
}
