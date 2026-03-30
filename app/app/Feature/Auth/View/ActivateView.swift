import SwiftUI
import PhotosUI

struct ActivateView: View {

    @State private var images: [PhotosPickerItem] = []
    @State private var selectImages: [IdentifiableImage] = []

    @State private var nickname: String = ""
    @State private var birthYear: String = ""
    @State private var bio: String = ""

    private var isSubmit: Bool {
        !nickname.isEmpty && !birthYear.isEmpty
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                VStack(alignment: .leading, spacing: 12) {
                    Text("프로필 사진")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundStyle(.primary)

                    ImagePicker(maxImages: 5, images: $images, selectImages: $selectImages)
                }

                VStack(alignment: .leading, spacing: 12) {
                    Text("닉네임")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundStyle(.primary)

                    TextField("홍길동", text: $nickname)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
                        .scrollContentBackground(.hidden)
                        .textInputAutocapitalization(.never)
                        .disableAutocorrection(true)
                }

                VStack(alignment: .leading, spacing: 12) {
                    Text("출생연도")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundStyle(.primary)

                    TextField("2000", text: $birthYear)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
                        .scrollContentBackground(.hidden)
                        .textInputAutocapitalization(.never)
                        .disableAutocorrection(true)
                        .keyboardType(.numberPad)
                }

                VStack(alignment: .leading, spacing: 12) {
                    Text("자기소개")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundStyle(.primary)

                    TextEditor(text: $bio)
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
        .safeAreaInset(edge: .bottom) {
            Button {

            } label: {
                Text("들어가기")
                    .font(.default.bold())
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical)
                    .glassEffect(.regular.tint(isSubmit ? Color(.blue) : Color(.systemGray3)).interactive())
            }
            .disabled(!isSubmit)
            .padding()
        }
        .navigationTitle("프로필 설정")
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .interactiveDismissDisabled(true)
    }
}

#Preview {
    ActivateView()
}
