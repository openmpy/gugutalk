import SwiftUI

struct RecentView: View {

    @StateObject private var vm = RecentViewModel()

    @State private var selectGender: String = "ALL"
    @State private var showComment: Bool = false
    @State private var comment: String = ""

    var body: some View {
        NavigationStack {
            VStack {
                GenderSelector(selectGender: $selectGender)

                if vm.members.isEmpty {
                    Spacer()

                    Text("내역이 비어있습니다.")
                        .foregroundColor(.primary)

                    Spacer()
                } else {
                    ScrollView {
                        LazyVStack {
                            ForEach(vm.members) { it in
                                NavigationLink {
                                    MemberProfileView()
                                } label: {
                                    MemberRow(
                                        profileUrl: it.profileUrl,
                                        nickname: it.nickname,
                                        updatedAt: it.updatedAt,
                                        content: it.comment ?? "",
                                        gender: it.gender,
                                        age: it.age,
                                        likes: it.likes,
                                        distance: it.distance
                                    )
                                }
                                .onAppear {
                                    if it.id == vm.members.last?.id {
                                        Task {
                                            await vm.loadMoreGrantedMember(gender: selectGender.uppercased())
                                        }
                                    }
                                }
                            }
                        }
                    }
                    .refreshable {
                        Task {
                            await vm.bump()
                            await vm.getRecentMembers(gender: selectGender.uppercased())
                        }
                    }
                }
            }
            .task {
                await vm.getRecentMembers(gender: selectGender.uppercased())
            }
            .onChange(of: selectGender) { _, newValue in
                Task {
                    await vm.getRecentMembers(gender: newValue.uppercased())
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
