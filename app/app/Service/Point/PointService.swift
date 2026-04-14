import Alamofire

final class PointService {

    static let shared = PointService()

    let baseURL = "http://192.168.0.15:8080/api"

    func earnByAttendance() async throws {
        let url = "\(baseURL)/v1/points/attendance"

        return try await APISession.authenticated.request(
            url,
            method: .post
        )
        .validateWithErrorHandling()
    }

    func earnByAdReward() async throws {
        let url = "\(baseURL)/v1/points/ad-reward"

        return try await APISession.authenticated.request(
            url,
            method: .post
        )
        .validateWithErrorHandling()
    }

    func get() async throws -> PointGetResponse {
        let url = "\(baseURL)/v1/points"

        return try await APISession.authenticated.request(
            url,
            method: .get
        )
        .decodingWithErrorHandling(PointGetResponse.self)
    }
}
