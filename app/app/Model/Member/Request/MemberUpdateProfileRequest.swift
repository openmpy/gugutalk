struct MemberUpdateProfileRequest: Encodable {

    let publicImages: [ProfileImageUpdate]
    let privateImages: [ProfileImageUpdate]
    let nickname: String
    let birthYear: Int
    let bio: String?
}

struct ProfileImageUpdate: Encodable {

    let imageId: Int64?
    let key: String?
    let sortOrder: Int
}
