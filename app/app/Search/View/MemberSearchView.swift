import SwiftUI

struct MemberSearchView: View {

    @State private var keyword: String = ""

    var body: some View {
        VStack {
            ScrollView {
                LazyVStack {
                    ForEach(0..<10) { _ in
                        MemberRow(
                            nickname: "닉네임",
                            updatedAt: "2026-03-30T12:00:00.0000",
                            content: "코멘트",
                            gender: "MALE",
                            age: 20,
                            likes: 100,
                            distance: 12.34
                        )
                    }
                }
            }
        }
        .searchable(
            text: $keyword,
            placement: .navigationBarDrawer(displayMode: .always),
            prompt: "닉네임 입력"
        )
        .navigationTitle("회원 검색")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
    }
}

#Preview {
    NavigationStack {
        MemberSearchView()
    }
}
