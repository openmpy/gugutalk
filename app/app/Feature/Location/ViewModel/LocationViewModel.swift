import SwiftUI
import Combine

@MainActor
final class LocationViewModel: ObservableObject {

    private let locationService = LocationService.shared
    private let memberService = MemberService.shared

    @Published var isLoading: Bool = false
    @Published var hasNext: Bool = true
    @Published var members: [MemberDiscoveryResponse] = []

    private var page: Int = 0
    private let size: Int = 20

    func getLocationMembers(gender: String) async -> Result<Void, Error> {
        page = 0
        hasNext = true

        guard !isLoading else { return .success(()) }

        isLoading = true
        defer { isLoading = false }
        
        do {
            let response = try await locationService.getLocationMembers(
                gender: gender,
                page: 0,
                size: size
            )
            members = response.payload
            hasNext = response.hasNext
            page = 1
            return .success(())
        } catch {
            return .failure(error)
        }
    }
    
    func loadMoreLocationMembers(gender: String) async -> Result<Void, Error> {
        guard !isLoading else { return .success(()) }
        guard hasNext else { return .success(()) }

        isLoading = true
        defer { isLoading = false }

        do {
            let response = try await locationService.getLocationMembers(
                gender: gender,
                page: page,
                size: size
            )
            members.append(contentsOf: response.payload)
            hasNext = response.hasNext
            page += 1
            return .success(())
        } catch {
            return .failure(error)
        }
    }

    func bump(latitude: Double?, longitude: Double?) async -> Result<Void, Error> {
        guard !isLoading else { return .failure(CancellationError()) }

        isLoading = true
        defer { isLoading = false }

        do {
            try await memberService.bump(latitude: latitude, longitude: longitude)
            return .success(())
        } catch {
            return .failure(error)
        }
    }
}
