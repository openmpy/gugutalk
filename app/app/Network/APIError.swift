import Foundation

enum APIError: Error {

    case server(message: String)
    case decoding
    case network
    case token
    case ban
    case unknown
}

extension APIError: LocalizedError {
    var errorDescription: String? {
        switch self {
        case .server(let message):
            return message
        case .decoding:
            return "데이터 처리 오류"
        case .network:
            return "네트워크 오류"
        case .token:
            return "토큰 오류"
        case .ban:
            return "정지 상태"
        case .unknown:
            return "알 수 없는 오류"
        }
    }
}
