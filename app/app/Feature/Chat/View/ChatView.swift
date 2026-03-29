import SwiftUI

struct ChatView: View {

    var body: some View {
        NavigationStack {
            VStack {
                NavigationLink {
                    MemberProfileView()
                } label: {
                    Text("채팅")
                }
            }
            .navigationTitle("채팅")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button {
                        // 검색
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
                        Image(systemName: "bell.slash")
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
