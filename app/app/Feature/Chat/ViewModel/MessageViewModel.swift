import SwiftUI
import Combine

@MainActor
final class MessageViewModel: ObservableObject {

    private let chatRoomService = ChatRoomService.shared
    private let stomp = StompManager.shared

    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true
    @Published var isRoomDelete: Bool = false

    private var cursorId: Int64?
    private var cursorDateAt: String?
    private var cancellables = Set<AnyCancellable>()

//    func sendMessage(chatRoomId: Int64, content: String) async -> Result<Void, Error> {
//        guard let data = try? JSONEncoder().encode(
//            MessageSendRequest(
//                content: content,
//                type: "TEXT"
//            )
//        ), let body = String(data: data, encoding: .utf8) else { return .failure(CancellationError()) }
//
//        stomp.send(
//            body: body,
//            to: "/app/chat/\(chatRoomId)",
//            headers: ["content-type": "application/json"],
//        )
//        return .success(())
//    }
//
//    func subscribeRoom(chatRoomId: Int64) {
//        stomp.publisher(for: "/topic/chat/\(chatRoomId)")
//            .compactMap { try? JSONDecoder().decode(MessageGetResponse.self, from: Data($0.utf8)) }
//            .receive(on: DispatchQueue.main)
//            .sink { [weak self] message in
//                if message.type == "SYSTEM" {
//                    self?.isRoomDelete = true
//                } else {
//                    self?.messages.insert(message, at: 0)
//                }
//            }
//            .store(in: &cancellables)
//    }
//
//    func unsubscribeRoom() {
//        cancellables.removeAll()
//    }
}
