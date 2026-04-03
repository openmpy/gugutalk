import SwiftUI

struct RecentView: View {

    @StateObject private var vm = RecentViewModel()
    @StateObject private var locationManager = LocationManager()

    @State private var showComment: Bool = false

    var body: some View {
        NavigationStack {
            VStack {
                GenderSelector(selectGender: $vm.selectGender)

                switch vm.state {

                case .idle:
                    Spacer()
                    EmptyView()
                    Spacer()

                case .loading:
                    Spacer()
                    ProgressView()
                    Spacer()

                case .empty:
                    Spacer()
                    Text("내역이 비어있습니다.")
                    Spacer()

                case .data:
                    listSection

                case .error(let message):
                    errorSection(message: message)
                }
            }
            .task {
                await vm.getRecentMembers()
            }
            .onChange(of: vm.selectGender) { _, _ in
                Task {
                    await vm.getRecentMembers()
                }
            }
            .overlay {
                if vm.isLoading {
                    LoadingOverlay()
                }
            }
            .navigationTitle("최근")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                searchToolbar
                commentToolbar
            }
            .alert("코멘트", isPresented: $showComment) {
                TextField("내용 입력", text: $vm.comment)

                Button("작성") {
                    if vm.comment.isEmpty {
                        ToastManager.shared.show("코멘트 내용을 작성해주세요.", type: .error)
                        return
                    }

                    Task {
                        do {
                            try await vm.updateComment(comment: vm.comment)

                            ToastManager.shared.show("코멘트가 작성되었습니다.")
                        } catch {
                            ToastManager.shared.show(error.localizedDescription, type: .error)
                        }
                    }
                }
                Button("취소", role: .cancel) {
                    vm.comment = ""
                }
            }
        }
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
                                try? await vm.loadMoreRecentMembers()
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
        .refreshable {
            Task {
                try? await locationManager.fetchLocation()
                try? await vm.bump(
                    latitude: locationManager.latitude,
                    longitude: locationManager.longitude
                )

                await vm.getRecentMembers()
            }
        }
    }

    private func errorSection(message: String) -> some View {
        VStack {
            Spacer()

            Text(message)
                .padding(.bottom)

            Button("다시 시도") {
                Task {
                    await vm.getRecentMembers()
                }
            }

            Spacer()
        }
    }

    private var searchToolbar: some ToolbarContent {
        ToolbarItem(placement: .topBarLeading) {
            NavigationLink {
                MemberSearchView()
            } label: {
                Image(systemName: "magnifyingglass")
                    .font(.title3)
                    .foregroundColor(.primary)
            }
        }
    }

    private var commentToolbar: some ToolbarContent {
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
}
