import SwiftUI
import Combine

@MainActor
final class MemberProfileViewModel: ObservableObject {

    private let memberService = MemberService.shared
    private let socialService = SocialService.shared
    private let privateImageGrantService = PrivateImageGrantService.shared

    @Published var isLoading: Bool = false
    @Published var member: MemberGetResponse? = nil
    @Published var isLiked: Bool = false
    @Published var isBlocked: Bool = false
    @Published var isPrivateImageGranted: Bool = false

    func getMember(memberId: Int64) async -> Result<Void, Error> {
        guard !isLoading else { return .failure(CancellationError()) }

        isLoading = true
        defer { isLoading = false }

        do {
            let response = try await memberService.getMember(memberId: memberId)
            member = response
            isLiked = response.isLiked
            isBlocked = response.isBlocked
            isPrivateImageGranted = response.isPrivateImageGranted
            return .success(())
        } catch {
            return .failure(error)
        }
    }

    func toggleLike() async -> Result<Void, Error> {
        guard let memberId = member?.memberId else { return .success(()) }

        isLiked.toggle()

        do {
            let response: LikeCountResponse
            if isLiked {
                response = try await socialService.like(memberId: memberId)
            } else {
                response = try await socialService.unlike(memberId: memberId)
            }
            member?.likes = response.likes
            return .success(())
        } catch {
            isLiked.toggle()
            return .failure(error)
        }
    }

    func toggleBlock() async -> Result<Void, Error> {
        guard let memberId = member?.memberId else { return .success(()) }

        isBlocked.toggle()

        do {
            if isBlocked {
                try await socialService.block(memberId: memberId)
            } else {
                try await socialService.unblock(memberId: memberId)
            }
            return .success(())
        } catch {
            isBlocked.toggle()
            return .failure(error)
        }
    }

    func togglePrivateImageGrant() async -> Result<Void, Error> {
        guard let memberId = member?.memberId else { return .success(()) }

        isPrivateImageGranted.toggle()
        
        do {
            if isPrivateImageGranted {
                try await privateImageGrantService.grant(memberId: memberId)
            } else {
                try await privateImageGrantService.revoke(memberId: memberId)
            }
            return .success(())
        } catch {
            isPrivateImageGranted.toggle()
            return .failure(error)
        }
    }
}
