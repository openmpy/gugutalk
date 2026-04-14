import SwiftUI
import PhotosUI

struct ProfileEditorView: View {

    @StateObject private var vm = ProfileEditorViewModel()

    @Environment(\.dismiss) var dismiss

    @State private var publicImages: [PhotosPickerItem] = []
    @State private var privateImages: [PhotosPickerItem] = []

    var body: some View {
        VStack {
            switch vm.state {

            case .idle:
                Spacer()
                EmptyView()
                Spacer()

            case .loading:
                Spacer()
                ProgressView()
                Spacer()

            case .data:
                if let member = vm.member {
                    memberEditorSection(member: member)
                }

            case .error(let message):
                errorSection(message: message)
            }
        }
        .safeAreaInset(edge: .bottom) {
            editButton
        }
        .overlay {
            if vm.isLoading {
                LoadingOverlay()
            }
        }
        .task {
            await vm.getMe()
        }
        .navigationTitle("프로필 편집")
        .navigationBarTitleDisplayMode(.inline)
    }

    // MARK: - SECTION

    private func memberEditorSection(member: MemberGetMeResponse) -> some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                VStack(alignment: .leading, spacing: 12) {
                    Text("프로필 사진")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundStyle(.primary)

                    ImageEditorPicker(maxImages: 5, images: $publicImages, selectImages: $vm.selectPublicImages)
                }

                VStack(alignment: .leading, spacing: 12) {
                    Text("닉네임")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundStyle(.primary)

                    TextField("홍길동", text: $vm.nickname)
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

                    TextField("2000", value: $vm.birthYear, format: .number.grouping(.never))
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

                    ImageEditorPicker(maxImages: 5, images: $privateImages, selectImages: $vm.selectPrivateImages)
                }

                VStack(alignment: .leading, spacing: 12) {
                    Text("자기소개")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundStyle(.primary)

                    TextEditor(text: $vm.bio)
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

    private var editButton: some View {
        Button {
            Task {
                do {
                    try await vm.updateProfile()

                    ToastManager.shared.show("프로필이 편집되었습니다.")
                    dismiss()
                } catch {
                    ToastManager.shared.show(error)
                }
            }
        } label: {
            Text("편집하기")
                .font(.default.bold())
                .foregroundStyle(.white)
                .frame(maxWidth: .infinity)
                .padding(.vertical)
                .glassEffect(vm.state == .data ?
                    .regular.tint(Color(.blue)).interactive() : .regular.tint(Color(.systemGray3)).interactive()
                )
        }
        .padding()
        .disabled(vm.state != .data || vm.isLoading)
    }

    private func errorSection(message: String) -> some View {
        VStack {
            Spacer()

            Text(message)
                .padding(.bottom)

            Button("다시 시도") {
                Task {
                    await vm.getMe()
                }
            }

            Spacer()
        }
    }
}
