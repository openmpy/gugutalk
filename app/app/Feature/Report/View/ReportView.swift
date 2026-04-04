import SwiftUI
import PhotosUI

struct ReportView: View {

    let memberId: Int64
    let nickname: String

    @StateObject private var vm = ReportViewModel()

    @Environment(\.dismiss) var dismiss

    @State private var images: [PhotosPickerItem] = []

    var body: some View {
        VStack {
            Spacer()

            reportBanner

            reportSection
        }
        .safeAreaInset(edge: .bottom) {
            Button {
                Task {
                    do {
                        try await vm.report(reportedId: memberId)

                        ToastManager.shared.show("신고가 접수되었습니다.")
                        dismiss()
                    } catch {
                        ToastManager.shared.show(error.localizedDescription, type: .error)
                    }
                }
            } label: {
                Text("접수하기")
                    .font(.default.bold())
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical)
                    .glassEffect(.regular.tint(Color(.red)).interactive())
            }
            .disabled(vm.isLoading)
            .padding()
        }
        .overlay {
            if vm.isLoading {
                LoadingOverlay()
            }
        }
        .navigationTitle("신고 (\(nickname))")
        .navigationBarTitleDisplayMode(.inline)
    }

    // MARK: - SECTION

    private var reportBanner: some View {
        Text("신고 남용 시 서비스 이용이 제한됩니다.")
            .font(.subheadline.bold())
            .padding(10)
            .frame(maxWidth: .infinity)
            .foregroundColor(.white)
            .background(.red)
    }

    private var reportSection: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                VStack(alignment: .leading) {
                    ForEach(ReportType.allCases) { it in
                        Button {
                            vm.selectType = it
                        } label: {
                            HStack {
                                Text(it.title)
                                    .font(.subheadline)
                                    .foregroundColor(.primary)

                                Spacer()

                                Image(systemName: vm.selectType == it ? "checkmark.circle.fill" : "circle")
                                    .foregroundColor(.red)
                            }
                            .padding()
                            .background(Color(.systemGray6))
                            .cornerRadius(20)
                            .padding(.top, 5)
                        }
                    }
                }

                VStack(alignment: .leading, spacing: 12) {
                    Text("증거 자료 (선택)")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundStyle(.primary)

                    ImagePicker(maxImages: 5, images: $images, selectImages: $vm.selectImages)
                }

                VStack(alignment: .leading, spacing: 12) {
                    Text("추가 설명 (선택)")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundStyle(.primary)
                    
                    TextEditor(text: $vm.reason)
                        .padding(.horizontal, 11)
                        .padding(.vertical, 7)
                        .frame(height: 150)
                        .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
                        .scrollContentBackground(.hidden)
                        .textInputAutocapitalization(.never)
                        .disableAutocorrection(true)
                }
            }
            .padding()
        }
        .onTapGesture {
            hideKeyboard()
        }
    }
}
