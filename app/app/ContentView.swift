import SwiftUI

struct ContentView: View {

    var body: some View {
        TabView {
            Tab("최근", systemImage: "clock") {
                RecentView()
            }
            Tab("위치", systemImage: "location") {
                LocationView()
            }
            Tab("채팅", systemImage: "bubble") {
                ChatView()
            }
            Tab("설정", systemImage: "gear") {
                SettingView()
            }
        }
    }
}

#Preview {
    ContentView()
}
