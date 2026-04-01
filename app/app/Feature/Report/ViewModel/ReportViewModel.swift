import SwiftUI
import Combine

@MainActor
final class ReportViewModel: ObservableObject {

    private let reportService = ReportService.shared
    private let reportImageService = ReportImageService.shared
    private let s3Service = S3Service.shared

    @Published var isLoading: Bool = false

    func create(
        reportedId: Int64,
        images: [IdentifiableImage],
        type: String,
        reason: String?
    ) async -> Result<Void, Error> {
        guard !isLoading else { return .failure(CancellationError()) }

        isLoading = true
        defer { isLoading = false }

        do {
            let urlRequest = images.map { _ in
                ReportGetPresignedUrlRequest(contentType: "image/jpeg")
            }
            let response = try await reportImageService.getPresignedUrls(images: urlRequest)

            for (it, presigned) in zip(images, response.presigned) {
                let resized = it.image.resized(toMaxDimension: 480)
                guard let data = resized.compressedData(maxBytes: 300_000) else { continue }
                try await s3Service.uploadImageToS3(data: data, presigned: presigned)
            }

            let imageRequest = response.presigned.enumerated().map { index, presigned in
                ReportImageRequest(index: index, key: presigned.key)
            }
            
            try await reportService.create(
                reportedId: reportedId,
                images: imageRequest,
                type: type,
                reason: reason
            )
            return .success(())
        } catch {
            return .failure(error)
        }
    }
}
