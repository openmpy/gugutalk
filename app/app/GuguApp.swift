import SwiftUI
import Toasts

@main
struct GuguApp: App {
    
    @AppStorage("isLoggedIn") private var isLoggedIn: Bool = false
    
    var body: some Scene {
        WindowGroup {
            if isLoggedIn == true {
                ContentView()
                    .installToast(position: .top)
            } else {
                LoginView()
                    .installToast(position: .top)
            }
        }
    }
}
