import SwiftUI
import Combine

@MainActor
final class LoginViewModel: ObservableObject {

    private let authService = AuthService.shared

    @Published var showErrorAlert: Bool = false
    @Published var errorMessage: String = ""
    @Published var isLoading: Bool = false

    func login(
        phoneNumber: String,
        password: String
    ) async -> Bool {
        guard !isLoading else { return false }

        isLoading = true
        defer { isLoading = false }

        do {
            let response = try await authService.login(
                phoneNumber: phoneNumber,
                password: password
            )

            AuthStore.shared.memberId = response.memberId
            AuthStore.shared.accessToken = response.accessToken
            AuthStore.shared.refreshToken = response.refreshToken
            return true
        } catch {
            showErrorAlert = true
            errorMessage = error.localizedDescription
            return false
        }
    }
}
