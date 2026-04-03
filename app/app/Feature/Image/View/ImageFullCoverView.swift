import SwiftUI
import Kingfisher
import Zoomable

struct ImageFullCoverView: View {

    let image: URL

    var body: some View {
        ZStack(alignment: .topTrailing) {
            Color.black.ignoresSafeArea()
            
            KFImage(image)
                .resizable()
                .placeholder {
                    ProgressView()
                        .tint(Color(.systemGray3))
                }
                .aspectRatio(contentMode: .fit)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .zoomable()
        }
    }
}
