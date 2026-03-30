struct SignupRequest: Codable {

    let uuid: String
    let phoneNumber: String
    let verificationCode: String
    let password: String
    let gender: String
}
