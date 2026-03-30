import Alamofire

final class AuthService {

    static let shared = AuthService()

    let session = Session(interceptor: AuthInterceptor())

    func sendCodeVerificationCode(
        phoneNumber: String
    ) async throws {
        let url = "http://192.168.0.15:8080/api/v1/auth/phone/send?phoneNumber=\(phoneNumber)"

        return try await AF.request(
            url,
            method: .post
        )
        .validateWithErrorHandling()
    }

    func signup(
        uuid: String,
        phoneNumber: String,
        verificationCode: String,
        password: String,
        gender: String
    ) async throws -> SignupResponse {
        let url = "http://192.168.0.15:8080/api/v1/auth/signup"
        let body = SignupRequest(
            uuid: uuid,
            phoneNumber: phoneNumber,
            verificationCode: verificationCode,
            password: password,
            gender: gender
        )

        return try await AF.request(
            url,
            method: .post,
            parameters: body,
            encoder: JSONParameterEncoder.default
        )
        .decodingWithErrorHandling(SignupResponse.self)
    }
}
