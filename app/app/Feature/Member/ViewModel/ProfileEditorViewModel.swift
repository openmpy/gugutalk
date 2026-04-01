import SwiftUI
import Combine

@MainActor
final class ProfileEditorViewModel: ObservableObject {

    private let memberService = MemberService.shared
    private let memberImageService = MemberImageService.shared
    private let s3Service = S3Service.shared

    @Published var isLoading: Bool = false
    @Published var member: MemberGetMeResponse? = nil
    @Published var nickname: String = ""
    @Published var birthYear: Int = 2000
    @Published var bio: String = ""

    func getMe() async -> Result<Void, Error> {
        guard !isLoading else { return .failure(CancellationError()) }

        isLoading = true
        defer { isLoading = false }

        do {
            member = try await memberService.getMe()

            if let member = member {
                nickname = member.nickname
                birthYear = member.birthYear
                bio = member.bio ?? ""
            }
            return .success(())
        } catch {
            return .failure(error)
        }
    }

    func updateProfile(
        publicImages: [IdentifiableEditorImage],
        privateImages: [IdentifiableEditorImage],
    ) async -> Result<Void, Error> {
        guard !isLoading else { return .failure(CancellationError()) }

        isLoading = true
        defer { isLoading = false }

        do {
            struct PendingUpload {
                let id: UUID
                let image: UIImage
                let type: String
            }

            let pendingUploads = publicImages.compactMap { item in
                item.image.map { PendingUpload(id: item.id, image: $0, type: "PUBLIC") }
            } + privateImages.compactMap { item in
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

            let publicImageRequests = publicImages.enumerated().compactMap { index, item in
                if let imageId = item.imageId {
                    return ProfileImageUpdate(imageId: imageId, key: nil, sortOrder: index)
                }
                guard let key = uploadedKeys[item.id] else { return nil }
                return ProfileImageUpdate(imageId: nil, key: key, sortOrder: index)
            }

            let privateImageRequests = privateImages.enumerated().compactMap { index, item in
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
            return .success(())
        } catch {
            return .failure(error)
        }
    }
}
