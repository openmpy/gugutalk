import SwiftUI
import Combine

@MainActor
final class MyProfileViewModel: ObservableObject {

    private let memberService = MemberService.shared

    @Published var isLoading: Bool = false
    @Published var member: MemberGetMeResponse? = nil

    func getMe() async -> Result<Void, Error> {
        guard !isLoading else { return .failure(CancellationError()) }

        isLoading = true
        defer { isLoading = false }

        do {
            member = try await memberService.getMe()
            return .success(())
        } catch {
            return .failure(error)
        }
    }
}
