enum SettingViewState: Equatable {

    case idle
    case loading
    case success(ActionType)
    case error(String)

    enum ActionType {
        
        case logout
        case withdraw
    }
}
