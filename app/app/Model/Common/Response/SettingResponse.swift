struct SettingResponse: Codable, Identifiable {

    let id: Int64
    let memberId: Int64
    let nickname: String
    let gender: String
    let age: Int
    let profileUrl: String?
    let createdAt: String
}
