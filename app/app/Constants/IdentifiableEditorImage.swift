import SwiftUI

struct IdentifiableEditorImage: Identifiable {

    let id = UUID()
    let imageId: Int64?
    let image: UIImage?
    let url: String?
    
    init(image: UIImage) {
        self.imageId = nil
        self.image = image
        self.url = nil
    }

    init(from response: MemberImageResponse) {
        self.imageId = response.imageId
        self.image = nil
        self.url = response.url
    }
}
