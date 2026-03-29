import SwiftUI

struct LocationView: View {

    var body: some View {
        NavigationStack {
            VStack {
                NavigationLink {
                    MemberProfileView()
                } label: {
                    Text("위치")
                }
            }
            .navigationTitle("위치")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

#Preview {
    LocationView()
}
