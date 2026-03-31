import SwiftUI
import Kingfisher

struct MemberProfileImage: View {

    let images: [URL]

    var body: some View {
        TabView {
            if images.isEmpty {
                Image(systemName: "person.fill")
                    .resizable()
                    .scaledToFit()
                    .padding(100)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .foregroundColor(Color(.systemGray3))
                    .background(Color(.systemGray6))
            } else {
                ForEach(Array(images.enumerated()), id: \.offset) { i, url in
                    NavigationLink {
                        ImageFullCoverTabView(images: images, startIndex: i)
                    } label: {
                        KFImage(url)
                            .resizable()
                            .placeholder {
                                ProgressView()
                                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                                    .background(Color(.systemGray6))
                            }
                            .aspectRatio(contentMode: .fill)
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
                            .background(.black)
                            .clipped()
                            .tag(i)
                    }
                }
            }
        }
        .tabViewStyle(PageTabViewStyle())
        .aspectRatio(4/3, contentMode: .fit)
        .clipped()
    }
}
