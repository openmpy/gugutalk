import SwiftUI
import UIKit

struct SecureView<Content: View>: UIViewControllerRepresentable {

    let content: Content

    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    func makeUIViewController(context: Context) -> SecureViewController<Content> {
        SecureViewController(content: content)
    }

    func updateUIViewController(_ vc: SecureViewController<Content>, context: Context) {}
}

class SecureViewController<Content: View>: UIViewController {

    private let content: Content
    private var hostingController: UIHostingController<Content>?

    init(content: Content) {
        self.content = content
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) { fatalError() }

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .clear

        let secureField = UITextField()
        secureField.isSecureTextEntry = true
        secureField.backgroundColor = .clear

        if let secureSubview = secureField.subviews.first {
            secureSubview.frame = view.bounds
            secureSubview.autoresizingMask = [.flexibleWidth, .flexibleHeight]
            secureSubview.backgroundColor = .clear
            view.addSubview(secureSubview)

            let hosting = UIHostingController(rootView: content)
            hosting.view.backgroundColor = .clear
            hosting.view.translatesAutoresizingMaskIntoConstraints = false
            addChild(hosting)
            secureSubview.addSubview(hosting.view)
            hosting.didMove(toParent: self)

            NSLayoutConstraint.activate([
                hosting.view.topAnchor.constraint(equalTo: secureSubview.topAnchor),
                hosting.view.bottomAnchor.constraint(equalTo: secureSubview.bottomAnchor),
                hosting.view.leadingAnchor.constraint(equalTo: secureSubview.leadingAnchor),
                hosting.view.trailingAnchor.constraint(equalTo: secureSubview.trailingAnchor)
            ])

            hostingController = hosting
        }
    }
}
