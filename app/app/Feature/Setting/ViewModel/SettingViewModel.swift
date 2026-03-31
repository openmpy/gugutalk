import SwiftUI
import Combine

@MainActor
final class SettingViewModel: ObservableObject {

    private let authService = AuthService.shared
    private let memberService = MemberService.shared

    @Published var showErrorAlert: Bool = false
    @Published var errorMessage: String = ""
    @Published var isLoading: Bool = false

    func logout(refreshToken: String) async {
        guard !isLoading else { return }

        isLoading = true
        defer { isLoading = false }

        do {
            try await authService.logout(refreshToken: refreshToken)
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    func withdraw(accessToken: String, refreshToken: String) async -> Bool {
        guard !isLoading else { return false }

        isLoading = true
        defer { isLoading = false }

        do {
            try await memberService.withdraw(accessToken: accessToken, refreshToken: refreshToken)
            return true
        } catch {
            showErrorAlert = true
            errorMessage = error.localizedDescription
            return false
        }
    }
}
