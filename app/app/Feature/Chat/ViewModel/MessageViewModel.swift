import SwiftUI
import Combine

@MainActor
final class MessageViewModel: ObservableObject {
    
    private let chatRoomService = ChatRoomService.shared
    private let messageService = MessageService.shared
    private let stomp = StompManager.shared
    
    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true
    @Published var messages: [MessageGetResponse] = []
    
    private var cursorId: Int64?
    private var cursorDateAt: String?
    private var cancellables = Set<AnyCancellable>()
    
    func gets(chatRoomId: Int64) async -> Result<Void, Error> {
        hasNext = true
        
        guard !isLoading else { return .success(()) }
        guard hasNext else { return .success(()) }
        
        isLoading = true
        defer { isLoading = false }
        
        do {
            let response = try await messageService.gets(
                chatRoomId: chatRoomId,
                cursorId: nil,
                cursorDateAt: nil
            )
            messages = response.payload
            cursorId = response.nextId
            cursorDateAt = response.nextDateAt
            hasNext = response.hasNext
            return .success(())
        } catch {
            return .failure(error)
        }
    }
    
    func loadMore(chatRoomId: Int64) async -> Result<Void, Error> {
        guard !isLoading else { return .success(()) }
        guard hasNext else { return .success(()) }
        
        isLoading = true
        defer { isLoading = false }
        
        do {
            let response = try await messageService.gets(
                chatRoomId: chatRoomId,
                cursorId: cursorId,
                cursorDateAt: cursorDateAt
            )
            messages.append(contentsOf: response.payload)
            cursorId = response.nextId
            cursorDateAt = response.nextDateAt
            hasNext = response.hasNext
            return .success(())
        } catch {
            return .failure(error)
        }
    }
    
    func send(chatRoomId: Int64, content: String, type: String) async -> Result<Void, Error> {
        guard !isLoading else { return .failure(CancellationError()) }
        
        isLoading = true
        defer { isLoading = false }
        
        guard let data = try? JSONEncoder().encode(
            MessageSendRequest(
                content: content,
                type: type
            )
        ), let body = String(data: data, encoding: .utf8) else { return .failure(CancellationError()) }
        
        stomp.send(
            body: body,
            to: "/app/chat-rooms/\(chatRoomId)/messages",
            headers: ["content-type": "application/json"],
        )
        return .success(())
    }
    
    func subscribe(chatRoomId: Int64) {
        stomp.publisher(for: "/topic/chat-rooms/\(chatRoomId)")
            .receive(on: DispatchQueue.main)
            .sink { [weak self] message in
                guard let self = self else { return }
                guard let data = message.data(using: .utf8) else { return }
                guard let base = try? JSONDecoder().decode(ChatBaseEvent.self, from: data) else {
                    return
                }
                
                switch base.eventType {
                case "SEND_MESSAGE":
                    if let event = try? JSONDecoder().decode(ChatEvent<MessageSendEvent>.self, from: data),
                       let payload = event.payload {
                        insertMessage(payload)
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
    
    private func insertMessage(_ event: MessageSendEvent) {
        let newMessage = MessageGetResponse(
            messageId: event.messageId,
            senderId: event.senderId,
            content: event.content,
            type: event.type,
            createdAt: event.createdAt
        )
        
        messages.insert(newMessage, at: 0)
    }
}
