struct LoginResponse: Codable {

    let memberId: Int64
    let accessToken: String
    let refreshToken: String
}
