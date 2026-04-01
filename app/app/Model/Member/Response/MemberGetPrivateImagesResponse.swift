struct MemberGetPrivateImagesResponse: Codable {
    
    let images: [MemberPrivateImageResponse]
}

struct MemberPrivateImageResponse: Codable {
    
    let url: String
}

