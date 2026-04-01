import Alamofire

final class MemberService {

    static let shared = MemberService()

    let session = Session(interceptor: AuthInterceptor())
    let baseURL = "http://192.168.0.15:8080/api"

    func getMember(memberId: Int64) async throws -> MemberGetResponse {
        let url = "\(baseURL)/v1/members/\(memberId)"

        return try await session.request(
            url,
            method: .get
        )
        .decodingWithErrorHandling(MemberGetResponse.self)
    }

    func getMe() async throws -> MemberGetMeResponse {
        let url = "\(baseURL)/v1/members/me"

        return try await session.request(
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

        try await session.request(
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

        try await session.request(
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

        try await session.request(
            url,
            method: .put,
            parameters: body,
            encoder: JSONParameterEncoder.default
        )
        .validateWithErrorHandling()
    }

    func search(
        keyword: String,
        cursorId: Int64?,
        size: Int = 20
    ) async throws -> CursorResponse<MemberDiscoveryResponse> {
        let url = "\(baseURL)/v1/members/search"

        var params: Parameters = [
            "keyword": keyword,
            "size": size
        ]
        if let cursorId {
            params["cursorId"] = cursorId
        }

        return try await session.request(
            url,
            method: .get,
            parameters: params
        )
        .decodingWithErrorHandling(CursorResponse<MemberDiscoveryResponse>.self)
    }
}
