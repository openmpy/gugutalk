import SwiftUI

struct PointView: View {

    @StateObject private var vm = PointViewModel()

    var body: some View {
        VStack {
            VStack {
                switch vm.state {

                case .idle:
                    EmptyView()

                case .loading:
                    ProgressView()

                case .empty:
                    Text("내역이 비어있습니다.")

                case .data:
                    pointSection
                    
                    Spacer()

                case .error(let message):
                    errorSection(message: message)
                }
            }
        }
        .task {
            await vm.get()
        }
        .navigationTitle("포인트")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
    }

    // MARK: - SECTION

    private var pointSection: some View {
        VStack(alignment: .center, spacing: 10) {
            Text("보유 포인트")
                .font(.subheadline)
                .foregroundStyle(.white)

            Text(String(format: "%@ P", (vm.point ?? 0).formatted()))
                .font(.largeTitle.bold())
                .foregroundStyle(.white)
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(.blue, in: RoundedRectangle(cornerRadius: 20))
        .padding()
    }

    private func errorSection(message: String) -> some View {
        VStack {
            Text(message)
                .padding(.bottom)

            Button("다시 시도") {
                Task {
                    await vm.get()
                }
            }
        }
    }
}
