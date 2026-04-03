import SwiftUI
import Kingfisher
import AVKit
import Photos

struct MessageBubble: View {

    let isMe: Bool
    let content: String
    let createdAt: String
    let type: String

    @Binding var playingVideoURL: URL?

    @State private var isDownloading: Bool = false

    var body: some View {
        if isMe == false {
            HStack(alignment: .bottom, spacing: 3) {
                bubbleContent
                    .foregroundColor(.primary)
                    .background(type == "TEXT" ? Color(.systemGray6) : Color.clear)
                    .clipShape(RoundedRectangle(cornerRadius: type == "TEXT" ? 20 : 12))

                if type == "IMAGE" || type == "VIDEO" {
                    downloadButton
                }

                Text(createdAt.ampmTime)
                    .font(.caption2)
                    .foregroundColor(.gray)

                Spacer()
            }
            .rotationEffect(.degrees(180))
        } else {
            HStack(alignment: .bottom, spacing: 3) {
                Spacer()

                Text(createdAt.ampmTime)
                    .font(.caption2)
                    .foregroundColor(.gray)

                if type == "IMAGE" || type == "VIDEO" {
                    downloadButton
                }

                bubbleContent
                    .foregroundColor(.white)
                    .background(type == "TEXT" ? Color.blue : Color.clear)
                    .clipShape(RoundedRectangle(cornerRadius: type == "TEXT" ? 20 : 12))
            }
            .rotationEffect(.degrees(180))
        }
    }

    // MARK: - Download Button

    private var downloadButton: some View {
        Button {
            guard !isDownloading else { return }

            downloadContent()
        } label: {
            if isDownloading {
                ProgressView()
                    .frame(width: 22, height: 22)
            } else {
                Image(systemName: "square.and.arrow.up.circle.fill")
                    .font(.title3)
                    .foregroundColor(Color(.systemGray3))
            }
        }
    }

    // MARK: - Download Logic

    private func downloadContent() {
        guard let url = URL(string: content) else { return }
        isDownloading = true

        if type == "IMAGE" {
            KingfisherManager.shared.retrieveImage(with: url) { result in
                DispatchQueue.main.async {
                    isDownloading = false
                    if case let .success(value) = result {
                        UIImageWriteToSavedPhotosAlbum(value.image, nil, nil, nil)
                    }
                }
            }
        } else if type == "VIDEO" {
            Task {
                await saveVideo(from: url)
                await MainActor.run { isDownloading = false }
            }
        }
    }

    private func saveVideo(from url: URL) async {
        do {
            let (tempURL, _) = try await URLSession.shared.download(from: url)

            let destURL = FileManager.default.temporaryDirectory
                .appendingPathComponent(UUID().uuidString)
                .appendingPathExtension("mp4")

            try FileManager.default.moveItem(at: tempURL, to: destURL)

            let status = await PHPhotoLibrary.requestAuthorization(for: .addOnly)
            guard status == .authorized || status == .limited else { return }

            try await PHPhotoLibrary.shared().performChanges {
                PHAssetChangeRequest.creationRequestForAssetFromVideo(atFileURL: destURL)
            }
        } catch {
            print("Video save failed: \(error)")
        }
    }

    // MARK: - Bubble Content

    @ViewBuilder
    private var bubbleContent: some View {
        switch type {
        case "IMAGE":
            KFImage(URL(string: content))
                .resizable()
                .placeholder {
                    ProgressView()
                        .frame(width: 200, height: 200)
                        .background(Color(.systemGray6))
                }
                .scaledToFill()
                .frame(width: 200, height: 200)
                .clipped()

        case "VIDEO":
            VideoThumbnailView(key: content, playingVideoURL: $playingVideoURL)
                .frame(width: 200, height: 200)

        default:
            Text(content)
                .font(.subheadline)
                .padding(.horizontal, 10)
                .padding(.vertical, 8)
        }
    }
}
