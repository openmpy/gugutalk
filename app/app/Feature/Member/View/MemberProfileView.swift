import SwiftUI

struct MemberProfileView: View {

    let memberId: Int64

    @AppStorage("saveMessage") private var saveMessage: String = ""

    @StateObject private var vm = MemberProfileViewModel()

    @State private var showMenu: Bool = false
    @State private var showMessage: Bool = false
    @State private var showBlock: Bool = false
    @State private var goReport: Bool = false
    @State private var goPrivateImage: Bool = false

    @Namespace var namespace

    var body: some View {
        VStack {
            switch vm.state {

            case .idle:
                Spacer()
                EmptyView()
                Spacer()

            case .loading:
                Spacer()
                ProgressView()
                Spacer()

            case .data:
                if let member = vm.member {
                    memberSection(member: member)
                }

            case .error(let message):
                errorSection(message: message)
            }
        }
        .safeAreaInset(edge: .bottom) {
            GlassEffectContainer {
                HStack(spacing: 25) {
                    likeButton
                    messageButton
                    privateButton
                    blockButton
                }
            }
        }
        .ignoresSafeArea(.keyboard, edges: .bottom)
        .overlay {
            if vm.isLoading {
                LoadingOverlay()
            }
        }
        .task {
            await vm.getMember(memberId: memberId)
        }
        .navigationTitle("프로필")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
        .toolbar {
            toolbar
        }
        .navigationDestination(isPresented: $goReport) {
            ReportView(memberId: memberId, nickname: vm.member?.nickname ?? "")
        }
        .navigationDestination(isPresented: $goPrivateImage) {
            PrivateImageFullCoverTabView(
                images: vm.privateImages.map { URL(string: $0.url)! }
            )
        }
        .alert("쪽지", isPresented: $showMessage) {
            TextField("내용 입력", text: $vm.message)

            Button("전송", role: .confirm) {
                Task {
                    do {
                        try await vm.createChatRoom(targetId: memberId, content: vm.message)
                        ToastManager.shared.show("쪽지가 전송되었습니다.")
                    } catch {
                        ToastManager.shared.show(error)
                    }
                }
            }

            Button("취소", role: .cancel) { }
        }
        .alert(vm.member?.isBlocked == true ? "차단 해제" : "차단", isPresented: $showBlock) {
            Button(vm.member?.isBlocked == true ? "차단 해제" : "차단", role: .destructive) {
                Task {
                    try? await vm.toggleBlock(memberId: memberId)
                }
            }
            Button("취소", role: .cancel) { }
        } message: {
            Text(vm.member?.isBlocked == true ? "차단을 해제하시겠습니까?" : "채팅 내역이 모두 삭제되며 서로의 목록에서도 표시되지 않습니다.")
        }
    }

    // MARK: - SECTION

    private func memberSection(member: MemberGetResponse) -> some View {
        ScrollView {
            MemberProfileImage(
                images: member.images.compactMap { URL(string: $0.url) }
            )

            MemberProfileInfo(
                nickname: member.nickname,
                updatedAt: member.updatedAt,
                gender: member.gender,
                age: member.age,
                bio: member.bio ?? "",
                likes: member.likes,
                distance: member.distance
            )
        }
    }

    private var likeButton: some View {
        Button {
            Task {
                try? await vm.toggleLike(memberId: memberId)
            }
        } label: {
            Image(systemName: "heart.fill")
                .font(.title)
                .frame(width: 60, height: 60)
                .foregroundColor(vm.member?.isLiked == true ? .red : .gray)
                .glassEffect(.regular.interactive())
                .glassEffectUnion(id: 1, namespace: namespace)
        }
        .disabled(vm.isLoading)
    }

    private var messageButton: some View {
        Button {
            vm.message = saveMessage
            showMessage = true
        } label: {
            Image(systemName: "envelope.fill")
                .font(.title)
                .frame(width: 60, height: 60)
                .foregroundColor(vm.member?.isChatEnabled == true ? .blue : .gray)
                .glassEffect(.regular.interactive())
                .glassEffectUnion(id: 1, namespace: namespace)
        }
        .disabled(vm.isLoading || vm.member?.isChatEnabled == false)
    }

    private var privateButton: some View {
        Button {
            Task {
                do {
                    try await vm.getPrivateImages(granterId: memberId)
                    goPrivateImage = true
                } catch {
                    ToastManager.shared.show(error)
                }
            }
        } label: {
            Image(systemName: "photo.fill")
                .font(.title)
                .frame(width: 60, height: 60)
                .foregroundColor(vm.member?.isPrivateImageGrantedByTarget == true ? .green : .gray)
                .glassEffect(.regular.interactive())
                .glassEffectUnion(id: 1, namespace: namespace)
        }
        .disabled(vm.member?.isPrivateImageGrantedByTarget == false)
    }

    private var blockButton: some View {
        Button {
            showBlock = true
        } label: {
            Image(systemName: "nosign")
                .font(.title)
                .frame(width: 60, height: 60)
                .foregroundColor(vm.member?.isBlocked == true ? .orange : .gray)
                .glassEffect(.regular.interactive())
                .glassEffectUnion(id: 1, namespace: namespace)
        }
        .disabled(vm.isLoading)
    }

    private var toolbar: some ToolbarContent {
        ToolbarItem(placement: .topBarTrailing) {
            Button {
                showMenu = true
            } label: {
                Image(systemName: "ellipsis")
                    .font(.title3)
                    .foregroundColor(.primary)
            }
            .confirmationDialog("메뉴", isPresented: $showMenu) {
                Button(vm.member?.isPrivateImageGranted == true ? "비밀 사진 닫기" : "비밀 사진 열기", role: .confirm) {
                    Task {
                        try? await vm.togglePrivateImageGrant(memberId: memberId)
                    }
                }
                .disabled(vm.isLoading)

                Button("신고", role: .destructive) {
                    goReport = true
                }

                Button("취소", role: .cancel) { }
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
                    await vm.getMember(memberId: memberId)
                }
            }

            Spacer()
        }
    }
}
