import Foundation
import Alamofire

final class DeviceIdInterceptor: RequestInterceptor, @unchecked Sendable {
    
    func adapt(
        _ urlRequest: URLRequest,
        for session: Session,
        completion: @escaping (Result<URLRequest, Error>) -> Void
    ) {
        var request = urlRequest
        request.setValue(AuthStore.shared.uuid ?? "", forHTTPHeaderField: "X-Device-Id")
        completion(.success(request))
    }
}
