import SwiftUI
import Combine

@MainActor
final class MyProfileViewModel: ObservableObject {

    private let memberService = MemberService.shared

    @Published var state: MyProfileViewState = .idle

    @Published var member: MemberGetMeResponse? = nil

    func getMe() async {
        guard state != .loading else { return }

        state = .loading
        await fetch()
    }

    private func fetch() async {
        do {
            let response = try await memberService.getMe()

            member = response
            state = .data
        } catch {
            member = nil
            state = .error(error.localizedDescription)
        }
    }
}
