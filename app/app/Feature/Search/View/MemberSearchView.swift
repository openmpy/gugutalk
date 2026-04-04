import SwiftUI

struct MemberSearchView: View {

    @StateObject private var vm = MemberSearchViewModel()

    var body: some View {
        VStack {
            switch vm.state {

            case .idle:
                Spacer()
                Text("닉네임을 입력해주세요.")
                Spacer()

            case .loading:
                Spacer()
                ProgressView()
                Spacer()

            case .empty:
                Spacer()
                Text("검색 결과가 없습니다.")
                Spacer()

            case .data:
                listSection

            case .error(let message):
                errorSection(message: message)
            }
        }
        .searchable(
            text: $vm.keyword,
            placement: .navigationBarDrawer(displayMode: .always),
            prompt: "닉네임 입력 (2자 이상)"
        )
        .onChange(of: vm.keyword) { _, newValue in
            if newValue.isEmpty {
                vm.state = .idle
            }
        }
        .onSubmit(of: .search) {
            Task {
                await vm.search()
            }
        }
        .navigationTitle("회원 검색")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
    }

    // MARK: - SECTION

    private var listSection: some View {
        ScrollView {
            LazyVStack {
                ForEach(vm.members) { it in
                    NavigationLink {
                        MemberProfileView(memberId: it.memberId)
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
                        if it.id == vm.members.last?.id && vm.hasNext {
                            Task {
                                try? await vm.loadMore()
                            }
                        }
                    }
                }
                if vm.isPaging {
                    ProgressView()
                        .padding()
                }
            }
        }
    }
    
    private func errorSection(message: String) -> some View {
        VStack {
            Text(message)
                .padding(.bottom)

            Button("다시 시도") {
                Task {
                    await vm.search()
                }
            }
        }
    }
}
