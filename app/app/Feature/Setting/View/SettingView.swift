import SwiftUI

struct SettingView: View {

    var body: some View {
        NavigationStack {
            VStack {
                NavigationLink {
                    MemberProfileView()
                } label: {
                    Text("설정")
                }
            }
            .navigationTitle("설정")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        // 더보기
                    } label: {
                        Image(systemName: "ellipsis")
                            .font(.title3)
                            .foregroundColor(.primary)
                    }
                }
            }
        }
    }
}

#Preview {
    SettingView()
}
