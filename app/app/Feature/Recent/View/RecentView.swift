import SwiftUI

struct RecentView: View {

    var body: some View {
        NavigationStack {
            VStack {
                NavigationLink {
                    MemberProfileView()
                } label: {
                    Text("최근")
                }
            }
            .navigationTitle("최근")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button {
                        // 검색
                    } label: {
                        Image(systemName: "magnifyingglass")
                            .font(.title3)
                            .foregroundColor(.primary)
                    }
                }

                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        // 코멘트
                    } label: {
                        Image(systemName: "square.and.pencil")
                            .font(.title3)
                            .foregroundColor(.primary)
                    }
                }
            }
        }
    }
}

#Preview {
    RecentView()
}
