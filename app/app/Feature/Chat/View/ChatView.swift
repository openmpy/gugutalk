import SwiftUI
import Toasts

struct ChatView: View {

    @StateObject private var vm = ChatViewModel()

    @Environment(\.presentToast) var presentToast

    @State private var selectStatus: String = "ALL"

    var body: some View {
        NavigationStack {
            VStack {
                ChatStatusSelector(selectStatus: $selectStatus)
                
                if vm.chatRooms.isEmpty {
                    Spacer()
                    Text("내역이 비어있습니다.")
                        .foregroundColor(.primary)
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
                                            let result = await vm.loadMore(status: selectStatus)
                                            if case .failure(let error) = result {
                                                presentToast(ToastValue(
                                                    icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                                                    message: error.localizedDescription
                                                ))
                                            }
                                        }
                                    }
                                }
                                .contextMenu {
                                    Button(role: .destructive) {
                                        Task {
                                            let result = await vm.delete(chatRoomId: it.chatRoomId)
                                            switch result {
                                            case .success():
                                                presentToast(ToastValue(
                                                    icon: Image(systemName: "checkmark.circle.fill").foregroundColor(.green),
                                                    message: "채팅방을 삭제하셨습니다."
                                                ))
                                            case .failure(let error):
                                                presentToast(ToastValue(
                                                    icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                                                    message: error.localizedDescription
                                                ))
                                            }
                                        }
                                    } label: {
                                        Label("삭제", systemImage: "trash")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .onChange(of: selectStatus) { _, newStatus in
                Task {
                    await vm.gets(status: newStatus)
                }
            }
            .onAppear {
                vm.subscribe()
            }
            .onDisappear {
                vm.unsubscribe()
            }
            .task {
                let result = await vm.gets(status: selectStatus)
                if case .failure(let error) = result {
                    presentToast(ToastValue(
                        icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                        message: error.localizedDescription
                    ))
                }

                let chatEnabledResult = await vm.getChatEnabled()
                if case .failure(let error) = chatEnabledResult {
                    presentToast(ToastValue(
                        icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                        message: error.localizedDescription
                    ))
                }
            }
            .navigationTitle("채팅")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    NavigationLink {
                        ChatSearchView()
                    } label: {
                        Image(systemName: "magnifyingglass")
                            .font(.title3)
                            .foregroundColor(.primary)
                    }
                }

                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        Task {
                            await vm.toggleChatEnabled()
                        }
                    } label: {
                        Image(systemName: vm.isChatEnabled ? "bell" : "bell.slash")
                            .font(.title3)
                            .foregroundColor(.primary)
                    }
                }
            }
        }
    }
}

#Preview {
    ChatView()
}
