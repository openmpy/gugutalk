import Foundation
import Combine
import SwiftStomp

class StompManager: NSObject, ObservableObject, SwiftStompDelegate {

    static let shared = StompManager()

    private var publishers: [String: PassthroughSubject<String, Never>] = [:]

    var stomp: SwiftStomp!

    func connect(accessToken: String) {
        let url = URL(string: "ws://192.168.0.15:8080/ws")!
        let headers: [String: String] = [
            "Authorization": "Bearer \(accessToken)"
        ]

        stomp = SwiftStomp(host: url, headers: headers)
        stomp.delegate = self
        stomp.connect()
    }

    func onConnect(swiftStomp : SwiftStomp, connectType : StompConnectType) {
        if connectType == .toStomp {
            subscribe(to: "/user/queue/chat-rooms")
        }
    }

    func onDisconnect(swiftStomp : SwiftStomp, disconnectType : StompDisconnectType) {
        if disconnectType == .fromStomp {
        }
    }

    func onMessageReceived(
        swiftStomp: SwiftStomp,
        message: Any?,
        messageId: String,
        destination: String,
        headers : [String : String]
    ) {
        guard let body = message as? String else { return }
        publishers[destination]?.send(body)

        print("🔥 message received:", message)
        print("🔥 destination:", destination)
    }

    func onReceipt(swiftStomp : SwiftStomp, receiptId : String) {

    }

    func onError(
        swiftStomp : SwiftStomp,
        briefDescription : String,
        fullDescription : String?,
        receiptId : String?,
        type : StompErrorType
    ) {
    }

    func send(
        body: String,
        to destination: String,
        receiptId: String? = nil,
        headers: [String: String] = [:]
    ) {
        stomp.send(
            body: body,
            to: destination,
            receiptId: receiptId,
            headers: headers
        )
    }

    func subscribe(to destination: String, headers: [String: String] = [:]) {
        if publishers[destination] == nil {
            publishers[destination] = PassthroughSubject<String, Never>()
        }

        stomp.subscribe(to: destination, headers: headers)
    }

    func unsubscribe(from destination: String, headers: [String: String] = [:]) {
        publishers.removeValue(forKey: destination)
        stomp.unsubscribe(from: destination, headers: headers)
    }

    func publisher(for destination: String) -> AnyPublisher<String, Never> {
        if publishers[destination] == nil {
            publishers[destination] = PassthroughSubject<String, Never>()
        }
        return publishers[destination]!.eraseToAnyPublisher()
    }
}
