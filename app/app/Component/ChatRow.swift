import SwiftUI
import Kingfisher

struct ChatRow: View {

    let profileUrl: String?
    let nickname: String
    let updatedAt: String
    let content: String
    let unreads: Int

    var body: some View {
        HStack(spacing: 15) {
            if let profileUrl = profileUrl {
                KFImage(URL(string: profileUrl))
                    .resizable()
                    .placeholder {
                        ProgressView()
                            .frame(width: 65, height: 65)
                            .background(Color(.systemGray6), in: Circle())
                    }
                    .aspectRatio(contentMode: .fill)
                    .frame(width: 65, height: 65)
                    .background(Color(.systemGray6), in: Circle())
                    .clipShape(Circle())
            } else {
                Image(systemName: "person.fill")
                    .font(.title)
                    .frame(width: 65, height: 65)
                    .foregroundColor(Color(.systemGray3))
                    .background(Color(.systemGray6), in: Circle())
            }

            VStack(alignment: .leading, spacing: 5) {
                HStack {
                    Text(nickname)
                        .foregroundColor(.primary)
                        .font(.default.bold())

                    Spacer()

                    Text(updatedAt.customTime)
                        .foregroundColor(.gray)
                        .font(.footnote)
                }

                HStack {
                    Text(content.byCharWrapping)
                        .foregroundColor(.gray)
                        .font(.subheadline)
                        .lineLimit(2)
                        .multilineTextAlignment(.leading)

                    Spacer()

                    if unreads > 0 {
                        Text(unreads > 99 ? "99+" : "\(unreads)")
                            .font(.caption2.bold())
                            .padding(.horizontal, 6)
                            .padding(.vertical, 2)
                            .background(Color(.systemGray2))
                            .foregroundColor(.white)
                            .clipShape(Capsule())
                    }
                }
            }
        }
        .padding(.horizontal)
        .padding(.vertical, 5)
    }
}
