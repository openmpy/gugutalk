import SwiftUI

struct IdentifiableURL: Identifiable {

    let id = UUID()
    let url: URL
}
