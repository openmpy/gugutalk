import SwiftUI

struct ChatStatusSelector: View {

    @Binding var selectStatus: String

    var body: some View {
        Picker("상태", selection: $selectStatus) {
            Text("전체").tag("ALL")
            Text("안읽음").tag("UNREAD")
        }
        .pickerStyle(.segmented)
        .padding(.horizontal)
    }
}
