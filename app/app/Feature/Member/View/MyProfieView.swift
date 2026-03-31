import SwiftUI

struct MyProfieView: View {

    @StateObject private var vm = MyProfileViewModel()

    var body: some View {
        VStack {
            ScrollView {
                if let member = vm.member {
                    MemberProfileImage(images: member.images.compactMap { URL(string: $0.url) })

                    MemberProfileInfo(
                        nickname: member.nickname,
                        updatedAt: nil,
                        gender: member.gender,
                        age: member.age,
                        bio: member.bio ?? "",
                        likes: member.likes,
                        distance: nil
                    )
                } else {
                    Text("로딩")
                }
            }
        }
        .task {
            await vm.getMe()
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
