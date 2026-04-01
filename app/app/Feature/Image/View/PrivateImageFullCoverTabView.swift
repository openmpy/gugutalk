import SwiftUI
import Kingfisher
import Zoomable

struct PrivateImageFullCoverTabView: View {

    let images: [URL]

    @State private var currentIndex: Int

    init(images: [URL]) {
        self.images = images
        self._currentIndex = State(initialValue: 0)
    }

    var body: some View {
        ZStack(alignment: .topTrailing) {
            Color.black.ignoresSafeArea()

            if images.isEmpty {
                Text("이미지가 없습니다.")
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .multilineTextAlignment(.center)
            } else {
                SecureView {
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
    }
}
