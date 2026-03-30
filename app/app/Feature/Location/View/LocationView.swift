import SwiftUI

struct LocationView: View {

    @State private var selectGender: String = "ALL"

    var body: some View {
        NavigationStack {
            VStack {
                GenderSelector(selectGender: $selectGender)

                ScrollView {
                    LazyVStack {
                        ForEach(0..<10) { _ in
                            NavigationLink {
                                MemberProfileView()
                            } label: {
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
                .refreshable {

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
