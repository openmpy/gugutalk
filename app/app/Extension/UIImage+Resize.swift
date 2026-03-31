import SwiftUI

extension UIImage {

    func resized(toMaxDimension maxDimension: CGFloat) -> UIImage {
        let width = size.width
        let height = size.height

        guard width > maxDimension || height > maxDimension else { return self }

        let scale = maxDimension / max(width, height)
        let newSize = CGSize(width: width * scale, height: height * scale)
        let renderer = UIGraphicsImageRenderer(size: newSize)

        return renderer.image { _ in
            self.draw(in: CGRect(origin: .zero, size: newSize))
        }
    }

    func compressedData(maxBytes: Int, compressionQuality: CGFloat = 0.8) -> Data? {
        var quality = compressionQuality

        while quality > 0.1 {
            if let data = jpegData(compressionQuality: quality), data.count <= maxBytes {
                return data
            }
            quality -= 0.1
        }
        return jpegData(compressionQuality: 0.1)
    }
}
