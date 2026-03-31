import SwiftUI
import Combine

@MainActor
final class SignupViewModel: ObservableObject {

    private let authService = AuthService.shared

    @Published var isLoading: Bool = false

    func sendCodeVerificationCode(phoneNumber: String) async -> Result<Void, Error> {
        guard !isLoading else { return .failure(CancellationError()) }

        isLoading = true
        defer { isLoading = false }
        
        do {
            try await authService.sendCodeVerificationCode(phoneNumber: phoneNumber)
            return .success(())
        } catch {
            return .failure(error)
        }
    }

    func signup(
        phoneNumber: String,
        verificationCode: String,
        password: String,
        gender: String
    ) async -> Result<Void, Error> {
        guard !isLoading else { return .failure(CancellationError()) }

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
            return .success(())
        } catch {
            return .failure(error)
        }
    }
}
