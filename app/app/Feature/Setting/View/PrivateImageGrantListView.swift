import SwiftUI
import Toasts

struct PrivateImageGrantListView: View {

    @StateObject private var vm = PrivateImageGrantListViewModel()

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
                                age: it.age,
                                onDelete: {
                                    Task {
                                        let result = await vm.revolke(memberId: it.memberId)
                                        if case .failure(let error) = result {
                                            presentToast(ToastValue(
                                                icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                                                message: error.localizedDescription
                                            ))
                                        }
                                    }
                                }
                            )
                            .onAppear {
                                if it.id == vm.members.last?.id {
                                    Task {
                                        let result = await vm.loadMoreGrantedMember()
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
            }
        }
        .task {
            let result = await vm.getGrantedMember()
            if case .failure(let error) = result {
                presentToast(ToastValue(
                    icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                    message: error.localizedDescription
                ))
            }
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
