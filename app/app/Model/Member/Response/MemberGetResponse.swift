struct MemberGetResponse: Codable {

    let memberId: Int64
    let images: [MemberImageResponse]
    let nickname: String
    let gender: String
    let age: Int
    let bio: String?
    var likes: Int64
    let distance: Double?
    let updatedAt: String
    let isLiked: Bool
    let isBlocked: Bool
    let isPrivateImageGranted: Bool
    let isPrivateImageGrantedByTarget: Bool
}
