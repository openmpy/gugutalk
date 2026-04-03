import SwiftUI
import Combine

@MainActor
final class SettingViewModel: ObservableObject {

    private let authService = AuthService.shared
    private let memberService = MemberService.shared

    @Published var state: SettingViewState = .idle

    func logout() async {
        state = .loading

        if let refreshToken = AuthStore.shared.refreshToken {
            try? await authService.logout(refreshToken: refreshToken)
        }

        AuthStore.shared.clearAll()
        state = .success(.logout)
    }

    func withdraw() async {
        guard case .loading = state else {
            state = .loading
            return await performWithdraw()
        }
    }

    private func performWithdraw() async {
        guard
            let accessToken = AuthStore.shared.accessToken,
            let refreshToken = AuthStore.shared.refreshToken
        else {
            state = .error("토큰 값을 찾을 수 없습니다.")
            return
        }

        do {
            try await memberService.withdraw(
                accessToken: accessToken,
                refreshToken: refreshToken
            )
            
            AuthStore.shared.clearAll()
            state = .success(.withdraw)
        } catch {
            state = .error(error.localizedDescription)
        }
    }
}
