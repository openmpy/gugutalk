import Foundation
import Security

final class AuthStore {
    
    static let shared = AuthStore()
    
    private let memberIdKey = "com.openmpy.app.member.id"
    private let uuidKey = "com.openmpy.app.member.uuid"
    private let accessTokenKey = "com.openmpy.app.token.access"
    private let refreshTokenKey = "com.openmpy.app.token.refresh"
    
    private init() {}
    
    var memberId: Int64? {
        get {
            let value = UserDefaults.standard.object(forKey: memberIdKey)
            return value != nil ? Int64(UserDefaults.standard.integer(forKey: memberIdKey)) : nil
        }
        set {
            if let memberId = newValue {
                UserDefaults.standard.set(memberId, forKey: memberIdKey)
            } else {
                UserDefaults.standard.removeObject(forKey: memberIdKey)
            }
        }
    }
    
    var uuid: String? {
        get { readFromKeychain(key: uuidKey) }
        set {
            if let uuid = newValue {
                saveToKeychain(key: uuidKey, value: uuid)
            } else {
                deleteFromKeychain(key: uuidKey)
            }
        }
    }
    
    var accessToken: String? {
        get { UserDefaults.standard.string(forKey: accessTokenKey) }
        set {
            if let token = newValue {
                UserDefaults.standard.set(token, forKey: accessTokenKey)
            } else {
                UserDefaults.standard.removeObject(forKey: accessTokenKey)
            }
        }
    }
    
    var refreshToken: String? {
        get { readFromKeychain(key: refreshTokenKey) }
        set {
            if let token = newValue {
                saveToKeychain(key: refreshTokenKey, value: token)
            } else {
                deleteFromKeychain(key: refreshTokenKey)
            }
        }
    }
    
    func save(memberId: Int64, uuid: String, accessToken: String, refreshToken: String) {
        self.memberId = memberId
        self.uuid = uuid
        self.accessToken = accessToken
        self.refreshToken = refreshToken
    }
    
    func clearAll() {
        memberId = nil
        accessToken = nil
        refreshToken = nil
    }
}

private extension AuthStore {
    
    @discardableResult
    func saveToKeychain(key: String, value: String) -> Bool {
        guard let data = value.data(using: .utf8) else { return false }
        
        if readFromKeychain(key: key) != nil {
            let query: [CFString: Any] = [
                kSecClass: kSecClassGenericPassword,
                kSecAttrAccount: key
            ]
            let attributes: [CFString: Any] = [kSecValueData: data]
            return SecItemUpdate(query as CFDictionary, attributes as CFDictionary) == errSecSuccess
        }
        
        let query: [CFString: Any] = [
            kSecClass: kSecClassGenericPassword,
            kSecAttrAccount: key,
            kSecValueData: data,
            kSecAttrAccessible: kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
        ]
        return SecItemAdd(query as CFDictionary, nil) == errSecSuccess
    }
    
    func readFromKeychain(key: String) -> String? {
        let query: [CFString: Any] = [
            kSecClass: kSecClassGenericPassword,
            kSecAttrAccount: key,
            kSecReturnData: true,
            kSecMatchLimit: kSecMatchLimitOne
        ]
        var result: AnyObject?
        guard SecItemCopyMatching(query as CFDictionary, &result) == errSecSuccess,
              let data = result as? Data,
              let value = String(data: data, encoding: .utf8)
        else { return nil }
        return value
    }
    
    @discardableResult
    func deleteFromKeychain(key: String) -> Bool {
        let query: [CFString: Any] = [
            kSecClass: kSecClassGenericPassword,
            kSecAttrAccount: key
        ]
        return SecItemDelete(query as CFDictionary) == errSecSuccess
    }
}
