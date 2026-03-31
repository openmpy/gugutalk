import SwiftUI
import Toasts

struct LikeListView: View {

    @StateObject private var vm = LikeListViewModel()
    
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
                                        let result = await vm.loadMoreLikedMember()
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
            let result = await vm.getLikedMember()
            if case .failure(let error) = result {
                presentToast(ToastValue(
                    icon: Image(systemName: "xmark.circle.fill"),
                    message: error.localizedDescription
                ))
            }
        }
        .navigationTitle("좋아요 목록")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
    }
}

#Preview {
    NavigationStack {
        LikeListView()
    }
}
