import Alamofire

final class LocationService {

    static let shared = LocationService()

    let session = Session(interceptor: AuthInterceptor())
    let baseURL = "http://192.168.0.15:8080/api"

    func getLocationMembers(
        gender: String = "ALL",
        page: Int = 0,
        size: Int = 20
    ) async throws -> PageResponse<MemberDiscoveryResponse> {
        let url = "\(baseURL)/v1/discovery/location"

        let params: Parameters = [
            "gender": gender,
            "page": page,
            "size": size
        ]

        return try await session.request(
            url,
            method: .get,
            parameters: params.compactMapValues { $0 }
        )
        .decodingWithErrorHandling(PageResponse<MemberDiscoveryResponse>.self)
    }
}
