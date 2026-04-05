import SwiftUI
import PhotosUI
import Kingfisher

struct MessageView: View {

    let chatRoomId: Int64
    let memberId: Int64

    @StateObject private var vm = MessageViewModel()
    @StateObject private var stomp = StompManager.shared

    @Environment(\.dismiss) var dismiss
    @Environment(\.scenePhase) private var scenePhase

    @State private var selectMedia: [PhotosPickerItem] = []
    @State private var playingVideoURL: URL? = nil

    var body: some View {
        VStack {
            VStack {
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
                        .rotationEffect(.degrees(180))
                    Spacer()

                case .data:
                    listSection

                case .error(let message):
                    errorSection(message: message)
                }
            }
        }
        .onAppear {
            stomp.subscribe(to: "/topic/chat-rooms/\(chatRoomId)")
            vm.subscribe(chatRoomId: chatRoomId)
        }
        .onDisappear {
            stomp.unsubscribe(from: "/topic/chat-rooms/\(chatRoomId)")
            vm.unsubscribe()
        }
        .task {
            await vm.gets(chatRoomId: chatRoomId)
            try? await vm.getMember(chatRoomId: chatRoomId)
            try? await vm.markAsRead(chatRoomId: chatRoomId)
        }
        .onChange(of: vm.isRoomDelete) { _, isDeleted in
            if isDeleted {
                ToastManager.shared.show("채팅방이 삭제되었습니다.")
                dismiss()
            }
        }
        .onChange(of: scenePhase) { _, newPhase in
            if newPhase == .active {
                Task {
                    try? await vm.markAsRead(chatRoomId: chatRoomId)
                }
            }
        }
        .safeAreaInset(edge: .top) {
            GlassEffectContainer(spacing: 5) {
                HStack(alignment: .bottom) {
                    uploadSection

                    inputSection
                }
                .padding()
            }
            .rotationEffect(.degrees(180))
            .background(Color(.systemBackground).opacity(0.0001))
        }
        .rotationEffect(.degrees(180))
        .navigationDestination(item: $playingVideoURL) { url in
            VideoPlayerView(url: url)
        }
        .navigationTitle(vm.member?.nickname ?? "")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                NavigationLink {
                    MemberProfileView(memberId: memberId)
                } label: {
                    KFImage(URL(string: vm.member?.profileUrl ?? ""))
                        .resizable()
                        .placeholder {
                            Image(systemName: "person.fill")
                                .font(.footnote)
                                .foregroundStyle(Color(.systemGray3))
                                .frame(width: 27, height: 27)
                                .background(Color(.systemGray6), in: Circle())
                        }
                        .font(.title)
                        .frame(width: 27, height: 27)
                        .foregroundColor(Color(.systemGray6))
                        .background(Color(.systemGray4))
                        .clipShape(Circle())
                }
            }
        }
    }

    // MARK: - SECTION

    private var listSection: some View {
        ScrollView {
            LazyVStack {
                ForEach(vm.messages) { it in
                    MessageBubble(
                        isMe: it.senderId == AuthStore.shared.memberId,
                        content: it.content,
                        createdAt: it.createdAt,
                        type: it.type,
                        playingVideoURL: $playingVideoURL
                    )
                    .onAppear {
                        if it.id == vm.messages.last?.id && vm.hasNext {
                            Task {
                                try? await vm.loadMore(chatRoomId: chatRoomId)
                            }
                        }
                    }
                }

                if vm.isPaging {
                    ProgressView()
                        .padding()
                }
            }
            .padding(.horizontal)
        }
        .onTapGesture {
            hideKeyboard()
        }
    }

    private var uploadSection: some View {
        Group {
            if vm.isUploading {
                ProgressView()
                    .frame(width: 44, height: 44)
                    .glassEffect(.regular.tint(Color(.clear)).interactive())
            } else {
                PhotosPicker(
                    selection: $selectMedia,
                    maxSelectionCount: 5,
                    matching: .any(of: [.images, .videos])
                ) {
                    Image(systemName: "paperclip")
                        .font(.title3)
                        .frame(width: 44, height: 44)
                        .foregroundColor(.primary)
                        .glassEffect(.regular.tint(Color(.clear)).interactive())
                }
            }
        }
        .onChange(of: selectMedia) { _, newItems in
            guard !newItems.isEmpty else { return }

            Task {
                for item in newItems {
                    if let data = try? await item.loadTransferable(type: Data.self),
                       let image = UIImage(data: data) {
                        vm.images.append(IdentifiableImage(image: image))
                        continue
                    }

                    if let videoItem = try? await item.loadTransferable(type: VideoItem.self) {
                        vm.videos.append(IdentifiableVideo(video: videoItem.url))
                    }
                }

                try await vm.sendMedia(chatRoomId: chatRoomId)
                selectMedia = []
            }
        }
    }

    private var inputSection: some View {
        TextField("메시지 입력", text: $vm.message, axis: .vertical)
            .font(.subheadline)
            .lineLimit(5)
            .padding(.leading)
            .padding(.trailing, 50)
            .padding(.vertical, 8)
            .frame(minHeight: 44)
            .overlay(
                HStack {
                    Spacer()

                    Button {
                        Task {
                            try? await vm.send(chatRoomId: chatRoomId)
                            vm.message = ""
                        }
                    } label: {
                        Image(systemName: "paperplane.fill")
                            .foregroundColor(.white)
                            .frame(width: 36, height: 36)
                            .background(vm.message.isEmpty ? Color(.systemGray3) : .blue)
                            .clipShape(Circle())
                    }
                    .padding(.trailing, 4)
                    .padding(.bottom, 4)
                    .disabled(vm.isLoading || vm.message.isEmpty)
                }, alignment: .bottom
            )
            .glassEffect(
                .regular.tint(.clear).interactive(),
                in: .rect(cornerRadius: 20)
            )
            .autocorrectionDisabled(true)
            .textInputAutocapitalization(.never)
    }

    private func errorSection(message: String) -> some View {
        VStack {
            Spacer()

            Text(message)
                .padding(.bottom)

            Button("다시 시도") {
                Task {
                    await vm.gets(chatRoomId: chatRoomId)
                }
            }

            Spacer()
        }
    }
}
