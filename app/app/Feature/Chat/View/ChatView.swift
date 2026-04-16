import SwiftUI

struct ChatRoute: Hashable {

    let chatRoomId: Int64
    let memberId: Int64
}

struct ChatView: View {

    @ObservedObject private var router: NotificationRouter

    @StateObject private var vm = ChatViewModel()

    @State private var path = NavigationPath()

    init() {
        self.router = NotificationRouter.shared
    }

    var body: some View {
        NavigationStack(path: $path) {
            VStack {
                ChatStatusSelector(selectStatus: $vm.selectStatus)

                switch vm.state {
                case .idle:
                    Spacer(); EmptyView(); Spacer()
                case .loading:
                    Spacer(); ProgressView(); Spacer()
                case .empty:
                    Spacer(); Text("내역이 비어있습니다."); Spacer()
                case .data:
                    listSection
                case .error(let message):
                    errorSection(message: message)
                }
            }
            .navigationDestination(for: ChatRoute.self) { route in
                MessageView(
                    chatRoomId: route.chatRoomId,
                    memberId: route.memberId
                )
            }
            .onChange(of: vm.selectStatus) { _, _ in
                Task { await vm.gets() }
            }
            .onChange(of: router.pendingChat) { _, newValue in
                guard let chat = newValue else { return }

                path.append(ChatRoute(chatRoomId: chat.chatRoomId, memberId: chat.memberId))
                router.pendingChat = nil
            }
            .onAppear {
                vm.subscribe()

                if let chat = router.pendingChat {
                    path.append(ChatRoute(chatRoomId: chat.chatRoomId, memberId: chat.memberId))
                    router.pendingChat = nil
                }
            }
            .onDisappear { vm.unsubscribe() }
            .overlay {
                if vm.isLoading { LoadingOverlay() }
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
                        Task { try? await vm.toggleChatEnabled() }
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

    private var listSection: some View {
        ScrollView {
            LazyVStack {
                ForEach(vm.chatRooms) { it in
                    NavigationLink(
                        value: ChatRoute(chatRoomId: it.chatRoomId, memberId: it.targetId)
                    ) {
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
                            Task { try? await vm.loadMore() }
                        }
                    }
                }

                if vm.isPaging {
                    ProgressView().padding()
                }
            }
        }
    }

    private func errorSection(message: String) -> some View {
        VStack {
            Spacer()
            Text(message).padding(.bottom)
            Button("다시 시도") {
                Task { await vm.gets() }
            }
            Spacer()
        }
    }
}
