import SwiftUI

struct LocationView: View {

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
                            content: "자기소개",
                            gender: "MALE",
                            age: 20,
                            likes: 100,
                            distance: 12.34
                        )
                    }
                }
            }
            .navigationTitle("위치")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

#Preview {
    LocationView()
}
