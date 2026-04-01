import Alamofire

final class SocialService {
    
    static let shared = SocialService()
    
    let session = Session(interceptor: AuthInterceptor())
    let baseURL = "http://192.168.0.15:8080/api"
    
    func like(memberId: Int64) async throws {
        let url = "\(baseURL)/v1/social/likes/\(memberId)"
        
        try await session.request(
            url,
            method: .post
        ).validateWithErrorHandling()
    }
    
    func unlike(memberId: Int64) async throws {
        let url = "\(baseURL)/v1/social/likes/\(memberId)"
        
        try await session.request(
            url,
            method: .delete
        ).validateWithErrorHandling()
    }
    
    func block(memberId: Int64) async throws {
        let url = "\(baseURL)/v1/social/blocks/\(memberId)"
        
        try await session.request(
            url,
            method: .post
        ).validateWithErrorHandling()
    }
    
    func unblock(memberId: Int64) async throws {
        let url = "\(baseURL)/v1/social/blocks/\(memberId)"
        
        try await session.request(
            url,
            method: .delete
        ).validateWithErrorHandling()
    }
    
    func getLikedMember(
        cursorId: Int64?,
        cursorDateAt: String?,
        size: Int = 20
    ) async throws -> CursorResponse<SettingResponse> {
        let url = "\(baseURL)/v1/social/likes"
        
        var params: Parameters = [
            "size": size
        ]
        if cursorId != nil && cursorDateAt != nil {
            params["cursorId"] = cursorId
            params["cursorDate"] = cursorDateAt
        }
        
        return try await session.request(
            url,
            method: .get,
            parameters: params.compactMapValues { $0 }
        )
        .decodingWithErrorHandling(CursorResponse<SettingResponse>.self)
    }
    
    func getBlockedMember(
        cursorId: Int64?,
        cursorDateAt: String?,
        size: Int = 20
    ) async throws -> CursorResponse<SettingResponse> {
        let url = "\(baseURL)/v1/social/blocks"
        
        var params: Parameters = [
            "size": size
        ]
        if cursorId != nil && cursorDateAt != nil {
            params["cursorId"] = cursorId
            params["cursorDate"] = cursorDateAt
        }
        
        return try await session.request(
            url,
            method: .get,
            parameters: params.compactMapValues { $0 }
        )
        .decodingWithErrorHandling(CursorResponse<SettingResponse>.self)
    }
}
