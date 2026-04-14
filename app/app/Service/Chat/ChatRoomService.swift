import Alamofire

final class ChatRoomService {

    static let shared = ChatRoomService()

    let baseURL = "http://192.168.0.15:8080/api"

    func create(
        targetId: Int64
    ) async throws -> ChatRoomCreateResponse {
        let url = "\(baseURL)/v1/chat-rooms?targetId=\(targetId)"

        return try await APISession.authenticated.request(
            url,
            method: .post
        )
        .decodingWithErrorHandling(ChatRoomCreateResponse.self)
    }

    func delete(
        chatRoomId: Int64
    ) async throws {
        let url = "\(baseURL)/v1/chat-rooms/\(chatRoomId)"

        return try await APISession.authenticated.request(
            url,
            method: .delete
        )
        .validateWithErrorHandling()
    }

    func gets(
        status: String = "ALL",
        cursorId: Int64?,
        cursorDateAt: String?,
        size: Int = 20
    ) async throws -> CursorResponse<ChatRoomGetResponse> {
        let url = "\(baseURL)/v1/chat-rooms"

        var params: Parameters = [
            "status": status,
            "size": size
        ]
        if cursorId != nil && cursorDateAt != nil {
            params["cursorId"] = cursorId
            params["cursorDate"] = cursorDateAt
        }

        return try await APISession.authenticated.request(
            url,
            method: .get,
            parameters: params.compactMapValues { $0 }
        )
        .decodingWithErrorHandling(CursorResponse<ChatRoomGetResponse>.self)
    }

    func search(
        nickname: String,
        cursorId: Int64?,
        cursorSimilarity: Double?,
        size: Int = 20
    ) async throws -> CursorSimilarityResponse<ChatRoomSearchResponse> {
        let url = "\(baseURL)/v1/chat-rooms/search"

        var params: Parameters = [
            "nickname": nickname,
            "size": size
        ]
        if cursorId != nil && cursorSimilarity != nil {
            params["cursorId"] = cursorId
            params["cursorSimilarity"] = cursorSimilarity
        }

        return try await APISession.authenticated.request(
            url,
            method: .get,
            parameters: params.compactMapValues { $0 }
        )
        .decodingWithErrorHandling(CursorSimilarityResponse<ChatRoomSearchResponse>.self)
    }

    func markAsRead(
        chatRoomId: Int64
    ) async throws {
        let url = "\(baseURL)/v1/chat-rooms/\(chatRoomId)"

        return try await APISession.authenticated.request(
            url,
            method: .put
        )
        .validateWithErrorHandling()
    }
}
