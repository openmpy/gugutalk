import SwiftUI
import Combine

@MainActor
final class ActivateViewModel: ObservableObject {

    private let authService = AuthService.shared
    private let memberService = MemberService.shared
    private let memberImageService = MemberImageService.shared
    private let s3Service = S3Service.shared

    @Published var isLoading: Bool = false

    func activate(images: [IdentifiableImage], nickname: String, birthYear: Int, bio: String) async -> Result<Void, Error> {
        guard !isLoading else { return .failure(CancellationError()) }

        isLoading = true
        defer { isLoading = false }

        do {
            let urlRequest = images.map { _ in
                MemberGetPresignedUrlRequest(imageType: "PUBLIC", contentType: "image/jpeg")
            }
            let response = try await memberImageService.getPresignedUrls(images: urlRequest)

            for (it, presigned) in zip(images, response.presigned) {
                let resized = it.image.resized(toMaxDimension: 480)
                guard let data = resized.compressedData(maxBytes: 300_000) else { continue }
                try await s3Service.uploadImageToS3(data: data, presigned: presigned)
            }

            let imageRequest = response.presigned.enumerated().map { index, presigned in
                ActivateImageRequest(index: index, key: presigned.key)
            }
            
            try await authService.activate(images: imageRequest, nickname: nickname, birthYear: birthYear, bio: bio)
            return .success(())
        } catch {
            return .failure(error)
        }
    }
}
