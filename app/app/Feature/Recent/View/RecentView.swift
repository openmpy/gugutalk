import SwiftUI

struct RecentView: View {

    @State private var selectGender = "ALL"

    var body: some View {
        NavigationStack {
            GenderSelector(selectGender: $selectGender)

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
            .navigationTitle("최근")
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
                        // 코멘트
                    } label: {
                        Image(systemName: "square.and.pencil")
                            .font(.title3)
                            .foregroundColor(.primary)
                    }
                }
            }
        }
    }
}

#Preview {
    RecentView()
}
