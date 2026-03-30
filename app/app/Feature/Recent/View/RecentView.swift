import SwiftUI

struct RecentView: View {

    @State private var selectGender: String = "ALL"
    @State private var showComment: Bool = false
    @State private var comment: String = ""

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
            }
            .navigationTitle("최근")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    NavigationLink {
                        MemberSearchView()
                    } label: {
                        Image(systemName: "magnifyingglass")
                            .font(.title3)
                            .foregroundColor(.primary)
                    }
                }

                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        showComment = true
                    } label: {
                        Image(systemName: "square.and.pencil")
                            .font(.title3)
                            .foregroundColor(.primary)
                    }
                }
            }
            .alert("코멘트", isPresented: $showComment) {
                TextField("내용 입력", text: $comment)

                Button("작성", role: .confirm) {
                    if comment.isEmpty {
                        return
                    }
                }
                Button("취소", role: .cancel) { }
            }
        }
    }
}

#Preview {
    RecentView()
}
