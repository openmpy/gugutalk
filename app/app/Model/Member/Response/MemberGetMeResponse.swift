struct MemberGetMeResponse: Codable {

    let memberId: Int64
    let images: [MemberImageResponse]
    let nickname: String
    let gender: String
    let age: Int
    let bio: String?
    let likes: Int
}
