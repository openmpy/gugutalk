import SwiftUI

struct ChatView: View {

    @StateObject private var vm = ChatViewModel()

    var body: some View {
        NavigationStack {
            VStack {
                ChatStatusSelector(selectStatus: $vm.selectStatus)

                switch vm.state {

                case .idle:
                    Spacer()
                    EmptyView()
                    Spacer()

                case .loading:
                    Spacer()
                    ProgressView()
                    Spacer()

                case .empty:
                    Spacer()
                    Text("내역이 비어있습니다.")
                    Spacer()

                case .data:
                    listSection

                case .error(let message):
                    errorSection(message: message)
                }
            }
            .onChange(of: vm.selectStatus) { _, newStatus in
                Task {
                    await vm.gets()
                }
            }
            .onAppear {
                vm.subscribe()
            }
            .onDisappear {
                vm.unsubscribe()
            }
            .overlay {
                if vm.isLoading {
                    LoadingOverlay()
                }
            }
            .task {
                await vm.gets()
                try? await vm.getChatEnabled()
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
                            try? await vm.toggleChatEnabled()
                        }
                    } label: {
                        Image(systemName: vm.isChatEnabled ? "bell" : "bell.slash")
                            .font(.title3)
                            .foregroundColor(.primary)
                    }
                    .disabled(vm.isLoading)
                }
            }
        }
    }

    // MARK: - SECTION

    private var listSection: some View {
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
                    .contextMenu {
                        Button(role: .destructive) {
                            Task {
                                do {
                                    try await vm.delete(chatRoomId: it.chatRoomId)
                                    ToastManager.shared.show("채팅방을 삭제하셨습니다.")
                                } catch {
                                    ToastManager.shared.show(error)
                                }
                            }
                        } label: {
                            Label("삭제", systemImage: "trash")
                        }
                    }
                    .onAppear {
                        if it.id == vm.chatRooms.last?.id && vm.hasNext {
                            Task {
                                try? await vm.loadMore()
                            }
                        }
                    }
                }

                if vm.isPaging {
                    ProgressView()
                        .padding()
                }
            }
        }
    }

    private func errorSection(message: String) -> some View {
        VStack {
            Spacer()

            Text(message)
                .padding(.bottom)

            Button("다시 시도") {
                Task {
                    await vm.gets()
                }
            }

            Spacer()
        }
    }
}
