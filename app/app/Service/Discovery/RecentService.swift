import Alamofire

final class RecentService {

    static let shared = RecentService()

    let session = Session(interceptor: AuthInterceptor())
    let baseURL = "http://192.168.0.15:8080/api"

    func getRecentMembers(
        gender: String = "ALL",
        cursorId: Int64?,
        cursorDateAt: String?,
        size: Int = 20
    ) async throws -> CursorResponse<MemberDiscoveryResponse> {
        let url = "\(baseURL)/v1/discovery/recent"

        var params: Parameters = [
            "gender": gender,
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
        .decodingWithErrorHandling(CursorResponse<MemberDiscoveryResponse>.self)
    }
}
