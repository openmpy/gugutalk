import SwiftUI
import Toasts

struct MyProfieView: View {

    @StateObject private var vm = MyProfileViewModel()

    @Environment(\.presentToast) var presentToast

    var body: some View {
        VStack {
            if let member = vm.member {
                ScrollView {
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
                }
            } else {
                ProgressView()
            }
        }
        .task {
            let result = await vm.getMe()
            if case .failure(let error) = result {
                presentToast(ToastValue(
                    icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                    message: error.localizedDescription
                ))
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
