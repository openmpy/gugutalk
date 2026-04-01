import SwiftUI
import Toasts

struct SettingView: View {

    @AppStorage("isLoggedIn") private var isLoggedIn: Bool = false

    @StateObject private var vm = SettingViewModel()

    @Environment(\.colorScheme) var colorScheme
    @Environment(\.presentToast) var presentToast

    @State private var showMenu: Bool = false
    @State private var showDelete: Bool = false

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 15) {
                    VStack(spacing: 0) {
                        NavigationLink {
                            MyProfieView()
                        } label : {
                            SettingRow(title: "내 프로필", icon: "person.crop.circle.fill", color: .blue)
                        }
                    }
                    .cornerRadius(20)

                    VStack(spacing: 0) {
                        NavigationLink {
                            LikeListView()
                        } label : {
                            SettingRow(title: "좋아요 목록", icon: "heart.fill", color: .red)
                        }
                        NavigationLink {
                            PrivateImageGrantListView()
                        } label: {
                            SettingRow(title: "비밀 사진 목록", icon: "photo.fill", color: .green)
                        }
                        NavigationLink {
                            BlockListView()
                        } label: {
                            SettingRow(title: "차단 목록", icon: "nosign", color: .red)
                        }
                    }
                    .cornerRadius(20)

                    VStack(spacing: 0) {
                        NavigationLink {
                            PointView()
                        } label: {
                            SettingRow(title: "포인트", icon: "star.circle.fill", color: .yellow)
                        }
                        Button {
                        } label: {
                            SettingRow(title: "출석 체크", icon: "calendar.circle.fill", color: .orange)
                        }
                        Button {
                        } label: {
                            SettingRow(title: "광고 보상", icon: "gift.fill", color: .pink)
                        }
                    }
                    .cornerRadius(20)

                    VStack(spacing: 0) {
                        SettingRow(title: "공지사항", icon: "megaphone.fill", color: .teal)
                        SettingRow(title: "문의사항", icon: "envelope.fill", color: .indigo)
                        SettingRow(title: "서비스 이용약관", icon: "doc.text.fill", color: .gray)
                        SettingRow(title: "개인정보 취급방침", icon: "shield.fill", color: .green)
                    }
                    .cornerRadius(20)
                }
                .padding()
            }
            .background(
                colorScheme == .light
                ? Color(uiColor: .systemGray6)
                : Color(uiColor: .systemBackground)
            )
            .navigationTitle("설정")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        showMenu = true
                    } label: {
                        Image(systemName: "ellipsis")
                    }
                    .confirmationDialog("메뉴", isPresented: $showMenu) {
                        Button("로그아웃", role: .confirm) {
                            Task {
                                let result = await vm.logout(refreshToken: AuthStore.shared.refreshToken ?? "")
                                switch result {
                                case .success:
                                    AuthStore.shared.clearAll()
                                    isLoggedIn = false
                                case .failure(let error):
                                    let toast = ToastValue(
                                        icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                                        message: error.localizedDescription
                                    )
                                    presentToast(toast)
                                }
                            }
                        }
                        Button("탈퇴", role: .destructive) {
                            showDelete = true
                        }
                        Button("취소", role: .cancel) {}
                    }
                }
            }
            .alert("회원 탈퇴", isPresented: $showDelete) {
                Button("탈퇴", role: .destructive) {
                    Task {
                        let result = await vm.withdraw(
                            accessToken: AuthStore.shared.accessToken ?? "",
                            refreshToken: AuthStore.shared.refreshToken ?? ""
                        )
                        switch result {
                        case .success:
                            AuthStore.shared.clearAll()
                            isLoggedIn = false
                        case .failure(let error):
                            let toast = ToastValue(
                                icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                                message: error.localizedDescription
                            )
                            presentToast(toast)
                        }
                    }
                }
                Button("닫기", role: .cancel) { }
            } message: {
                Text("탈퇴 시 모든 정보가 삭제됩니다.\n정말 탈퇴하시겠습니까?")
            }
        }
    }
}


#Preview {
    SettingView()
}
