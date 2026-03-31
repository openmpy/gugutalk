import SwiftUI
import Kingfisher

struct MemberRow: View {

    let profileUrl: String?
    let nickname: String
    let updatedAt: String
    let content: String
    let gender: String
    let age: Int
    let likes: Int
    let distance: Double?

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

            VStack(alignment: .leading) {
                HStack{
                    Text(nickname)
                        .foregroundColor(.primary)
                        .font(.default.bold())

                    Spacer()

                    Text(updatedAt.relativeTime)
                        .foregroundColor(.gray)
                        .font(.footnote)
                }

                Text(content)
                    .foregroundColor(.gray)
                    .font(.subheadline)
                    .lineLimit(1)

                HStack {
                    HStack {
                        Text(gender == "MALE" ? "남자" : "여자")
                        Text("·")
                        Text("\(age)살")
                        Text("·")
                        Text("♥ \(likes)")
                    }

                    Spacer()

                    if let distance = distance {
                        Text(String(format: "%.1f", distance) + "km")
                    }
                }
                .foregroundColor(.gray)
                .font(.footnote)
            }
        }
        .padding(.horizontal)
        .padding(.vertical, 5)
    }
}
