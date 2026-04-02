import SwiftUI
import Toasts

struct ChatSearchView: View {

    @StateObject private var vm = ChatSearchViewModel()

    @Environment(\.presentToast) var presentToast

    @State private var keyword: String = ""

    var body: some View {
        VStack {
            if keyword.isEmpty || vm.chatRooms.isEmpty {
                Spacer()
                Text("검색 결과가 없습니다.")
                    .foregroundColor(.secondary)
                Spacer()
            } else {
                ScrollView {
                    LazyVStack {
                        ForEach(vm.chatRooms) { it in
                            NavigationLink {
                                MessageView(
                                    chatRoomId: it.chatRoomId,
                                    memberId: it.targetId
                                )
                            } label: {
                                ChatRow(
                                    profileUrl: it.profileUrl,
                                    nickname: it.nickname,
                                    updatedAt: it.sortAt,
                                    content: it.lastMessage ?? "",
                                    unreads: it.unreadCount
                                )
                            }
                            .onAppear {
                                if it.id == vm.chatRooms.last?.id {
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
                vm.chatRooms = []
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
        .onAppear {
            vm.subscribe()
        }
        .onDisappear {
            vm.unsubscribe()
        }
        .task {
            guard keyword.count >= 2 else { return }
            
            let result = await vm.search(keyword: keyword)
            if case .failure(let error) = result {
                presentToast(ToastValue(
                    icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                    message: error.localizedDescription
                ))
            }
        }
        .navigationTitle("채팅 검색")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
    }
}

#Preview {
    NavigationStack {
        ChatSearchView()
    }
}
