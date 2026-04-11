import SwiftUI

struct ChatSearchView: View {
    
    @StateObject private var vm = ChatSearchViewModel()
    
    var body: some View {
        VStack {
            VStack {
                switch vm.state {
                    
                case .idle:
                    Spacer()
                    Text("닉네임을 입력해주세요.")
                    Spacer()
                    
                case .loading:
                    Spacer()
                    ProgressView()
                    Spacer()
                    
                case .empty:
                    Spacer()
                    Text("검색 결과가 없습니다.")
                    Spacer()
                    
                case .data:
                    listSection
                    
                case .error(let message):
                    errorSection(message: message)
                }
            }
        }
        .searchable(
            text: $vm.nickname,
            placement: .navigationBarDrawer(displayMode: .always),
            prompt: "닉네임 입력 (2자 이상)"
        )
        .onChange(of: vm.nickname) { _, newValue in
            if newValue.isEmpty {
                vm.state = .idle
            }
        }
        .onSubmit(of: .search) {
            Task {
                await vm.search()
            }
        }
        .navigationTitle("채팅 검색")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
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
        .onTapGesture {
            hideKeyboard()
        }
    }
    
    private func errorSection(message: String) -> some View {
        VStack {
            Text(message)
                .padding(.bottom)
            
            Button("다시 시도") {
                Task {
                    await vm.search()
                }
            }
        }
    }
}
