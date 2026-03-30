import SwiftUI

struct MessageBubble: View {

    let isMe: Bool
    let content: String
    let createdAt: String

    var body: some View {
        if isMe == false {
            HStack(alignment: .bottom, spacing: 3) {
                Text(content)
                    .font(.subheadline)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 8)
                    .foregroundColor(.primary)
                    .background(Color(.systemGray6))
                    .clipShape(RoundedRectangle(cornerRadius: 20))

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

                Text(content)
                    .font(.subheadline)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 8)
                    .foregroundColor(.white)
                    .background(.blue)
                    .clipShape(RoundedRectangle(cornerRadius: 20))
            }
            .rotationEffect(.degrees(180))
        }
    }
}
