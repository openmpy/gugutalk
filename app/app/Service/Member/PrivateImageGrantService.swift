import Alamofire

final class PrivateImageGrantService {

    static let shared = PrivateImageGrantService()

    let session = Session(interceptor: AuthInterceptor())
    let baseURL = "http://192.168.0.15:8080/api"

    func getGrantedMember(
        cursorId: Int64?,
        cursorDateAt: String?,
        size: Int = 20
    ) async throws -> CursorResponse<SettingResponse> {
        let url = "\(baseURL)/v1/members/private-image-grants"

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
        .decodingWithErrorHandling(CursorResponse<SettingResponse>.self)
    }
}
