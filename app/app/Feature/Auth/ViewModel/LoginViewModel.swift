import SwiftUI
import Combine

@MainActor
final class LoginViewModel: ObservableObject {

    private let authService = AuthService.shared

    @Published var isLoading: Bool = false

    func login(
        phoneNumber: String,
        password: String
    ) async -> Result<Void, Error> {
        guard !isLoading else { return .failure(CancellationError()) }

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
            return .success(())
        } catch {
            return .failure(error)
        }
    }
}
