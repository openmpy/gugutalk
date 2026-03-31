import SwiftUI

struct PrivateImageGrantListView: View {

    @StateObject private var vm = PrivateImageGrantListViewModel()

    var body: some View {
        VStack {
            if vm.members.isEmpty {
                Text("내역이 비어있습니다.")
                    .foregroundColor(.primary)
            } else {
                ScrollView {
                    LazyVStack {
                        ForEach(vm.members) { it in
                            MemberSettingRow(
                                nickname: it.nickname,
                                createdAt: it.createdAt,
                                gender: it.gender,
                                age: it.age
                            )
                            .onAppear {
                                if it.id == vm.members.last?.id {
                                    Task {
                                        await vm.loadMoreGrantedMember()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        .task {
            await vm.getGrantedMember()
        }
        .navigationTitle("비밀 사진 목록")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
    }
}

#Preview {
    NavigationStack {
        PrivateImageGrantListView()
    }
}
