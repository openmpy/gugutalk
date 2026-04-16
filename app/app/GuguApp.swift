import SwiftUI
import GoogleMobileAds
import SimpleToast

@main
struct GuguApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    @AppStorage("isLoggedIn") private var isLoggedIn: Bool = false

    @StateObject private var stomp = StompManager.shared
    @StateObject private var toast = ToastManager.shared

    @State private var showBannedAlert: Bool = false
    @State private var bannedMessage: String = ""

    private let toastOptions = SimpleToastOptions(alignment: .top, hideAfter: 5)

    init() {
        MobileAds.shared.start(completionHandler: nil)
    }

    var body: some Scene {
        WindowGroup {
            ZStack {
                if isLoggedIn == true {
                    ContentView()
                        .onAppear {
                            stomp.connect(accessToken: AuthStore.shared.accessToken ?? "")
                        }
                } else {
                    LoginView()
                }
            }
            .simpleToast(isPresented: $toast.isShow, options: toastOptions) {
                if let data = toast.toast {
                    Label(data.message, systemImage: data.type == .error
                          ? "xmark.circle.fill"
                          : "checkmark.circle.fill"
                    )
                    .padding()
                    .background(data.type == .error ? Color.red.opacity(0.9) : Color.blue.opacity(0.9))
                    .foregroundColor(Color.white)
                    .cornerRadius(20)
                    .padding(.top, 20)
                }
            }
            .alert("정지", isPresented: $showBannedAlert) {
                Button("복사") {
                    UIPasteboard.general.string = bannedMessage
                }
                Button("닫기", role: .cancel) { }
            } message: {
                Text(bannedMessage)
            }
            .onReceive(NotificationCenter.default.publisher(for: .didSessionExpire)) { _ in
                isLoggedIn = false
            }
            .onReceive(NotificationCenter.default.publisher(for: .didDeviceBanned)) { notification in
                bannedMessage = notification.userInfo?["message"] as? String ?? ""
                isLoggedIn = false
                showBannedAlert = true
            }
        }
    }
}
