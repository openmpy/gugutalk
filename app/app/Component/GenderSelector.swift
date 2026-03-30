import SwiftUI

struct GenderSelector: View {

    @Binding var selectGender: String

    var body: some View {
        Picker("성별", selection: $selectGender) {
            Text("전체").tag("ALL")
            Text("여자").tag("FEMALE")
            Text("남자").tag("MALE")
        }
        .pickerStyle(.segmented)
        .padding(.horizontal)
    }
}
