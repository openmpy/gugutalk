import SwiftUI
import Alamofire

final class AuthInterceptor: RequestInterceptor, @unchecked Sendable {

    @AppStorage("isLoggedIn") private var isLoggedIn: Bool = false

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
        // 401이 아니면 재시도 안 함
        guard let response = request.task?.response as? HTTPURLResponse, response.statusCode == 401 else {
            completion(.doNotRetry)
            return
        }

        // refreshToken 없으면 재시도 안 함
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
                self.resolvePendingRetries(with: .retry)
            } catch {
                isLoggedIn = false
                AuthStore.shared.clearAll()
                self.resolvePendingRetries(with: .doNotRetryWithError(APIError.token))
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
