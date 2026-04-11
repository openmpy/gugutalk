import SwiftUI
import Combine

@MainActor
final class ChatSearchViewModel: ObservableObject {

    private let chatRoomService = ChatRoomService.shared
    private let stomp = StompManager.shared

    @Published var state: MemberSearchViewState = .idle
    @Published var isPaging: Bool = false
    @Published var hasNext: Bool = true

    @Published var chatRooms: [ChatRoomGetResponse] = []
    @Published var keyword: String = ""

    private var isLoading: Bool = false
    private var cursorId: Int64?
    private var cursorDateAt: String?
    private var cancellables = Set<AnyCancellable>()

    func search() async {
        guard keyword.count >= 2 else {
            state = .idle
            return
        }
        guard !isLoading else { return }

        isLoading = true
        defer { isLoading = false }

        state = .loading

        do {
            let response = try await chatRoomService.search(
                keyword: keyword,
                cursorId: nil,
                cursorDateAt: nil,
            )

            chatRooms = response.payload
            cursorId = response.nextId
            cursorDateAt = response.nextDateAt
            hasNext = response.hasNext

            state = chatRooms.isEmpty ? .empty : .data
        } catch {
            state = .error(error.localizedDescription)
        }
    }

    func loadMore() async throws {
        guard !isPaging, hasNext else { return }

        isPaging = true
        defer { isPaging = false }

        let response = try await chatRoomService.search(
            keyword: keyword,
            cursorId: cursorId,
            cursorDateAt: cursorDateAt
        )

        chatRooms.append(contentsOf: response.payload)
        cursorId = response.nextId
        cursorDateAt = response.nextDateAt
        hasNext = response.hasNext
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
            sortAt: event.lastMessageAt ?? "",
            unreadCount: event.unreadCount
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
