import Alamofire

final class MemberService {

    static let shared = MemberService()

    let baseURL = "http://192.168.0.15:8080/api"

    func getMember(memberId: Int64) async throws -> MemberGetResponse {
        let url = "\(baseURL)/v1/members/\(memberId)"

        return try await APISession.authenticated.request(
            url,
            method: .get
        )
        .decodingWithErrorHandling(MemberGetResponse.self)
    }

    func getMe() async throws -> MemberGetMeResponse {
        let url = "\(baseURL)/v1/members/me"

        return try await APISession.authenticated.request(
            url,
            method: .get
        )
        .decodingWithErrorHandling(MemberGetMeResponse.self)
    }

    func withdraw(
        accessToken: String,
        refreshToken: String,
    ) async throws {
        let url = "\(baseURL)/v1/members/me"
        let body = MemberWithdrawRequest(accessToken: accessToken, refreshToken: refreshToken)

        try await APISession.authenticated.request(
            url,
            method: .delete,
            parameters: body,
            encoder: JSONParameterEncoder.default
        )
        .validateWithErrorHandling()
    }

    func bump(latitude: Double?, longitude: Double?) async throws {
        let url = "\(baseURL)/v1/members/me/bump"
        let body = MemberBumpRequest(latitude: latitude, longitude: longitude)

        try await APISession.authenticated.request(
            url,
            method: .put,
            parameters: body,
            encoder: JSONParameterEncoder.default
        )
        .validateWithErrorHandling()
    }

    func updateComment(comment: String) async throws {
        let url = "\(baseURL)/v1/members/me/comment"
        let body: [String: String] = ["comment": comment]

        try await APISession.authenticated.request(
            url,
            method: .put,
            parameters: body,
            encoder: JSONParameterEncoder.default
        )
        .validateWithErrorHandling()
    }

    func updateProfile(request: MemberUpdateProfileRequest) async throws {
        let url = "\(baseURL)/v1/members/me/profile"

        try await APISession.authenticated.request(
            url,
            method: .put,
            parameters: request,
            encoder: JSONParameterEncoder.default
        )
        .validateWithErrorHandling()
    }

    func search(
        nickname: String,
        cursorId: Int64?,
        cursorSimilarity: Double?,
        size: Int = 20
    ) async throws -> CursorSimilarityResponse<MemberSearchResponse> {
        let url = "\(baseURL)/v1/members/search"

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
            parameters: params
        )
        .decodingWithErrorHandling(CursorSimilarityResponse<MemberSearchResponse>.self)
    }

    func getChatEnabled() async throws -> MemberGetChatEnabledResponse {
        let url = "\(baseURL)/v1/members/chat-enabled"

        return try await APISession.authenticated.request(
            url,
            method: .get
        )
        .decodingWithErrorHandling(MemberGetChatEnabledResponse.self)
    }

    func toggleChatEnabled() async throws -> MemberGetChatEnabledResponse {
        let url = "\(baseURL)/v1/members/chat-enabled"

        return try await APISession.authenticated.request(
            url,
            method: .put
        )
        .decodingWithErrorHandling(MemberGetChatEnabledResponse.self)
    }
}
