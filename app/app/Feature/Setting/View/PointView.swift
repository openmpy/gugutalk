import SwiftUI

struct PointView: View {

    var body: some View {
        VStack {
            VStack(alignment: .center, spacing: 10) {
                Text("보유 포인트")
                    .font(.subheadline)
                    .foregroundStyle(.white)

                Text(String(format: "%@ P", (10000).formatted()))
                    .font(.largeTitle.bold())
                    .foregroundStyle(.white)
            }
            .frame(maxWidth: .infinity)
            .padding()
            .background(.blue, in: RoundedRectangle(cornerRadius: 20))
            .padding()

            Spacer()
        }
        .navigationTitle("포인트")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
    }
}

#Preview {
    NavigationStack {
        PointView()
    }
}
