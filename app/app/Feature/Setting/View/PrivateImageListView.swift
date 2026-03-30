import SwiftUI

struct PrivateImageListView: View {

    var body: some View {
        VStack {
            ScrollView {
                LazyVStack {
                    ForEach(0..<10) { _ in
                        MemberSettingRow(
                            nickname: "닉네임",
                            createdAt: "2026-03-30T12:00:00.0000",
                            gender: "MALE",
                            age: 20
                        )
                    }
                }
            }
        }
        .navigationTitle("비밀 사진 목록")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
    }
}

#Preview {
    NavigationStack {
        PrivateImageListView()
    }
}
