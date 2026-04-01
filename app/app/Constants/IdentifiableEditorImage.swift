import SwiftUI

struct IdentifiableEditorImage: Identifiable {

    let id = UUID()
    let image: UIImage?
    let url: String?
    
    init(image: UIImage) {
        self.image = image
        self.url = nil
    }

    init(from response: MemberImageResponse) {
        self.image = nil
        self.url = response.url
    }
}
