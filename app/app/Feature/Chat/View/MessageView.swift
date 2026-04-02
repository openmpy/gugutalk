import SwiftUI
import Toasts

struct MessageView: View {

    let chatRoomId: Int64
    let memberId: Int64

    @StateObject private var vm = MessageViewModel()
    @StateObject private var stomp = StompManager.shared

    @Environment(\.presentToast) var presentToast

    @State private var message: String = ""

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
                                isMe: it.senderId != memberId,
                                content: it.content,
                                createdAt: it.createdAt
                            )
                            .onAppear {
                                if it.id == vm.messages.last?.id {
                                    Task {
                                        let result = await vm.loadMoreMessage(chatRoomId: chatRoomId)
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
        .onAppear {
            stomp.subscribe(to: "/topic/chat/\(chatRoomId)")
            vm.subscribeRoom(chatRoomId: chatRoomId)
        }
        .onDisappear {
            stomp.unsubscribe(from: "/topic/chat/\(chatRoomId)")
            vm.unsubscribeRoom()
        }
        .task {
            let result = await vm.gets(chatRoomId: chatRoomId)
            if case .failure(let error) = result {
                presentToast(ToastValue(
                    icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                    message: error.localizedDescription
                ))
            }
        }
        .safeAreaInset(edge: .top) {
            GlassEffectContainer(spacing: 5) {
                HStack(alignment: .bottom) {
                    Button {

                    } label: {
                        Image(systemName: "paperclip")
                            .font(.title3)
                            .frame(width: 44, height: 44)
                            .foregroundColor(.primary)
                            .glassEffect(.regular.tint(Color(.clear)).interactive())
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
                                    Task {
                                        let result = await vm.sendMessage(chatRoomId: chatRoomId, content: message)
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
        .navigationTitle("홍길동")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                NavigationLink {
                    MemberProfileView(memberId: memberId)
                } label: {
                    Image(systemName: "person.fill")
                        .font(.footnote)
                        .foregroundStyle(Color(.systemGray3))
                        .frame(width: 27, height: 27)
                        .background(Color(.systemGray6), in: Circle())
                }
            }
        }
    }
}
