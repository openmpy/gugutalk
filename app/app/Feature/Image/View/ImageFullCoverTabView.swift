import SwiftUI
import Kingfisher
import Zoomable

struct ImageFullCoverTabView: View {

    let images: [URL]
    let startIndex: Int

    @State private var currentIndex: Int

    init(images: [URL], startIndex: Int) {
        self.images = images
        self.startIndex = startIndex
        self._currentIndex = State(initialValue: startIndex)
    }

    var body: some View {
        ZStack(alignment: .topTrailing) {
            Color.black.ignoresSafeArea()

            TabView(selection: $currentIndex) {
                ForEach(images.indices, id: \.self) { index in
                    KFImage(images[index])
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
            .tabViewStyle(PageTabViewStyle())
        }
    }
}
