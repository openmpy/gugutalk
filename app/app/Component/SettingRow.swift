import SwiftUI

struct SettingRow: View {

    @Environment(\.colorScheme) var colorScheme

    let title: String
    let icon: String
    let color: Color

    var body: some View {
        HStack(spacing: 13) {
            Image(systemName: icon)
                .font(.system(size: 25))
                .foregroundColor(color)
                .frame(width: 35, height: 35)

            Text(title)
                .foregroundColor(.primary)

            Spacer()
        }
        .padding(.vertical, 12)
        .padding(.horizontal)
        .background(
            colorScheme == .light
            ? Color(.systemBackground)
            : Color(.systemGray6)
        )
    }
}
