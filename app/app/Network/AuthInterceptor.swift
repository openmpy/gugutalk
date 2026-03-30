import Foundation
import Alamofire

final class AuthInterceptor: RequestInterceptor, @unchecked Sendable {

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
}
