import SwiftUI
import AVKit

struct VideoThumbnailView: View {

    let key: String

    @Binding var playingVideoURL: URL?

    @State private var thumbnail: UIImage? = nil

    private var videoURL: URL? {
        URL(string: key)
    }

    var body: some View {
        ZStack {
            if let thumbnail {
                Image(uiImage: thumbnail)
                    .resizable()
                    .scaledToFill()
                    .frame(width: 200, height: 200)
                    .clipped()
            } else {
                Color(.systemGray6)
                    .frame(width: 200, height: 200)
            }

            Image(systemName: "play.circle.fill")
                .font(.system(size: 44))
                .foregroundColor(.white)
        }
        .onTapGesture {
            playingVideoURL = videoURL
        }
        .task {
            thumbnail = await generateThumbnail()
        }
    }

    private func generateThumbnail() async -> UIImage? {
        guard let url = videoURL else { return nil }

        let asset = AVURLAsset(url: url)
        let generator = AVAssetImageGenerator(asset: asset)
        generator.appliesPreferredTrackTransform = true

        guard let cgImage = try? await generator.image(at: .zero).image else { return nil }
        return UIImage(cgImage: cgImage)
    }
}
