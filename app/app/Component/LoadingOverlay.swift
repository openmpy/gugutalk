import SwiftUI

struct LoadingOverlay: View {
    
    var body: some View {
        ZStack {
            Color.black.opacity(0.4)
                .ignoresSafeArea()

            ProgressView()
        }
    }
}
