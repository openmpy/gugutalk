import SwiftUI
import Combine

@MainActor
final class PointViewModel: ObservableObject {

    private let pointService = PointService.shared

    @Published var state: PointViewState = .idle

    @Published var point: Int64?

    func get() async {
        state = .loading

        do {
            let response = try await pointService.get()

            point = response.point
            state = point == nil ? .empty : .data
        } catch {
            state = .error(error.localizedDescription)
        }
    }
}
