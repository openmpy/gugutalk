import SwiftUI
import PhotosUI
import UniformTypeIdentifiers
import Toasts
import Kingfisher

struct MessageView: View {

    let chatRoomId: Int64
    let memberId: Int64

    @StateObject private var vm = MessageViewModel()
    @StateObject private var stomp = StompManager.shared

    @Environment(\.presentToast) var presentToast
    @Environment(\.dismiss) var dismiss

    @State private var selectMedia: [PhotosPickerItem] = []
    @State private var images: [IdentifiableImage] = []
    @State private var videos: [IdentifiableVideo] = []
    @State private var message: String = ""
    @State private var playingVideoURL: URL? = nil
    @State private var isUploading = false

    var body: some View {
        VStack {
            if vm.messages.isEmpty {
                Spacer()
                Text("내역이 비어있습니다.")
                    .foregroundColor(.primary)
                    .rotationEffect(.degrees(180))
                Spacer()
            } else {
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
                                if it.id == vm.messages.last?.id {
                                    Task {
                                        let result = await vm.loadMore(chatRoomId: chatRoomId)
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
                    .padding(.horizontal)
                }
                .onTapGesture {
                    hideKeyboard()
                }
            }
        }
        .onChange(of: vm.isRoomDelete) { _, isDeleted in
            if isDeleted {
                presentToast(ToastValue(
                    icon: Image(systemName: "checkmark.circle.fill").foregroundColor(.green),
                    message: "채팅방이 삭제되었습니다."
                ))
                dismiss()
            }
        }
        .onAppear {
            stomp.subscribe(to: "/topic/chat-rooms/\(chatRoomId)")
            vm.subscribe(chatRoomId: chatRoomId)
            vm.enter(chatRoomId: chatRoomId)
        }
        .onDisappear {
            stomp.unsubscribe(from: "/topic/chat-rooms/\(chatRoomId)")
            vm.unsubscribe()
            vm.leave()
        }
        .task {
            let result = await vm.gets(chatRoomId: chatRoomId)
            switch result {
            case .success():
                _ = await vm.getMember(chatRoomId: chatRoomId)
                _ = await vm.markAsRead(chatRoomId: chatRoomId)
            case .failure(let error):
                presentToast(ToastValue(
                    icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                    message: error.localizedDescription
                ))
                dismiss()
            }
        }
        .safeAreaInset(edge: .top) {
            GlassEffectContainer(spacing: 5) {
                HStack(alignment: .bottom) {
                    if isUploading {
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
                        .onChange(of: selectMedia) { _, newItems in
                            guard !newItems.isEmpty else { return }

                            Task {
                                isUploading = true
                                defer { isUploading = false }

                                var images: [IdentifiableImage] = []
                                var videos: [IdentifiableVideo] = []

                                for item in newItems {
                                    if let data = try? await item.loadTransferable(type: Data.self),
                                       let image = UIImage(data: data) {
                                        images.append(IdentifiableImage(image: image))
                                        continue
                                    }

                                    if let videoItem = try? await item.loadTransferable(type: VideoItem.self) {
                                        videos.append(IdentifiableVideo(video: videoItem.url))
                                    }
                                }

                                let result = await vm.sendMedia(
                                    chatRoomId: chatRoomId,
                                    images: images,
                                    videos: videos
                                )
                                if case .failure(let error) = result {
                                    presentToast(ToastValue(
                                        icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                                        message: error.localizedDescription
                                    ))
                                }

                                selectMedia = []
                            }
                        }
                    }

                    TextField("메시지 입력", text: $message, axis: .vertical)
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
                                    if (message.isEmpty) { return }

                                    Task {
                                        let result = await vm.send(chatRoomId: chatRoomId, content: message)
                                        switch result {
                                        case .success():
                                            message = ""
                                        case .failure(let error):
                                            presentToast(ToastValue(
                                                icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                                                message: error.localizedDescription
                                            ))
                                        }
                                    }
                                } label: {
                                    Image(systemName: "paperplane.fill")
                                        .foregroundColor(.white)
                                        .frame(width: 36, height: 36)
                                        .background(message.isEmpty ? Color(.systemGray3) : .blue)
                                        .clipShape(Circle())
                                }
                                .padding(.trailing, 4)
                                .padding(.bottom, 4)
                            }, alignment: .bottom
                        )
                        .glassEffect(
                            .regular.tint(.clear).interactive(),
                            in: .rect(cornerRadius: 20)
                        )
                        .autocorrectionDisabled(true)
                        .textInputAutocapitalization(.never)
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
}
