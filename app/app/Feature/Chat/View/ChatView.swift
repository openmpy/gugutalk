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
                                        memberId: it.memberId
                                    )
                                } label: {
                                    ChatRow(
                                        nickname: it.nickname,
                                        updatedAt: it.lastMessageAt ?? "",
                                        content: it.lastMessage ?? "",
                                        unreads: 0
                                    )
                                }
                                .onAppear {
                                    if it.id == vm.chatRooms.last?.id {
                                        Task {
                                            let result = await vm.loadMoreChatRoom()
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
                let result = await vm.gets()
                if case .failure(let error) = result {
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
                        // 수신 토글
                    } label: {
                        Image(systemName: "bell")
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
