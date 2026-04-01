import SwiftUI
import PhotosUI
import Toasts

struct ReportView: View {

    let memberId: Int64
    let nickname: String

    @StateObject private var vm = ReportViewModel()

    @Environment(\.presentToast) var presentToast
    @Environment(\.dismiss) var dismiss

    @State private var selectType: ReportType = .abuse
    @State private var images: [PhotosPickerItem] = []
    @State private var selectImages: [IdentifiableImage] = []
    @State private var reason: String = ""

    var body: some View {
        VStack {
            Spacer()

            Text("신고 남용 시 서비스 이용이 제한됩니다.")
                .font(.subheadline.bold())
                .padding(10)
                .frame(maxWidth: .infinity)
                .foregroundColor(.white)
                .background(.red)

            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    VStack(alignment: .leading) {
                        ForEach(ReportType.allCases) { it in
                            Button {
                                selectType = it
                            } label: {
                                HStack {
                                    Text(it.title)
                                        .font(.subheadline)
                                        .foregroundColor(.primary)

                                    Spacer()

                                    Image(systemName: selectType == it ? "checkmark.circle.fill" : "circle")
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

                        ImagePicker(maxImages: 5, images: $images, selectImages: $selectImages)
                    }

                    VStack(alignment: .leading, spacing: 12) {
                        Text("추가 설명 (선택)")
                            .font(.subheadline)
                            .fontWeight(.semibold)
                            .foregroundStyle(.primary)

                        TextEditor(text: $reason)
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
        .safeAreaInset(edge: .bottom) {
            Button {
                Task {
                    let result = await vm.create(
                        reportedId: memberId,
                        images: selectImages,
                        type: selectType.rawValue.uppercased(),
                        reason: reason
                    )
                    switch result {
                    case .success:
                        presentToast(ToastValue(
                            icon: Image(systemName: "checkmark.circle.fill").foregroundColor(.green),
                            message: "신고가 접수되었습니다."
                        ))
                        dismiss()
                    case .failure(let error):
                        presentToast(ToastValue(
                            icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                            message: error.localizedDescription
                        ))
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
        .navigationTitle("신고 (\(nickname))")
        .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    ReportView(memberId: 1, nickname: "홍길동")
}
