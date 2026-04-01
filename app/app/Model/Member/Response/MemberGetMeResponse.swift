struct MemberGetMeResponse: Codable {

    let memberId: Int64
    let publicImages: [MemberImageResponse]
    let privateImages: [MemberImageResponse]
    let nickname: String
    let gender: String
    let age: Int
    let birthYear: Int
    let bio: String?
    let likes: Int
}
