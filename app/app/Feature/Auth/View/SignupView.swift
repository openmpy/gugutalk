import SwiftUI

struct SignupView: View {

    @StateObject private var vm = SignupViewModel()

    @State private var phoneNumber: String = ""
    @State private var verificationCode: String = ""
    @State private var sendVerificationCode = false
    @State private var password: String = ""
    @State private var password2: String = ""
    @State private var selectGender: String = "MALE"
    @State private var showAlert: Bool = false
    @State private var alertMessage: String = ""
    @State private var goActivate: Bool = false
    @State private var timeRemaining: Int = 0
    @State private var timer: Timer?

    private var isPhoneNumberValid: Bool {
        phoneNumber.starts(with: "010") && phoneNumber.count == 11
    }
    private var isPasswordValid: Bool {
        password == password2
    }
    private var isSubmit: Bool {
        isPhoneNumberValid && !verificationCode.isEmpty &&
        !password.isEmpty && !password2.isEmpty && sendVerificationCode
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                VStack(alignment: .leading, spacing: 12) {
                    Text("휴대폰")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundStyle(.primary)

                    HStack {
                        TextField("휴대폰 번호", text: $phoneNumber)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 10)
                            .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
                            .scrollContentBackground(.hidden)
                            .textInputAutocapitalization(.never)
                            .disableAutocorrection(true)
                            .keyboardType(.numberPad)

                        Button {
                            if !isPhoneNumberValid {
                                showAlert = true
                                alertMessage = "올바른 휴대폰 번호를 입력해주세요."
                                return
                            }

                            Task {
                                if await vm.sendCodeVerificationCode(phoneNumber: phoneNumber) {
                                    startTimer()
                                }
                            }
                        } label: {
                            Text(sendVerificationCode ? "\(timeRemaining)" : "전송")
                                .font(.subheadline.bold())
                                .frame(width: 60, height: 40)
                                .foregroundColor(.white)
                                .background(
                                    isPhoneNumberValid && !sendVerificationCode ? .blue : Color(.systemGray3),
                                    in: RoundedRectangle(cornerRadius: 20)
                                )
                        }
                        .disabled(!isPhoneNumberValid || sendVerificationCode)
                    }

                    TextField("인증 번호", text: $verificationCode)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
                        .scrollContentBackground(.hidden)
                        .textInputAutocapitalization(.never)
                        .disableAutocorrection(true)
                        .keyboardType(.numberPad)
                }

                VStack(alignment: .leading, spacing: 12) {
                    Text("비밀번호")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundStyle(.primary)

                    SecureField("비밀번호", text: $password)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
                        .scrollContentBackground(.hidden)
                        .textInputAutocapitalization(.never)
                        .disableAutocorrection(true)
                        .textContentType(.oneTimeCode)

                    SecureField("비밀번호 확인", text: $password2)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
                        .scrollContentBackground(.hidden)
                        .textInputAutocapitalization(.never)
                        .disableAutocorrection(true)
                        .textContentType(.oneTimeCode)
                }

                VStack(alignment: .leading, spacing: 12) {
                    Text("성별")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundStyle(.primary)

                    HStack {
                        Button {
                            selectGender = "MALE"
                        } label: {
                            Text("남자")
                                .font(.subheadline.bold())
                                .frame(maxWidth: .infinity)
                                .frame(height: 40)
                                .foregroundColor(.white)
                                .background(
                                    selectGender == "MALE" ? .blue : Color(.systemGray3),
                                    in: RoundedRectangle(cornerRadius: 20)
                                )
                        }

                        Button {
                            selectGender = "FEMALE"
                        } label: {
                            Text("여자")
                                .font(.subheadline.bold())
                                .frame(maxWidth: .infinity)
                                .frame(height: 40)
                                .foregroundColor(.white)
                                .background(
                                    selectGender == "FEMALE" ? .blue : Color(.systemGray3),
                                    in: RoundedRectangle(cornerRadius: 20)
                                )
                        }
                    }
                }
            }
            .padding()
        }
        .onTapGesture {
            hideKeyboard()
        }
        .safeAreaInset(edge: .bottom) {
            Button {
                if password != password2 {
                    showAlert = true
                    alertMessage = "비밀번호가 일치하지 않습니다."
                    return
                }

                Task {
                    if await vm.signup(
                        phoneNumber: phoneNumber,
                        verificationCode: verificationCode,
                        password: password,
                        gender: selectGender
                    ) {
                        goActivate = true
                    }
                }
            } label: {
                Text("회원가입")
                    .font(.default.bold())
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical)
                    .glassEffect(.regular.tint(isSubmit ? Color(.blue) : Color(.systemGray3)).interactive())
            }
            .disabled(!isSubmit || vm.isLoading)
            .padding()
        }
        .onDisappear {
            timer?.invalidate()
            timer = nil
        }
        .navigationTitle("회원가입")
        .navigationBarTitleDisplayMode(.inline)
        .navigationDestination(isPresented: $goActivate) {
            ActivateView()
        }
        .alert("알림", isPresented: $showAlert) {
            Button("확인", role: .cancel) { }
        } message: {
            Text(alertMessage)
        }
        .alert("에러", isPresented: $vm.showErrorAlert) {
            Button("확인", role: .cancel) { }
        } message: {
            Text(vm.errorMessage)
        }
    }

    private func startTimer() {
        sendVerificationCode = true
        timeRemaining = 300

        timer?.invalidate()
        timer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { _ in
            if timeRemaining > 0 {
                timeRemaining -= 1
            } else {
                sendVerificationCode = false
                timer?.invalidate()
            }
        }
    }
}

#Preview {
    SignupView()
}
