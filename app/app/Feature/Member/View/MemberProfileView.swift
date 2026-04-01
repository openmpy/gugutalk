import SwiftUI
import Toasts

struct MemberProfileView: View {

    let memberId: Int64

    @StateObject private var vm = MemberProfileViewModel()

    @Environment(\.presentToast) var presentToast

    @State private var showMenu: Bool = false
    @State private var showMessage: Bool = false
    @State private var showBlock: Bool = false
    @State private var goReport: Bool = false
    @State private var goPrivateImage: Bool = false
    @State private var message: String = ""

    @Namespace var namespace

    var body: some View {
        VStack {
            if let member = vm.member {
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
                        likes: Int(member.likes),
                        distance: member.distance
                    )
                }
            }
        }
        .safeAreaInset(edge: .bottom) {
            GlassEffectContainer {
                HStack(spacing: 25) {
                    Button {
                        Task {
                            let result = await vm.toggleLike()
                            if case .failure(let error) = result {
                                presentToast(ToastValue(
                                    icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                                    message: error.localizedDescription
                                ))
                            }
                        }
                    } label: {
                        Image(systemName: "heart.fill")
                            .font(.title)
                            .frame(width: 60, height: 60)
                            .foregroundColor(vm.isLiked ? .red : .gray)
                            .glassEffect(.regular.interactive())
                            .glassEffectUnion(id: 1, namespace: namespace)
                    }

                    Button {
                        showMessage = true
                    } label: {
                        Image(systemName: "envelope.fill")
                            .font(.title)
                            .frame(width: 60, height: 60)
                            .foregroundColor(.blue)
                            .glassEffect(.regular.interactive())
                            .glassEffectUnion(id: 1, namespace: namespace)
                    }

                    Button {
                        Task {
                            let result = await vm.getPrivateImages(granterId: memberId)
                            switch result {
                            case .success():
                                goPrivateImage = true
                            case .failure(let error):
                                presentToast(ToastValue(
                                    icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                                    message: error.localizedDescription
                                ))
                            }
                        }
                    } label: {
                        Image(systemName: "photo.fill")
                            .font(.title)
                            .frame(width: 60, height: 60)
                            .foregroundColor(vm.isPrivateImageGrantedByTarget ? .green : .gray)
                            .glassEffect(.regular.interactive())
                            .glassEffectUnion(id: 1, namespace: namespace)
                    }
                    .disabled(!vm.isPrivateImageGrantedByTarget)

                    Button {
                        showBlock = true
                    } label: {
                        Image(systemName: "nosign")
                            .font(.title)
                            .frame(width: 60, height: 60)
                            .foregroundColor(vm.isBlocked ? .orange : .gray)
                            .glassEffect(.regular.interactive())
                            .glassEffectUnion(id: 1, namespace: namespace)
                    }
                }
            }
        }
        .ignoresSafeArea(.keyboard, edges: .bottom)
        .task {
            let result = await vm.getMember(memberId: memberId)
            if case .failure(let error) = result {
                presentToast(ToastValue(
                    icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                    message: error.localizedDescription
                ))
            }
        }
        .navigationTitle("프로필")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button {
                    showMenu = true
                } label: {
                    Image(systemName: "ellipsis")
                        .font(.title3)
                        .foregroundColor(.primary)
                }
                .confirmationDialog("메뉴", isPresented: $showMenu) {
                    Button(vm.isPrivateImageGranted ? "비밀 사진 닫기" : "비밀 사진 열기", role: .confirm) {
                        Task {
                            let result = await vm.togglePrivateImageGrant()
                            if case .failure(let error) = result {
                                presentToast(ToastValue(
                                    icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                                    message: error.localizedDescription
                                ))
                            }
                        }
                    }
                    Button("신고", role: .destructive) {
                        goReport = true
                    }
                    Button("취소", role: .cancel) { }
                }
            }
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
            TextField("내용 입력", text: $message)

            Button("전송", role: .confirm) {
                if message.isEmpty { return }
            }
            Button("취소", role: .cancel) { }
        }
        .alert(vm.isBlocked ? "차단 해제" : "차단", isPresented: $showBlock) {
            Button(vm.isBlocked ? "차단 해제" : "차단", role: .destructive) {
                Task {
                    let result = await vm.toggleBlock()
                    if case .failure(let error) = result {
                        presentToast(ToastValue(
                            icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                            message: error.localizedDescription
                        ))
                    }
                }
            }
            Button("취소", role: .cancel) { }
        } message: {
            Text(vm.isBlocked
                 ? "차단을 해제하시겠습니까?"
                 : "채팅 내역이 모두 삭제되며 서로의 목록에서도 표시되지 않습니다.")
        }
    }
}
