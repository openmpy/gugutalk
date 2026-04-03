import SwiftUI

struct IdentifiableVideo: Identifiable {

    let id = UUID()
    let video: URL
}
