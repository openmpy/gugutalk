import SwiftUI
import Combine
import GoogleMobileAds

class RewardedAdManager: NSObject, ObservableObject, FullScreenContentDelegate {

    @Published var adLoaded = false

    private var rewardedAd: RewardedAd?

    // 테스트 ID
    private let adUnitID = "ca-app-pub-3940256099942544/1712485313"

    func loadAd() {
        let request = Request()
        RewardedAd.load(with: adUnitID, request: request) { [weak self] ad, error in
            if error != nil {
                return
            }

            self?.rewardedAd = ad
            self?.rewardedAd?.fullScreenContentDelegate = self
            self?.adLoaded = true
        }
    }

    func showAd(from viewController: UIViewController, completion: @escaping (Bool) -> Void) {
        guard let ad = rewardedAd else {
            completion(false)
            return
        }

        ad.present(from: viewController) {
            completion(true)
        }
    }

    // MARK: - GADFullScreenContentDelegate

    func adDidDismissFullScreenContent(_ ad: FullScreenPresentingAd) {
        adLoaded = false
        loadAd()
    }
}
