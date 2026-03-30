import SwiftUI
import PhotosUI

struct ProfileEditorView: View {

    @State private var images: [PhotosPickerItem] = []
    @State private var selectImages: [IdentifiableImage] = []

    @State private var privateImages: [PhotosPickerItem] = []
    @State private var selectPrivateImages: [IdentifiableImage] = []

    @State private var nickname: String = ""
    @State private var birthYear: String = ""
    @State private var bio: String = ""

    var body: some View {
        VStack {
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
                        Text("비밀 사진")
                            .font(.subheadline)
                            .fontWeight(.semibold)
                            .foregroundStyle(.primary)

                        ImagePicker(maxImages: 5, images: $privateImages, selectImages: $selectPrivateImages)
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
        }
        .safeAreaInset(edge: .bottom) {
            Button {

            } label: {
                Text("편집하기")
                    .font(.default.bold())
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical)
                    .glassEffect(.regular.tint(Color(.blue)).interactive())
            }
            .padding()
        }
        .navigationTitle("프로필 편집")
        .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    NavigationStack {
        ProfileEditorView()
    }
}
