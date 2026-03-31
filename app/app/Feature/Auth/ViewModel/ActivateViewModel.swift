import SwiftUI
import Combine

@MainActor
final class ActivateViewModel: ObservableObject {

    private let authService = AuthService.shared
    private let memberService = MemberService.shared
    private let memberImageService = MemberImageService.shared
    private let s3Service = S3Service.shared

    @Published var showErrorAlert: Bool = false
    @Published var errorMessage: String = ""
    @Published var isLoading: Bool = false

    func activate(images: [IdentifiableImage], nickname: String, birthYear: Int, bio: String) async -> Bool {
        guard !isLoading else { return false }

        isLoading = true
        defer { isLoading = false }

        do {
            // 1. Presigned URL 발급
            let urlRequest = images.map { _ in
                MemberGetPresignedUrlRequest(imageType: "PUBLIC", contentType: "image/jpeg")
            }
            let response = try await memberImageService.getPresignedUrls(images: urlRequest)

            // 2. S3 업로드
            for (it, presigned) in zip(images, response.presigned) {
                let resized = it.image.resized(toMaxDimension: 480)
                guard let data = resized.compressedData(maxBytes: 300_000) else { continue }

                try await s3Service.uploadImageToS3(data: data, presigned: presigned)
            }

            // 3. 업로드된 key 목록
            let imageRequest = response.presigned.enumerated().map { index, presigned in
                ActivateImageRequest(index: index, key: presigned.key)
            }

            // 4. 프로필 저장 API 호출
            try await authService.activate(images: imageRequest, nickname: nickname, birthYear: birthYear, bio: bio)
            return true
        } catch {
            showErrorAlert = true
            errorMessage = error.localizedDescription
            return false
        }
    }
}
