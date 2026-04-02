import SwiftUI
import Combine

@MainActor
final class ChatViewModel: ObservableObject {

    private let chatRoomService = ChatRoomService.shared

    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true

    private var cursorId: Int64?
    private var cursorDateAt: String?
}
