import SwiftUI
import Combine
import FirebaseMessaging

@MainActor
final class LoginViewModel: ObservableObject {

    private let authService = AuthService.shared
    private let fcmService = FcmService.shared

    @Published var isLoading: Bool = false

    @Published var phoneNumber: String = ""
    @Published var password: String = ""

    var isPhoneNumberValid: Bool {
        phoneNumber.starts(with: "010") && phoneNumber.count == 11
    }
    var isSubmittable: Bool {
        isPhoneNumberValid && !password.isEmpty
    }

    func login() async throws {
        guard !isLoading else { return }

        guard let uuid = AuthStore.shared.uuid else {
            throw AppError("앱을 다시 시작해주시길 바랍니다.")
        }

        isLoading = true
        defer { isLoading = false }

        let response = try await authService.login(
            uuid: uuid,
            phoneNumber: phoneNumber,
            password: password
        )

        AuthStore.shared.memberId = response.memberId
        AuthStore.shared.accessToken = response.accessToken
        AuthStore.shared.refreshToken = response.refreshToken

        if let token = Messaging.messaging().fcmToken {
            try await fcmService.register(
                token: token,
                uuid: uuid,
                memberId: response.memberId
            )
        }
    }
}
