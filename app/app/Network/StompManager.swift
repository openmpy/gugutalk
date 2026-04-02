import Foundation
import Combine
import SwiftStomp

class StompManager: NSObject, ObservableObject, SwiftStompDelegate {

    static let shared = StompManager()

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

    func reconnect(accessToken: String) {
        stomp.disconnect()
        connect(accessToken: accessToken)
    }

    func onConnect(swiftStomp : SwiftStomp, connectType : StompConnectType) {
        if connectType == .toStomp {
            print("스톰프 연결")
        }
    }

    func onDisconnect(swiftStomp : SwiftStomp, disconnectType : StompDisconnectType) {
        if disconnectType == .fromStomp {
            print("스톰프 연결 해제")
        }
    }

    func onMessageReceived(
        swiftStomp: SwiftStomp,
        message: Any?,
        messageId: String,
        destination: String,
        headers : [String : String]
    ) {
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
        stomp.subscribe(to: destination, headers: headers)
    }

    func unsubscribe(from destination: String, headers: [String: String] = [:]) {
        stomp.unsubscribe(from: destination, headers: headers)
    }
}
