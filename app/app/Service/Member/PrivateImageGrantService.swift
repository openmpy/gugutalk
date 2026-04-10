import Alamofire

final class PrivateImageGrantService {

    static let shared = PrivateImageGrantService()

    let session = Session(interceptor: AuthInterceptor())
    let baseURL = "http://192.168.0.15:8080/api"

    func open(memberId: Int64) async throws {
        let url = "\(baseURL)/v1/members/\(memberId)/private-image-grant"

        try await session.request(
            url,
            method: .post
        ).validateWithErrorHandling()
    }

    func close(memberId: Int64) async throws {
        let url = "\(baseURL)/v1/members/\(memberId)/private-image-grant"

        try await session.request(
            url,
            method: .delete
        ).validateWithErrorHandling()
    }

    func getGrantedMember(
        cursorId: Int64?,
        size: Int = 20
    ) async throws -> CursorResponse<SettingResponse> {
        let url = "\(baseURL)/v1/members/private-image-grants"

        var params: Parameters = [
            "size": size
        ]
        if cursorId != nil {
            params["cursorId"] = cursorId
        }

        return try await session.request(
            url,
            method: .get,
            parameters: params.compactMapValues { $0 }
        )
        .decodingWithErrorHandling(CursorResponse<SettingResponse>.self)
    }
}
