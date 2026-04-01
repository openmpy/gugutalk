import SwiftUI

struct MessageView: View {

    let memberId: Int64

    @State private var message: String = ""

    var body: some View {
        VStack {
            ScrollView {
                LazyVStack {
                    ForEach(0..<10) { i in
                        MessageBubble(
                            isMe: i % 2 == 0,
                            content: "안녕하세요",
                            createdAt: "2026-03-30T12:00:00.0000"
                        )
                    }
                }
                .padding(.horizontal)
            }
            .onTapGesture {
                hideKeyboard()
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

#Preview {
    NavigationStack {
        MessageView(memberId: 0)
    }
}
