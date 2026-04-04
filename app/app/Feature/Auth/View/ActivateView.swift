import SwiftUI
import PhotosUI

struct ActivateView: View {

    @AppStorage("isLoggedIn") private var isLoggedIn: Bool = false

    @StateObject private var vm = ActivateViewModel()

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                profileImageSection
                nicknameSection
                birthYearSection
                bioSection
            }
            .padding()
        }
        .onTapGesture { hideKeyboard() }
        .safeAreaInset(edge: .bottom) { activateButton }
        .overlay {
            if vm.isLoading {
                LoadingOverlay()
            }
        }
        .navigationTitle("프로필 설정")
        .navigationBarTitleDisplayMode(.inline)
        .navigationBarBackButtonHidden(true)
        .interactiveDismissDisabled(true)
    }

    // MARK: - SECTION

    private var profileImageSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("프로필 사진")
                .font(.subheadline)
                .fontWeight(.semibold)
                .foregroundStyle(.primary)

            ImagePicker(maxImages: 5, images: $vm.images, selectImages: $vm.selectImages)
        }
    }

    private var nicknameSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("닉네임")
                .font(.subheadline)
                .fontWeight(.semibold)
                .foregroundStyle(.primary)

            TextField("홍길동", text: $vm.nickname)
                .padding(.horizontal, 16)
                .padding(.vertical, 10)
                .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
                .textInputAutocapitalization(.never)
                .disableAutocorrection(true)
        }
    }

    private var birthYearSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("출생연도")
                .font(.subheadline)
                .fontWeight(.semibold)
                .foregroundStyle(.primary)

            TextField("2000", value: $vm.birthYear, format: .number.grouping(.never))
                .padding(.horizontal, 16)
                .padding(.vertical, 10)
                .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
                .textInputAutocapitalization(.never)
                .disableAutocorrection(true)
                .keyboardType(.numberPad)
        }
    }

    private var bioSection: some View {
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

    private var activateButton: some View {
        Button {
            Task {
                do {
                    try await vm.activate()
                    
                    ToastManager.shared.show("계정이 활성화되었습니다.")
                    isLoggedIn = true
                } catch {
                    ToastManager.shared.show(error.localizedDescription, type: .error)
                }
            }
        } label: {
            Text("들어가기")
                .font(.default.bold())
                .foregroundStyle(.white)
                .frame(maxWidth: .infinity)
                .padding(.vertical)
                .glassEffect(.regular.tint(vm.isSubmittable ? Color(.blue) : Color(.systemGray3)).interactive())
        }
        .disabled(!vm.isSubmittable || vm.isLoading)
        .padding()
    }
}
