struct FcmTokenRegisterRequest: Encodable {

    let token: String
    let uuid: String
    let memberId: Int64?
}
