import Alamofire

final class FcmService {

    static let shared = FcmService()

    let baseURL = "http://192.168.0.15:8080/api"

    func register(
        token: String,
        uuid: String,
        memberId: Int64?,
    ) async throws {
        let url = "\(baseURL)/v1/fcm/token"
        let body = FcmTokenRegisterRequest(token: token, uuid: uuid, memberId: memberId)

        try await APISession.plain.request(
            url,
            method: .post,
            parameters: body,
            encoder: JSONParameterEncoder.default
        )
        .validateWithErrorHandling()
    }

    func inactive(uuid: String) async throws {
        let url = "\(baseURL)/v1/fcm/inactive?uuid=\(uuid)"

        try await APISession.plain.request(
            url,
            method: .put,
        )
        .validateWithErrorHandling()
    }
}
