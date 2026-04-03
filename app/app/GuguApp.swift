import SwiftUI
import SimpleToast
import Toasts

@main
struct GuguApp: App {

    @AppStorage("isLoggedIn") private var isLoggedIn: Bool = false

    @StateObject private var stomp = StompManager.shared
    @StateObject private var toast = ToastManager.shared

    private let toastOptions = SimpleToastOptions(alignment: .top, hideAfter: 5)

    var body: some Scene {
        WindowGroup {
            ZStack {
                if isLoggedIn == true {
                    ContentView()
                        .onAppear {
                            stomp.connect(accessToken: AuthStore.shared.accessToken ?? "")
                        }
                        .installToast(position: .top)
                } else {
                    LoginView()
                        .installToast(position: .top)
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
        }
    }
}
