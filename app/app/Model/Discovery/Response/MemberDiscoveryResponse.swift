struct MemberDiscoveryResponse: Codable, Identifiable {

    let memberId: Int64
    let profileUrl: String?
    let nickname: String
    let gender: String
    let age: Int
    let comment: String?
    let distance: Double?
    let likes: Int
    let updatedAt: String

    var id: Int64 { memberId }
}
