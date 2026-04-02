import SwiftUI

struct ChatSearchView: View {

    @State private var keyword: String = ""

    var body: some View {
        VStack {
            ScrollView {
                LazyVStack {
                    ForEach(0..<10) { _ in
                        ChatRow(
                            profileUrl: nil,
                            nickname: "닉네임",
                            updatedAt: "2026-03-30T12:00:00.0000",
                            content: "마지막 채팅 내용",
                            unreads: 1
                        )
                    }
                }
            }
            .onTapGesture {
                hideKeyboard()
            }
        }
        .searchable(
            text: $keyword,
            placement: .navigationBarDrawer(displayMode: .always),
            prompt: "닉네임 입력"
        )
        .navigationTitle("채팅 검색")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
    }
}

#Preview {
    NavigationStack {
        ChatSearchView()
    }
}
