import Foundation
import Alamofire

final class DeviceIdInterceptor: RequestInterceptor, @unchecked Sendable {
    
    func adapt(
        _ urlRequest: URLRequest,
        for session: Session,
        completion: @escaping (Result<URLRequest, Error>) -> Void
    ) {
        guard let uuid = AuthStore.shared.uuid else { return }

        var request = urlRequest
        request.setValue(uuid, forHTTPHeaderField: "X-Device-Id")
        completion(.success(request))
    }
}
