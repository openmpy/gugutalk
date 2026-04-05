import SwiftUI
import MessageUI

struct SettingView: View {

    @AppStorage("isLoggedIn") private var isLoggedIn: Bool = false

    @StateObject private var vm = SettingViewModel()

    @Environment(\.colorScheme) var colorScheme

    @State private var showMenu: Bool = false
    @State private var showDelete: Bool = false
    @State private var safariItem: IdentifiableURL? = nil

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 15) {
                    profileSection
                    discoverySection
                    pointSection
                    noticeSection
                }
                .padding()
            }
            .overlay {
                if vm.state == .loading {
                    LoadingOverlay()
                }
            }
            .background(
                colorScheme == .light
                ? Color(uiColor: .systemGray6)
                : Color(uiColor: .systemBackground)
            )
            .navigationTitle("설정")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                toolbar
            }
            .sheet(item: $safariItem) { item in
                SafariView(url: item.url)
            }
            .alert("회원 탈퇴", isPresented: $showDelete) {
                Button("탈퇴", role: .destructive) {
                    Task {
                        await vm.withdraw()
                    }
                }
                Button("닫기", role: .cancel) { }
            } message: {
                Text("탈퇴 시 모든 정보가 삭제됩니다.\n정말 탈퇴하시겠습니까?")
            }
            .onChange(of: vm.state) { _, state in
                switch state {

                case .success(let action):
                    switch action {
                    case .logout, .withdraw:
                        isLoggedIn = false
                    }

                case .error(let message):
                    ToastManager.shared.show(message, type: .error)

                default:
                    break
                }
            }
        }
    }

    // MARK: - SECTION

    private var profileSection: some View {
        VStack(spacing: 0) {
            NavigationLink {
                MyProfieView()
            } label : {
                SettingRow(title: "내 프로필", icon: "person.crop.circle.fill", color: .blue)
            }
        }
        .cornerRadius(20)
    }

    private var discoverySection: some View {
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
    }

    private var pointSection: some View {
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
    }

    private var noticeSection: some View {
        VStack(spacing: 0) {
            Button {
            } label: {
                SettingRow(title: "문의사항", icon: "envelope.fill", color: .indigo)
            }

            Button {
            } label: {
                SettingRow(title: "버그제보", icon: "exclamationmark.triangle.fill", color: .red)
            }

            SettingRow(title: "서비스 이용약관", icon: "doc.text.fill", color: .gray)
                .onTapGesture {
                    safariItem = IdentifiableURL(url: URL(string: "https://www.notion.so/339495438f1c805cbe36fc2cbd3aedad?source=copy_link")!)
                }

            SettingRow(title: "개인정보 취급방침", icon: "shield.fill", color: .green)
                .onTapGesture {
                    safariItem = IdentifiableURL(url: URL(string: "https://www.notion.so/339495438f1c801b85a4d8ac01eee33e?source=copy_link")!)
                }
        }
        .cornerRadius(20)
    }

    private var toolbar: some ToolbarContent {
        ToolbarItem(placement: .topBarTrailing) {
            Button {
                showMenu = true
            } label: {
                Image(systemName: "ellipsis")
            }
            .confirmationDialog("메뉴", isPresented: $showMenu) {
                Button("로그아웃", role: .confirm) {
                    Task {
                        await vm.logout()
                    }
                }

                Button("탈퇴", role: .destructive) {
                    showDelete = true
                }

                Button("취소", role: .cancel) {}
            }
        }
    }
}
