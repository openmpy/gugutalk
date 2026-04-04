import SwiftUI

struct MyProfieView: View {

    @StateObject private var vm = MyProfileViewModel()

    var body: some View {
        VStack {
            switch vm.state {

            case .idle:
                Spacer()
                EmptyView()
                Spacer()

            case .loading:
                Spacer()
                ProgressView()
                Spacer()

            case .data:
                if let member = vm.member {
                    memberSection(member: member)
                }

            case .error(let message):
                errorSection(message: message)
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

    // MARK: - SECTION

    private func memberSection(member: MemberGetMeResponse) -> some View {
        ScrollView {
            MemberProfileImage(
                images: member.publicImages.compactMap { URL(string: $0.url) }
            )

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
    }

    private func errorSection(message: String) -> some View {
        VStack {
            Spacer()

            Text(message)
                .padding(.bottom)

            Button("다시 시도") {
                Task {
                    await vm.getMe()
                }
            }

            Spacer()
        }
    }
}
