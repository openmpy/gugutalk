import SwiftUI

struct SignupView: View {

    @StateObject private var vm = SignupViewModel()

    @State private var goActivate: Bool = false

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                phoneSection
                passwordSection
                genderSection
            }
            .padding()
        }
        .onTapGesture { hideKeyboard() }
        .safeAreaInset(edge: .bottom) { signupButton }
        .onDisappear { vm.invalidateTimer() }
        .overlay {
            if vm.isLoading {
                LoadingOverlay()
            }
        }
        .navigationTitle("회원가입")
        .navigationBarTitleDisplayMode(.inline)
        .navigationDestination(isPresented: $goActivate) {
            ActivateView()
        }
    }

    // MARK: - SECTION

    private var phoneSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("휴대폰")
                .font(.subheadline)
                .fontWeight(.semibold)
                .foregroundStyle(.primary)

            HStack {
                TextField("휴대폰 번호", text: $vm.phoneNumber)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 10)
                    .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
                    .textInputAutocapitalization(.never)
                    .disableAutocorrection(true)
                    .keyboardType(.numberPad)

                Button {
                    Task {
                        do {
                            try await vm.sendVerificationCode()

                            ToastManager.shared.show("인증 번호가 전송되었습니다.")
                        } catch {
                            ToastManager.shared.show(error)
                        }
                    }
                } label: {
                    Text(vm.isSent ? "\(vm.timeRemaining)" : "전송")
                        .font(.subheadline.bold())
                        .frame(width: 60, height: 40)
                        .foregroundColor(.white)
                        .background(
                            vm.isPhoneNumberValid && !vm.isSent ? .blue : Color(.systemGray3),
                            in: RoundedRectangle(cornerRadius: 20)
                        )
                }
                .disabled(!vm.isPhoneNumberValid || vm.isSent)
            }

            TextField("인증 번호", text: $vm.verificationCode)
                .padding(.horizontal, 16)
                .padding(.vertical, 10)
                .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
                .textInputAutocapitalization(.never)
                .disableAutocorrection(true)
                .keyboardType(.numberPad)
        }
    }

    private var passwordSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("비밀번호")
                .font(.subheadline)
                .fontWeight(.semibold)
                .foregroundStyle(.primary)

            SecureField("비밀번호", text: $vm.password)
                .padding(.horizontal, 16)
                .padding(.vertical, 10)
                .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
                .textInputAutocapitalization(.never)
                .disableAutocorrection(true)
                .textContentType(.oneTimeCode)

            SecureField("비밀번호 확인", text: $vm.password2)
                .padding(.horizontal, 16)
                .padding(.vertical, 10)
                .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
                .textInputAutocapitalization(.never)
                .disableAutocorrection(true)
                .textContentType(.oneTimeCode)
        }
    }

    private var genderSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("성별")
                .font(.subheadline)
                .fontWeight(.semibold)
                .foregroundStyle(.primary)

            HStack {
                ForEach([("남자", "MALE"), ("여자", "FEMALE")], id: \.1) { label, value in
                    Button {
                        vm.selectedGender = value
                    } label: {
                        Text(label)
                            .font(.subheadline.bold())
                            .frame(maxWidth: .infinity)
                            .frame(height: 40)
                            .foregroundColor(.white)
                            .background(
                                vm.selectedGender == value ? .blue : Color(.systemGray3),
                                in: RoundedRectangle(cornerRadius: 20)
                            )
                    }
                }
            }
        }
    }

    private var signupButton: some View {
        Button {
            Task {
                do {
                    try await vm.signup()

                    ToastManager.shared.show("회원가입이 완료되었습니다.")
                    goActivate = true
                } catch {
                    ToastManager.shared.show(error)
                }
            }
        } label: {
            Text("회원가입")
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
