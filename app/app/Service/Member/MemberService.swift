import Alamofire

final class MemberService {

    static let shared = MemberService()

    let session = Session(interceptor: AuthInterceptor())
    let baseURL = "http://192.168.0.15:8080/api"

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

    func bump() async throws {
        let url = "\(baseURL)/v1/members/me/bump"

        try await session.request(
            url,
            method: .put
        )
        .validateWithErrorHandling()
    }
}
