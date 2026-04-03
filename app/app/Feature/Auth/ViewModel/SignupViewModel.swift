import SwiftUI
import Combine

@MainActor
final class SignupViewModel: ObservableObject {

    private let authService = AuthService.shared

    @Published var isLoading: Bool = false
    @Published var isSent: Bool = false
    @Published var timeRemaining: Int = 0

    @Published var phoneNumber: String = ""
    @Published var verificationCode: String = ""
    @Published var password: String = ""
    @Published var password2: String = ""
    @Published var selectedGender: String = "MALE"

    private var timerTask: Task<Void, Never>?

    var isPhoneNumberValid: Bool {
        phoneNumber.starts(with: "010") && phoneNumber.count == 11
    }
    var isPasswordMatched: Bool {
        password == password2
    }
    var isSubmittable: Bool {
        isPhoneNumberValid &&
        !verificationCode.isEmpty &&
        !password.isEmpty &&
        !password2.isEmpty
    }

    func sendVerificationCode() async throws {
        guard !isLoading else { return }

        isLoading = true
        defer { isLoading = false }

        try await authService.sendCodeVerificationCode(phoneNumber: phoneNumber)
        startTimer()
    }

    func signup() async throws {
        guard !isLoading else { return }
        guard isPasswordMatched else {
            throw AppError("비밀번호가 일치하지 않습니다.")
        }

        isLoading = true
        defer { isLoading = false }

        let uuid = AuthStore.shared.uuid ?? UUID().uuidString
        let response = try await authService.signup(
            uuid: uuid,
            phoneNumber: phoneNumber,
            verificationCode: verificationCode,
            password: password,
            gender: selectedGender
        )

        AuthStore.shared.memberId = response.memberId
        AuthStore.shared.uuid = uuid
        AuthStore.shared.accessToken = response.accessToken
        AuthStore.shared.refreshToken = response.refreshToken
    }

    private func startTimer() {
        isSent = true
        timeRemaining = 300

        timerTask?.cancel()
        timerTask = Task {
            while timeRemaining > 0 {
                try? await Task.sleep(for: .seconds(1))
                guard !Task.isCancelled else { return }
                timeRemaining -= 1
            }
            isSent = false
        }
    }

    func invalidateTimer() {
        timerTask?.cancel()
        timerTask = nil
    }
}
