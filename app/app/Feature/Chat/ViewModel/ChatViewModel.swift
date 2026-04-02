import SwiftUI
import Combine

@MainActor
final class ChatViewModel: ObservableObject {

    private let chatRoomService = ChatRoomService.shared
    private let stomp = StompManager.shared

    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true
    @Published var chatRooms: [ChatRoomGetResponse] = []

    private var cursorId: Int64?
    private var cursorDateAt: String?
    private var cancellables = Set<AnyCancellable>()

    func gets() async -> Result<Void, Error> {
        hasNext = true

        guard !isLoading else { return .success(()) }
        guard hasNext else { return .success(()) }

        isLoading = true
        defer { isLoading = false }

        do {
            let response = try await chatRoomService.gets(
                cursorId: nil,
                cursorDateAt: nil
            )
            chatRooms = response.payload
            cursorId = response.nextId
            cursorDateAt = response.nextDateAt
            hasNext = response.hasNext
            return .success(())
        } catch {
            return .failure(error)
        }
    }

    func loadMore() async -> Result<Void, Error> {
        guard !isLoading else { return .success(()) }
        guard hasNext else { return .success(()) }

        isLoading = true
        defer { isLoading = false }

        do {
            let response = try await chatRoomService.gets(
                cursorId: cursorId,
                cursorDateAt: cursorDateAt
            )
            chatRooms.append(contentsOf: response.payload)
            cursorId = response.nextId
            cursorDateAt = response.nextDateAt
            hasNext = response.hasNext
            return .success(())
        } catch {
            return .failure(error)
        }
    }

    func delete(chatRoomId: Int64) async -> Result<Void, Error> {
        guard !isLoading else { return .success(()) }

        isLoading = true
        defer { isLoading = false }

        do {
            try await chatRoomService.delete(chatRoomId: chatRoomId)
            chatRooms.removeAll { $0.chatRoomId == chatRoomId }
            return .success(())
        } catch {
            return .failure(error)
        }
    }

    // MARK: - 이벤트

    func subscribe() {
        stomp.publisher(for: "/user/queue/chat-rooms")
            .receive(on: DispatchQueue.main)
            .sink { [weak self] message in
                guard let self = self else { return }
                guard let data = message.data(using: .utf8) else { return }
                guard let base = try? JSONDecoder().decode(ChatBaseEvent.self, from: data) else {
                    return
                }

                switch base.eventType {
                case "SEND_CHAT_ROOM":
                    if let event = try? JSONDecoder().decode(ChatEvent<ChatRoomSendEvent>.self, from: data),
                       let payload = event.payload {
                        self.upsertChatRoom(payload)
                    }
                case "DELETE_CHAT_ROOM":
                    if let event = try? JSONDecoder().decode(ChatEvent<ChatRoomDeleteEvent>.self, from: data),
                       let payload = event.payload {
                        self.deleteChatRoom(payload.chatRoomId)
                    }
                default:
                    break
                }
            }
            .store(in: &cancellables)
    }

    func unsubscribe() {
        cancellables.removeAll()
    }

    private func upsertChatRoom(_ event: ChatRoomSendEvent) {
        let newRoom = ChatRoomGetResponse(
            chatRoomId: event.chatRoomId,
            targetId: event.senderId,
            nickname: event.nickname,
            profileUrl: event.profileUrl,
            lastMessage: event.lastMessage,
            lastMessageAt: event.lastMessageAt,
            sortAt: event.lastMessageAt ?? ""
        )

        if let index = chatRooms.firstIndex(where: { $0.chatRoomId == event.chatRoomId }) {
            chatRooms.remove(at: index)
        }
        chatRooms.insert(newRoom, at: 0)
    }

    private func deleteChatRoom(_ chatRoomId: Int64) {
        self.chatRooms.removeAll { $0.chatRoomId == chatRoomId }
    }
}
