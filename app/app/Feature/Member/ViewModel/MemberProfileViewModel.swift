import SwiftUI
import Combine

@MainActor
final class MemberProfileViewModel: ObservableObject {

    private let memberService = MemberService.shared
    private let memberImageService = MemberImageService.shared
    private let socialService = SocialService.shared
    private let privateImageGrantService = PrivateImageGrantService.shared
    private let chatRoomService = ChatRoomService.shared
    private let stomp = StompManager.shared

    @Published var state: MemberProfileViewState = .idle

    @Published var isLoading: Bool = false
    @Published var member: MemberGetResponse? = nil
    @Published var privateImages: [MemberPrivateImageResponse] = []
    @Published var message: String = ""

    func getMember(memberId: Int64) async {
        guard state != .loading else { return }

        state = .loading
        await fetch(memberId: memberId)
    }

    private func fetch(memberId: Int64) async {
        do {
            let response = try await memberService.getMember(memberId: memberId)

            member = response
            state = .data
        } catch {
            member = nil
            state = .error(error.localizedDescription)
        }
    }

    func toggleLike(memberId: Int64) async throws {
        guard !isLoading else { return }
        guard var member = member else { return }

        isLoading = true
        defer { isLoading = false }

        let response: LikeCountResponse

        if member.isLiked == false {
            response = try await socialService.like(memberId: memberId)
            member.isLiked = true
        } else {
            response = try await socialService.unlike(memberId: memberId)
            member.isLiked = false
        }

        member.likes = response.likes
        self.member = member
    }

    func toggleBlock(memberId: Int64) async throws {
        guard !isLoading else { return }
        guard var member = member else { return }

        isLoading = true
        defer { isLoading = false }

        if member.isBlocked == false {
            try await socialService.block(memberId: memberId)
            member.isBlocked = true

            ToastManager.shared.show("차단하셨습니다.")
        } else {
            try await socialService.unblock(memberId: memberId)
            member.isBlocked = false

            ToastManager.shared.show("차단을 해제하셨습니다.")
        }

        self.member = member
    }

    func togglePrivateImageGrant(memberId: Int64) async throws {
        guard !isLoading else { return }
        guard var member = member else { return }

        isLoading = true
        defer { isLoading = false }

        if member.isPrivateImageGranted == false {
            try await privateImageGrantService.grant(memberId: memberId)
            member.isPrivateImageGranted = true

            ToastManager.shared.show("비밀 사진을 열으셨습니다.")
        } else {
            try await privateImageGrantService.revoke(memberId: memberId)
            member.isPrivateImageGranted = false

            ToastManager.shared.show("비밀 사진을 닫으셨습니다.")
        }

        self.member = member
    }

    func getPrivateImages(granterId: Int64) async throws {
        let response = try await memberImageService.getPrivateImages(granterId: granterId)
        privateImages = response.images
    }

    func createChatRoom(targetId: Int64, content: String) async throws {
        guard !isLoading else { return }
        guard !content.isEmpty else {
            throw AppError("쪽지 내용을 입력해주세요.")
        }

        isLoading = true
        defer { isLoading = false }

        let response = try await chatRoomService.create(targetId: targetId)

        guard let data = try? JSONEncoder().encode(
            MessageSendRequest(
                content: content
            )
        ), let body = String(data: data, encoding: .utf8) else { return }

        stomp.send(
            body: body,
            to: "/app/chat-rooms/\(response.chatRoomId)/messages",
            headers: ["content-type": "application/json"],
        )
    }
}
