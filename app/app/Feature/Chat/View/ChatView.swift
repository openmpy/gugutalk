import SwiftUI

struct ChatView: View {

    @State private var selectStatus: String = "ALL"

    var body: some View {
        NavigationStack {
            VStack {
                ChatStatusSelector(selectStatus: $selectStatus)

                ScrollView {
                    LazyVStack {
                        ForEach(0..<10) { _ in
                            ChatRow(
                                nickname: "닉네임",
                                updatedAt: "2026-03-30T12:00:00.0000",
                                content: "마지막 채팅 내용",
                                unreads: 1
                            )
                        }
                    }
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
