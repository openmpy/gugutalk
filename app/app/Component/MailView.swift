import SwiftUI
import MessageUI

struct MailView: UIViewControllerRepresentable {

    let subject: String
    let body: String
    let recipient: String

    @Binding var isShowing: Bool

    class Coordinator: NSObject, MFMailComposeViewControllerDelegate {
        var parent: MailView
        init(_ parent: MailView) { self.parent = parent }

        func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
            parent.isShowing = false
        }
    }

    func makeCoordinator() -> Coordinator { Coordinator(self) }

    func makeUIViewController(context: Context) -> MFMailComposeViewController {
        let mc = MFMailComposeViewController()
        mc.mailComposeDelegate = context.coordinator
        mc.setToRecipients([recipient])
        mc.setSubject(subject)
        mc.setMessageBody(body, isHTML: false)
        return mc
    }

    func updateUIViewController(_ uiViewController: MFMailComposeViewController, context: Context) {}
}
