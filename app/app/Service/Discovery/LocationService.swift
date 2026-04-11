import Alamofire

final class LocationService {

    static let shared = LocationService()

    let session = Session(interceptor: AuthInterceptor())
    let baseURL = "http://192.168.0.15:8080/api"

    func getLocationMembers(
        gender: String = "ALL",
        cursorId: Int64?,
        cursorDistance: Double?,
        size: Int = 20
    ) async throws -> CursorDistanceResponse<MemberDiscoveryResponse> {
        let url = "\(baseURL)/v1/discovery/location"

        var params: Parameters = [
            "gender": gender,
            "size": size
        ]
        if cursorId != nil && cursorDistance != nil {
            params["cursorId"] = cursorId
            params["cursorDistance"] = cursorDistance
        }

        return try await session.request(
            url,
            method: .get,
            parameters: params.compactMapValues { $0 }
        )
        .decodingWithErrorHandling(CursorDistanceResponse<MemberDiscoveryResponse>.self)
    }
}
