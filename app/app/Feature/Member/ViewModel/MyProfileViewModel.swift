import SwiftUI
import Combine

@MainActor
final class MyProfileViewModel: ObservableObject {

    private let memberService = MemberService.shared

    @Published var showErrorAlert: Bool = false
    @Published var errorMessage: String = ""
    @Published var isLoading: Bool = false

    @Published var member: MemberGetMeResponse? = nil

    func getMe() async {
        guard !isLoading else { return }

        isLoading = true
        defer { isLoading = false }

        do {
            member = try await memberService.getMe()
            print(member?.images)
        } catch {
            showErrorAlert = true
            errorMessage = error.localizedDescription
        }
    }
}
