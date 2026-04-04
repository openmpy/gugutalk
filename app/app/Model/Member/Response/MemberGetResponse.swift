struct MemberGetResponse: Codable, Equatable {

    let memberId: Int64
    let images: [MemberImageResponse]
    let nickname: String
    let gender: String
    let age: Int
    let bio: String?
    var likes: Int64
    let distance: Double?
    let updatedAt: String
    let isChatEnabled: Bool
    var isLiked: Bool
    var isBlocked: Bool
    var isPrivateImageGranted: Bool
    let isPrivateImageGrantedByTarget: Bool
}
