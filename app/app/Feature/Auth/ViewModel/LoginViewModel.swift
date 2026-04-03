import SwiftUI
import Combine

@MainActor
final class LoginViewModel: ObservableObject {

    @Published var isLoading: Bool = false

    @Published var phoneNumber: String = ""
    @Published var password: String = ""

    private let authService = AuthService.shared

    var isPhoneNumberValid: Bool {
        phoneNumber.starts(with: "010") && phoneNumber.count == 11
    }
    var isSubmittable: Bool {
        isPhoneNumberValid && !password.isEmpty
    }

    func login() async throws {
        guard !isLoading else { return }

        isLoading = true
        defer { isLoading = false }

        let response = try await authService.login(
            phoneNumber: phoneNumber,
            password: password
        )

        AuthStore.shared.memberId = response.memberId
        AuthStore.shared.accessToken = response.accessToken
        AuthStore.shared.refreshToken = response.refreshToken
    }
}
