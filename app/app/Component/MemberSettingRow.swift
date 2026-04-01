import SwiftUI
import Kingfisher

struct MemberSettingRow: View {

    let profileUrl: String?
    let nickname: String
    let createdAt: String
    let gender: String
    let age: Int
    let onDelete: () -> Void

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
                Text(nickname)
                    .foregroundColor(.primary)
                    .font(.default.bold())

                VStack(alignment: .leading) {
                    HStack {
                        Text(gender == "MALE" ? "남자" : "여자")
                        Text("·")
                        Text("\(age)살")
                    }

                    Text(createdAt.dateLabel)
                        .foregroundColor(.gray)
                        .font(.footnote)
                }
                .foregroundColor(.gray)
                .font(.footnote)
            }

            Spacer()

            Button {
                onDelete()
            } label: {
                Image(systemName: "trash.fill")
                    .font(.default)
                    .frame(width: 40, height: 40)
                    .foregroundColor(.white)
                    .background(.red, in: Circle())
            }
        }
        .padding(.horizontal)
        .padding(.vertical, 5)
    }
}
