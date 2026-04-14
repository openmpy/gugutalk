import Foundation
import Alamofire

enum APISession {

    static let authenticated: Session = {
        let authInterceptor = AuthInterceptor()
        let interceptor = Interceptor(
            adapters: [DeviceIdInterceptor(), authInterceptor],
            retriers: [authInterceptor]
        )
        return Session(interceptor: interceptor)
    }()

    static let plain: Session = {
        let interceptor = Interceptor(
            adapters: [DeviceIdInterceptor()],
            retriers: []
        )
        return Session(interceptor: interceptor)
    }()
}
