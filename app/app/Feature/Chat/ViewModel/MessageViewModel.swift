import SwiftUI
import Combine

@MainActor
final class MessageViewModel: ObservableObject {

    private let chatRoomService = ChatRoomService.shared
    private let messageService = MessageService.shared
    private let messageImageService = MessageImageService.shared
    private let s3Service = S3Service.shared
    private let stomp = StompManager.shared

    @Published var state: MessageViewState = .idle
    @Published var isRoomDelete: Bool = false
    @Published var isPaging: Bool = false
    @Published var isLoading: Bool = false
    @Published var isUploading = false
    @Published var hasNext: Bool = true

    @Published var messages: [MessageGetResponse] = []
    @Published var member: MessageGetMemberResponse? = nil
    @Published var images: [IdentifiableImage] = []
    @Published var videos: [IdentifiableVideo] = []
    @Published var message: String = ""

    private var cursorId: Int64?
    private var cursorDateAt: String?
    private var cancellables = Set<AnyCancellable>()

    func gets(chatRoomId: Int64) async {
        state = .loading
        await fetchFirstPage(chatRoomId: chatRoomId)
    }

    private func fetchFirstPage(chatRoomId: Int64) async {
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

            state = messages.isEmpty ? .empty : .data
        } catch {
            state = .error(error.localizedDescription)
        }
    }

    func loadMore(chatRoomId: Int64) async throws {
        guard !isPaging, hasNext else { return }

        isPaging = true
        defer { isPaging = false }

        let response = try await messageService.gets(
            chatRoomId: chatRoomId,
            cursorId: cursorId,
            cursorDateAt: cursorDateAt
        )

        messages.append(contentsOf: response.payload)
        cursorId = response.nextId
        cursorDateAt = response.nextDateAt
        hasNext = response.hasNext
    }

    func send(chatRoomId: Int64) async throws {
        guard !isLoading else { return }
        guard !message.isEmpty else { return }

        isLoading = true
        defer { isLoading = false }

        guard let data = try? JSONEncoder().encode(
            MessageSendRequest(
                content: message
            )
        ), let body = String(data: data, encoding: .utf8) else { return }

        stomp.send(
            body: body,
            to: "/app/chat-rooms/\(chatRoomId)/messages",
            headers: ["content-type": "application/json"],
        )
    }

    func sendMedia(chatRoomId: Int64) async throws {
        guard !isUploading else { return }

        isUploading = true
        defer { isUploading = false }

        let imageRequests = images.map { _ in
            MessageGetPresignedUrlRequest(contentType: "image/jpeg")
        }
        let videoRequests = videos.map { _ in
            MessageGetPresignedUrlRequest(contentType: "video/quicktime")
        }

        let response = try await messageImageService.getPresignedUrls(
            chatRoomId: chatRoomId,
            medias: imageRequests + videoRequests
        )

        let imagePresigned = Array(response.presigned.prefix(images.count))
        let videoPresigned = Array(response.presigned.suffix(videos.count))

        // 이미지 업로드
        for (it, presigned) in zip(images, imagePresigned) {
            let resized = it.image.resized(toMaxDimension: 480)
            guard let data = resized.compressedData(maxBytes: 300_000) else { continue }
            try await s3Service.uploadImageToS3(data: data, presigned: presigned)
        }

        // 동영상 업로드
        for (it, presigned) in zip(videos, videoPresigned) {
            try await s3Service.uploadVideoToS3(fileURL: it.video, presigned: presigned)
            try? FileManager.default.removeItem(at: it.video)
        }

        // 메시지 전송
        guard let data = try? JSONEncoder().encode(
            MessageSendMediaRequest(
                imageKeys: imagePresigned.map(\.key),
                videoKeys: videoPresigned.map(\.key)
            )
        ), let body = String(data: data, encoding: .utf8) else { return }
        
        stomp.send(
            body: body,
            to: "/app/chat-rooms/\(chatRoomId)/medias",
            headers: ["content-type": "application/json"]
        )

        images = []
        videos = []
    }

    func getMember(chatRoomId: Int64) async throws {
        let response = try await messageService.getMember(chatRoomId: chatRoomId)
        member = response
    }

    func markAsRead(chatRoomId: Int64) async throws {
        try await chatRoomService.markAsRead(chatRoomId: chatRoomId)
    }

    // MARK: - 이벤트

    func subscribe(chatRoomId: Int64) {
        stomp.send(
            body: "",
            to: "/app/chat-rooms/\(chatRoomId)/enter",
            headers: ["content-type": "application/json"],
        )

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
                case "DELETE_CHAT_ROOM":
                    isRoomDelete = true
                default:
                    break
                }
            }
            .store(in: &cancellables)
    }

    func unsubscribe() {
        stomp.send(
            body: "",
            to: "/app/chat-rooms/leave",
            headers: ["content-type": "application/json"],
        )

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
