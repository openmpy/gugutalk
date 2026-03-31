import SwiftUI
import Alamofire

final class S3Service {

    static let shared = S3Service()

    let session = Session(interceptor: AuthInterceptor())
    let baseURL = "http://192.168.0.15:8080/api"

    func uploadImageToS3(
        data: Data,
        presigned: PresignedUrlResponse
    ) async throws {
        let headers: HTTPHeaders = ["Content-Type": "image/jpeg"]

        return try await AF.upload(
            data,
            to: presigned.url,
            method: .put,
            headers: headers
        )
        .validateWithErrorHandling()
    }
}
