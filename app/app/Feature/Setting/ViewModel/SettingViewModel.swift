import SwiftUI
import Combine

@MainActor
final class SettingViewModel: ObservableObject {

    private let authService = AuthService.shared

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
            showErrorAlert = true
            errorMessage = error.localizedDescription
        }
    }
}
