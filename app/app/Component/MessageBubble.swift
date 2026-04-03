import SwiftUI
import Kingfisher
import AVKit

struct MessageBubble: View {

    let isMe: Bool
    let content: String
    let createdAt: String
    let type: String

    var body: some View {
        if isMe == false {
            HStack(alignment: .bottom, spacing: 3) {
                bubbleContent
                    .foregroundColor(.primary)
                    .background(type == "TEXT" ? Color(.systemGray6) : Color.clear)
                    .clipShape(RoundedRectangle(cornerRadius: type == "TEXT" ? 20 : 12))

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

                bubbleContent
                    .foregroundColor(.white)
                    .background(type == "TEXT" ? Color.blue : Color.clear)
                    .clipShape(RoundedRectangle(cornerRadius: type == "TEXT" ? 20 : 12))
            }
            .rotationEffect(.degrees(180))
        }
    }

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
            VideoThumbnailView(key: content)
                .frame(width: 200, height: 200)

        default:
            Text(content)
                .font(.subheadline)
                .padding(.horizontal, 10)
                .padding(.vertical, 8)
        }
    }
}
