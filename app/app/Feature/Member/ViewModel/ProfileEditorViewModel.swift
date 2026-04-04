import SwiftUI
import Combine

@MainActor
final class ProfileEditorViewModel: ObservableObject {

    private let memberService = MemberService.shared
    private let memberImageService = MemberImageService.shared
    private let authService = AuthService.shared
    private let s3Service = S3Service.shared

    @Published var isLoading: Bool = false
    @Published var state: MyProfileViewState = .idle

    @Published var member: MemberGetMeResponse? = nil
    @Published var selectPublicImages: [IdentifiableEditorImage] = []
    @Published var selectPrivateImages: [IdentifiableEditorImage] = []
    @Published var nickname: String = ""
    @Published var birthYear: Int = 2000
    @Published var bio: String = ""

    func getMe() async {
        guard state != .loading else { return }

        state = .loading
        await fetch()
    }

    private func fetch() async {
        do {
            let response = try await memberService.getMe()

            member = response
            selectPublicImages = response.publicImages.map(IdentifiableEditorImage.init)
            selectPrivateImages = response.privateImages.map(IdentifiableEditorImage.init)
            nickname = response.nickname
            birthYear = response.birthYear
            bio = response.bio ?? ""

            state = .data
        } catch {
            member = nil
            state = .error(error.localizedDescription)
        }
    }

    func updateProfile() async throws {
        guard !isLoading else { return }
        guard (19...60).contains(Calendar.current.component(.year, from: Date()) - birthYear) else {
            throw AppError("만 19세 이상 60세 이하만 가입할 수 있습니다.")
        }

        isLoading = true
        defer { isLoading = false }

        // 검증
        try await authService.validate(nickname: nickname, birthYear: birthYear)

        // 편집
        struct PendingUpload {
            let id: UUID
            let image: UIImage
            let type: String
        }

        let pendingUploads = selectPublicImages.compactMap { item in
            item.image.map { PendingUpload(id: item.id, image: $0, type: "PUBLIC") }
        } + selectPrivateImages.compactMap { item in
            item.image.map { PendingUpload(id: item.id, image: $0, type: "PRIVATE") }
        }

        var uploadedKeys: [UUID: String] = [:]

        if !pendingUploads.isEmpty {
            let urlRequest = pendingUploads.map { upload in
                MemberGetPresignedUrlRequest(imageType: upload.type, contentType: "image/jpeg")
            }
            let response = try await memberImageService.getPresignedUrls(images: urlRequest)

            for (upload, presigned) in zip(pendingUploads, response.presigned) {
                let resized = upload.image.resized(toMaxDimension: 480)
                guard let data = resized.compressedData(maxBytes: 300_000) else { continue }

                try await s3Service.uploadImageToS3(data: data, presigned: presigned)
                uploadedKeys[upload.id] = presigned.key
            }
        }

        let publicImageRequests = selectPublicImages.enumerated().compactMap { index, item in
            if let imageId = item.imageId {
                return ProfileImageUpdate(imageId: imageId, key: nil, sortOrder: index)
            }
            guard let key = uploadedKeys[item.id] else { return nil }
            return ProfileImageUpdate(imageId: nil, key: key, sortOrder: index)
        }

        let privateImageRequests = selectPrivateImages.enumerated().compactMap { index, item in
            if let imageId = item.imageId {
                return ProfileImageUpdate(imageId: imageId, key: nil, sortOrder: index)
            }
            guard let key = uploadedKeys[item.id] else { return nil }
            return ProfileImageUpdate(imageId: nil, key: key, sortOrder: index)
        }

        let request = MemberUpdateProfileRequest(
            publicImages: publicImageRequests,
            privateImages: privateImageRequests,
            nickname: nickname,
            birthYear: birthYear,
            bio: bio
        )
        try await memberService.updateProfile(request: request)
    }
}
