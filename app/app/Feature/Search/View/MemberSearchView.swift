import SwiftUI
import Toasts

struct MemberSearchView: View {

    @StateObject private var vm = MemberSearchViewModel()

    @Environment(\.presentToast) var presentToast

    @State private var keyword: String = ""

    var body: some View {
        VStack {
            if vm.members.isEmpty && !keyword.isEmpty && !vm.isLoading {
                Spacer()
                Text("검색 결과가 없습니다.")
                    .foregroundColor(.secondary)
                Spacer()
            } else {
                ScrollView {
                    LazyVStack {
                        ForEach(vm.members) { member in
                            NavigationLink {
                                MemberProfileView(memberId: member.memberId)
                            } label: {
                                MemberRow(
                                    profileUrl: member.profileUrl,
                                    nickname: member.nickname,
                                    updatedAt: member.updatedAt,
                                    content: member.comment ?? "",
                                    gender: member.gender,
                                    age: member.age,
                                    likes: member.likes,
                                    distance: member.distance
                                )
                            }
                            .onAppear {
                                if member.id == vm.members.last?.id {
                                    Task {
                                        let result = await vm.loadMore(keyword: keyword)
                                        if case .failure(let error) = result {
                                            presentToast(ToastValue(
                                                icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                                                message: error.localizedDescription
                                            ))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                .onTapGesture {
                    hideKeyboard()
                }
            }
        }
        .searchable(
            text: $keyword,
            placement: .navigationBarDrawer(displayMode: .always),
            prompt: "닉네임 입력 (2자 이상)"
        )
        .onChange(of: keyword) { _, newValue in
            if newValue.isEmpty {
                vm.members = []
            }
        }
        .onSubmit(of: .search) {
            guard keyword.count >= 2 else { return }

            Task {
                let result = await vm.search(keyword: keyword)
                if case .failure(let error) = result {
                    presentToast(ToastValue(
                        icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                        message: error.localizedDescription
                    ))
                }
            }
        }
        .navigationTitle("회원 검색")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
    }
}

#Preview {
    NavigationStack {
        MemberSearchView()
    }
}
