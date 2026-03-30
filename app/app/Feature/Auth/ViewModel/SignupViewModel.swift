import SwiftUI
import Combine

@MainActor
final class SignupViewModel: ObservableObject {

    private let authService = AuthService.shared

    @Published var showErrorAlert: Bool = false
    @Published var errorMessage: String = ""
    @Published var isLoading: Bool = false

    func sendCodeVerificationCode(phoneNumber: String) async -> Bool {
        guard !isLoading else { return false }

        isLoading = true
        defer { isLoading = false }

        do {
            try await authService.sendCodeVerificationCode(phoneNumber: phoneNumber)
            return true
        } catch {
            showErrorAlert = true
            errorMessage = error.localizedDescription
            return false
        }
    }

    func signup(
        phoneNumber: String,
        verificationCode: String,
        password: String,
        gender: String
    ) async -> Bool {
        guard !isLoading else { return false }

        isLoading = true
        defer { isLoading = false }

        do {
            let uuid = AuthStore.shared.uuid ?? UUID().uuidString
            let response = try await authService.signup(
                uuid: uuid,
                phoneNumber: phoneNumber,
                verificationCode: verificationCode,
                password: password,
                gender: gender
            )

            AuthStore.shared.memberId = response.memberId
            AuthStore.shared.uuid = uuid
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
