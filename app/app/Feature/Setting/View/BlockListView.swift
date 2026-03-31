import SwiftUI

struct BlockListView: View {

    @StateObject private var vm = BlockListViewModel()

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
                                        await vm.loadMoreBlockedMember()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        .task {
            await vm.getBlockedMember()
        }
        .navigationTitle("차단 목록")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
        .alert("에러", isPresented: $vm.showErrorAlert) {
            Button("확인", role: .cancel) { }
        } message: {
            Text(vm.errorMessage)
        }
    }
}

#Preview {
    NavigationStack {
        BlockListView()
    }
}
