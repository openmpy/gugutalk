import SwiftUI

struct MemberProfileImage: View {
    
    var body: some View {
        TabView {
            ForEach(0..<10) { i in
                Image(systemName: "person.fill")
                    .resizable()
                    .scaledToFit()
                    .padding(100)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .foregroundColor(Color(.systemGray3))
                    .background(Color(.systemGray6))
                    .tag(i)
            }
        }
        .tabViewStyle(PageTabViewStyle())
        .aspectRatio(4/3, contentMode: .fit)
        .clipped()
    }
}
