import SwiftUI

extension String {

    var toDate: Date? {
        String.inputFormatter.date(from: self)
    }

    var relativeTime: String {
        guard let date = toDate else { return "" }

        let seconds = Date().timeIntervalSince(date)
        if abs(seconds) < 60 {
            return "방금 전"
        }
        return String.relativeFormatter.localizedString(for: date, relativeTo: Date())
    }

    private static let inputFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "ko_KR")
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
        return formatter
    }()

    private static let relativeFormatter: RelativeDateTimeFormatter = {
        let formatter = RelativeDateTimeFormatter()
        formatter.locale = Locale(identifier: "ko_KR")
        formatter.unitsStyle = .full
        return formatter
    }()
}
