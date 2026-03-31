import SwiftUI
import Toasts

struct BlockListView: View {

    @StateObject private var vm = BlockListViewModel()

    @Environment(\.presentToast) var presentToast

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
                                        let result = await vm.loadMoreBlockedMember()
                                        if case .failure(let error) = result {
                                            presentToast(ToastValue(
                                                icon: Image(systemName: "xmark.circle.fill"),
                                                message: error.localizedDescription
                                            ))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        .task {
            let result = await vm.getBlockedMember()
            if case .failure(let error) = result {
                presentToast(ToastValue(
                    icon: Image(systemName: "xmark.circle.fill"),
                    message: error.localizedDescription
                ))
            }
        }
        .navigationTitle("차단 목록")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
    }
}

#Preview {
    NavigationStack {
        BlockListView()
    }
}
