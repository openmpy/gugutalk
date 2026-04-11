import SwiftUI
import Combine

@MainActor
final class LocationViewModel: ObservableObject {

    private let locaionService = LocationService.shared
    private let memberService = MemberService.shared

    @Published var state: LocationViewState = .idle
    @Published var isLoading: Bool = false
    @Published var isPaging: Bool = false
    @Published var hasNext: Bool = true
    @Published var hasLoaded: Bool = false

    @Published var members: [MemberDiscoveryResponse] = []
    @Published var selectGender: String = "ALL"

    private var cursorId: Int64?
    private var cursorDistance: Double?

    func getLocationMembers() async {
        guard !hasLoaded else { return }

        state = .loading
        await fetchFirstPage()
        hasLoaded = true
    }

    func refresh() async {
        state = .loading
        await fetchFirstPage()
    }

    private func fetchFirstPage() async {
        do {
            let response = try await locaionService.getLocationMembers(
                gender: selectGender,
                cursorId: nil,
                cursorDistance: nil
            )

            members = response.payload
            cursorId = response.nextId
            cursorDistance = response.nextDistance
            hasNext = response.hasNext

            state = members.isEmpty ? .empty : .data
        } catch {
            state = .error(error.localizedDescription)
        }
    }

    func loadMoreLocationMembers() async throws {
        guard !isPaging, hasNext else { return }

        isPaging = true
        defer { isPaging = false }

        let response = try await locaionService.getLocationMembers(
            gender: selectGender,
            cursorId: cursorId,
            cursorDistance: cursorDistance
        )

        members.append(contentsOf: response.payload)
        cursorId = response.nextId
        cursorDistance = response.nextDistance
        hasNext = response.hasNext
    }

    func bump(latitude: Double?, longitude: Double?) async throws {
        try await memberService.bump(latitude: latitude, longitude: longitude)
    }
}
