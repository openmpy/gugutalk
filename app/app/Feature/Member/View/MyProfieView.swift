import SwiftUI

struct MyProfieView: View {
    
    var body: some View {
        VStack {
            ScrollView {
                MemberProfileImage()

                MemberProfileInfo(
                    nickname: "닉네임",
                    updatedAt: nil,
                    gender: "MALE",
                    age: 20,
                    bio: "자기소개",
                    likes: 100,
                    distance: nil
                )
            }
        }
        .navigationTitle("내 프로필")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                NavigationLink {
                    ProfileEditorView()
                } label: {
                    Text("편집")
                        .font(.subheadline)
                        .foregroundColor(.primary)
                }
            }
        }
    }
}

#Preview {
    MyProfieView()
}
