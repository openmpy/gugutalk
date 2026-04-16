import SwiftUI

struct ContentView: View {

    @ObservedObject private var router = NotificationRouter.shared

    @State private var selectedTab = 0

    var body: some View {
        TabView(selection: $selectedTab) {
            Tab("최근", systemImage: "clock", value: 0) {
                RecentView()
            }
            Tab("위치", systemImage: "location", value: 1) {
                LocationView()
            }
            Tab("채팅", systemImage: "bubble", value: 2) {
                ChatView()
            }
            Tab("설정", systemImage: "gear", value: 3) {
                SettingView()
            }
        }
        .onChange(of: router.pendingChat) { _, newValue in
            guard newValue != nil else { return }
            
            selectedTab = 2
        }
        .onAppear {
            if router.pendingChat != nil {
                selectedTab = 2
            }
        }
    }
}
