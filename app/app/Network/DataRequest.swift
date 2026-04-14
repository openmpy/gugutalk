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

            if let statusCode = response.response?.statusCode {
                if statusCode == 401 {
                    throw APIError.token
                } else if statusCode == 423 {
                    let message = response.data
                        .flatMap { try? JSONDecoder().decode(ErrorResponse.self, from: $0) }
                        .map { $0.message } ?? "정지된 기기입니다."

                    DispatchQueue.main.async {
                        NotificationCenter.default.post(
                            name: .didDeviceBanned,
                            object: nil,
                            userInfo: ["message": message]
                        )
                    }
                    throw APIError.ban
                }
            }
            if let data = response.data,
               let errorResponse = try? JSONDecoder().decode(ErrorResponse.self, from: data) {
                print("[DEBUG] Server error response: \(errorResponse)")
                throw APIError.server(message: errorResponse.message)
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

            if let statusCode = response.response?.statusCode {
                if statusCode == 401 {
                    throw APIError.token
                } else if statusCode == 423 {
                    let message = response.data
                        .flatMap { try? JSONDecoder().decode(ErrorResponse.self, from: $0) }
                        .map { $0.message } ?? "정지된 기기입니다."

                    DispatchQueue.main.async {
                        NotificationCenter.default.post(
                            name: .didDeviceBanned,
                            object: nil,
                            userInfo: ["message": message]
                        )
                    }
                    throw APIError.ban
                }
            }
            if let data = response.data,
               let errorResponse = try? JSONDecoder().decode(ErrorResponse.self, from: data) {
                print("[DEBUG] Server error response: \(errorResponse)")
                throw APIError.server(message: errorResponse.message)
            }
            if let err = response.error, err.isSessionTaskError {
                print("[DEBUG] Network/session error detected")
                throw APIError.network
            }
            throw APIError.unknown
        }
    }
}
