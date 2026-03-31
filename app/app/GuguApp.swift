import SwiftUI

@main
struct GuguApp: App {

    @AppStorage("isLoggedIn") private var isLoggedIn: Bool = false

    var body: some Scene {
        WindowGroup {
            if isLoggedIn == true {
                ContentView()
            } else {
                LoginView()
            }
        }
    }
}
