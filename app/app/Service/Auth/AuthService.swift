import Alamofire

final class AuthService {
    
    static let shared = AuthService()
    
    let session = Session(interceptor: AuthInterceptor())
    let baseURL = "http://192.168.0.15:8080/api"
    
    func sendCodeVerificationCode(
        phoneNumber: String
    ) async throws {
        let url = "\(baseURL)/v1/auth/phone/send?phoneNumber=\(phoneNumber)"
        
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
        let url = "\(baseURL)/v1/auth/signup"
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
    
    func activate(
        images: [ActivateImageRequest],
        nickname: String,
        birthYear: Int,
        bio: String?
    ) async throws {
        let url = "\(baseURL)/v1/auth/activate"
        let body = ActivateRequest(
            images: images,
            nickname: nickname,
            birthYear: birthYear,
            bio: bio
        )
        
        return try await session.request(
            url,
            method: .put,
            parameters: body,
            encoder: JSONParameterEncoder.default
        )
        .validateWithErrorHandling()
    }
    
    func validate(
        nickname: String,
        birthYear: Int
    ) async throws {
        let url = "\(baseURL)/v1/auth/validate"
        let body = ValidateRequest(
            nickname: nickname,
            birthYear: birthYear
        )
        
        return try await session.request(
            url,
            method: .post,
            parameters: body,
            encoder: JSONParameterEncoder.default
        )
        .validateWithErrorHandling()
    }
    
    func login(
        phoneNumber: String,
        password: String
    ) async throws -> LoginResponse {
        let url = "\(baseURL)/v1/auth/login"
        let body = LoginRequest(
            phoneNumber: phoneNumber,
            password: password
        )
        
        return try await AF.request(
            url,
            method: .post,
            parameters: body,
            encoder: JSONParameterEncoder.default
        )
        .decodingWithErrorHandling(LoginResponse.self)
    }
    
    func logout(
        refreshToken: String
    ) async throws {
        let url = "\(baseURL)/v1/auth/logout?refreshToken=\(refreshToken)"
        
        try await session.request(
            url,
            method: .delete
        )
        .validateWithErrorHandling()
    }

    func rotateToken(
        memberId: Int64,
        refreshToken: String
    ) async throws -> RotateTokenResponse {
        let url = "\(baseURL)/v1/auth/rotation"
        let body = RotateTokenRequest(
            memberId: memberId,
            refreshToken: refreshToken
        )

        return try await AF.request(
            url,
            method: .post,
            parameters: body,
            encoder: JSONParameterEncoder.default
        )
        .decodingWithErrorHandling(RotateTokenResponse.self)
    }
}
