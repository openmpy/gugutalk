import Foundation
import Alamofire

extension DataRequest {

    func decodingWithErrorHandling<T: Decodable>(_ type: T.Type) async throws -> T {
        let response = await self
            .validate()
            .serializingDecodable(T.self)
            .response

        switch response.result {
        case .success(let value):
            return value

        case .failure(let error):
            print("[DEBUG] Decoding failed with error: \(error)")

            if let data = response.data,
               let errorResponse = try? JSONDecoder().decode(ErrorResponse.self, from: data) {
                print("[DEBUG] Server error response: \(errorResponse)")
                throw APIError.server(message: errorResponse.message)
            }

            if let statusCode = response.response?.statusCode {
                if statusCode == 401 {
                    throw APIError.token
                }
            }

            if response.error?.isSessionTaskError == true {
                print("[DEBUG] Network/session error detected")
                throw APIError.network
            }

            throw APIError.unknown
        }
    }

    func validateWithErrorHandling() async throws {
        let response = await self
            .validate()
            .serializingData(emptyResponseCodes: [200, 201, 204])
            .response

        switch response.result {
        case .success:
            return
        case .failure(let error):
            print("[DEBUG] Validation failed with error: \(error)")

            if let data = response.data,
               let errorResponse = try? JSONDecoder().decode(ErrorResponse.self, from: data) {
                print("[DEBUG] Server error response: \(errorResponse)")
                throw APIError.server(message: errorResponse.message)
            }

            if let statusCode = response.response?.statusCode {
                if statusCode == 401 {
                    throw APIError.token
                }
            }

            if let err = response.error, err.isSessionTaskError {
                print("[DEBUG] Network/session error detected")
                throw APIError.network
            }
            throw APIError.unknown
        }
    }
}
