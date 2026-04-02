import SwiftUI
import Toasts

@main
struct GuguApp: App {

    @AppStorage("isLoggedIn") private var isLoggedIn: Bool = false

    @StateObject private var stomp = StompManager.shared

    var body: some Scene {
        WindowGroup {
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
    }
}
