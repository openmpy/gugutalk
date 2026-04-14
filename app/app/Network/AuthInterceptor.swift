import SwiftUI
import Alamofire

final class AuthInterceptor: RequestInterceptor, @unchecked Sendable {

    private var isRefreshing = false
    private var pendingRetries: [(RetryResult) -> Void] = []
    private let lock = NSLock()

    func adapt(
        _ urlRequest: URLRequest,
        for session: Session,
        completion: @escaping (Result<URLRequest, Error>) -> Void
    ) {
        var request = urlRequest

        guard let accessToken = AuthStore.shared.accessToken else {
            completion(.failure(APIError.token))
            return
        }

        request.setValue("Bearer \(accessToken)", forHTTPHeaderField: "Authorization")
        completion(.success(request))
    }

    func retry(
        _ request: Request,
        for session: Session,
        dueTo error: Error,
        completion: @escaping (RetryResult) -> Void
    ) {
        guard let response = request.task?.response as? HTTPURLResponse, response.statusCode == 401 else {
            completion(.doNotRetry)
            return
        }
        guard let refreshToken = AuthStore.shared.refreshToken else {
            completion(.doNotRetryWithError(APIError.token))
            return
        }

        lock.lock()
        pendingRetries.append(completion)

        guard !isRefreshing else {
            lock.unlock()
            return
        }

        isRefreshing = true
        lock.unlock()

        Task {
            do {
                let url = "http://192.168.0.15:8080/api/v1/auth/rotation"
                let body = RotateTokenRequest(
                    memberId: AuthStore.shared.memberId ?? 0,
                    refreshToken: refreshToken
                )

                let response = try await AF.request(
                    url,
                    method: .post,
                    parameters: body,
                    encoder: JSONParameterEncoder.default
                ).decodingWithErrorHandling(RotateTokenResponse.self)

                // 새 토큰 저장
                AuthStore.shared.accessToken = response.accessToken
                AuthStore.shared.refreshToken = response.refreshToken
                StompManager.shared.reconnect(accessToken: response.accessToken)
                self.resolvePendingRetries(with: .retry)
            } catch {
                AuthStore.shared.clearAll()
                self.resolvePendingRetries(with: .doNotRetryWithError(APIError.token))

                DispatchQueue.main.async {
                    NotificationCenter.default.post(name: .didSessionExpire, object: nil)
                }
            }
        }
    }

    private func resolvePendingRetries(with result: RetryResult) {
        lock.lock()

        let retries = pendingRetries

        pendingRetries.removeAll()
        isRefreshing = false
        lock.unlock()

        retries.forEach { $0(result) }
    }
}
