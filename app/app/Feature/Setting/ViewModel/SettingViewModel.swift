import SwiftUI
import Combine

@MainActor
final class SettingViewModel: ObservableObject {

    private let authService = AuthService.shared
    private let memberService = MemberService.shared

    @Published var isLoading: Bool = false

    func logout(refreshToken: String) async -> Result<Void, Error> {
        guard !isLoading else { return .failure(CancellationError()) }

        isLoading = true
        defer { isLoading = false }

        do {
            try await authService.logout(refreshToken: refreshToken)
            return .success(())
        } catch {
            return .failure(error)
        }
    }

    func withdraw(accessToken: String, refreshToken: String) async -> Result<Void, Error> {
        guard !isLoading else { return .failure(CancellationError()) }

        isLoading = true
        defer { isLoading = false }

        do {
            try await memberService.withdraw(accessToken: accessToken, refreshToken: refreshToken)
            return .success(())
        } catch {
            return .failure(error)
        }
    }
}
