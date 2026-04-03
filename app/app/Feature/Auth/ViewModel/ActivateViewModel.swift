import SwiftUI
import PhotosUI
import Combine

@MainActor
final class ActivateViewModel: ObservableObject {

    private let authService = AuthService.shared
    private let memberImageService = MemberImageService.shared
    private let s3Service = S3Service.shared

    @Published var isLoading: Bool = false

    @Published var images: [PhotosPickerItem] = []
    @Published var selectImages: [IdentifiableImage] = []
    @Published var nickname: String = ""
    @Published var birthYear: Int? = nil
    @Published var bio: String = ""

    var isSubmittable: Bool {
        !nickname.isEmpty && birthYear != nil
    }

    func activate() async throws {
        guard !isLoading else { return }

        isLoading = true
        defer { isLoading = false }

        // 검증
        guard let birthYear = birthYear, (19...60).contains(Calendar.current.component(.year, from: Date()) - birthYear) else {
            throw AppError("만 19세 이상 60세 이하만 가입할 수 있습니다.")
        }

        try await authService.validate(nickname: nickname, birthYear: birthYear)

        // 이미지 업로드
        let urlRequest = selectImages.map { _ in
            MemberGetPresignedUrlRequest(imageType: "PUBLIC", contentType: "image/jpeg")
        }
        let response = try await memberImageService.getPresignedUrls(images: urlRequest)

        for (it, presigned) in zip(selectImages, response.presigned) {
            let resized = it.image.resized(toMaxDimension: 480)
            guard let data = resized.compressedData(maxBytes: 300_000) else { continue }
            try await s3Service.uploadImageToS3(data: data, presigned: presigned)
        }
        
        let imageRequest = response.presigned.enumerated().map { index, presigned in
            ActivateImageRequest(index: index, key: presigned.key)
        }

        // 활성화
        try await authService.activate(
            images: imageRequest,
            nickname: nickname,
            birthYear: birthYear,
            bio: bio
        )
    }
}
