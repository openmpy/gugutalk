import SwiftUI

struct MemberProfileInfo: View {
    
    let nickname: String
    let updatedAt: String?
    let gender: String
    let age: Int
    let bio: String
    let likes: Int
    let distance: Double?
    
    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            HStack {
                Text(nickname)
                    .font(.title.bold())
                    .foregroundColor(.primary)
                
                Spacer()

                if let updatedAt = updatedAt {
                    Text(updatedAt.relativeTime)
                        .font(.default)
                        .foregroundColor(.gray)
                }
            }
            
            HStack {
                Text(gender == "MALE" ? "남자" : "여자")
                Text("·")
                Text("\(age)살")
                Text("·")
                Text("♥ \(likes)")
                
                Spacer()
                
                if let distance = distance {
                    Text(String(format: "%.1f", distance) + "km")
                }
            }
            .font(.default)
            .foregroundColor(.gray)
            .padding(.bottom)
            
            Text(bio)
                .foregroundColor(.primary)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding()
                .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
        }
        .padding()
    }
}
