import SwiftUI
import AVKit

struct VideoPlayerView: View {

    let url: URL

    @State private var player: AVPlayer

    init(url: URL) {
        self.url = url
        _player = State(initialValue: AVPlayer(url: url))
    }

    var body: some View {
        VStack {
            VideoPlayer(player: player)
                .ignoresSafeArea()
        }
    }
}
