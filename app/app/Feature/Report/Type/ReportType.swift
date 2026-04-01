enum ReportType: String, CaseIterable, Identifiable {

    case abuse
    case spam
    case minor
    case sexual
    case fake
    case etc

    var id: Self { self }

    var title: String {
        switch self {
        case .abuse: return "욕설 / 비방"
        case .spam: return "스팸 / 광고"
        case .minor: return "미성년자"
        case .sexual: return "음란물"
        case .fake: return "도용"
        case .etc: return "기타"
        }
    }
}
