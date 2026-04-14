import Foundation
import Combine

enum ToastType {

    case info
    case error
}

struct ToastData {

    let message: String
    let type: ToastType
}

@MainActor
final class ToastManager: ObservableObject {

    static let shared = ToastManager()

    @Published var isShow: Bool = false
    @Published var toast: ToastData?

    func show(_ message: String, type: ToastType = .info) {
        toast = ToastData(message: message, type: type)
        isShow = true
    }

    func show(_ error: Error) {
        if let apiError = error as? APIError, !apiError.shouldShowToast {
            return
        }

        show(error.localizedDescription, type: .error)
    }
}
