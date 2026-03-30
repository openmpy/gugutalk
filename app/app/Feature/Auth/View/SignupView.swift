import SwiftUI

struct SignupView: View {

    @State private var phoneNumber: String = ""
    @State private var verificationCode: String = ""
    @State private var password: String = ""
    @State private var password2: String = ""
    @State private var selectGender: String = "MALE"

    private var isSubmit: Bool {
        !phoneNumber.isEmpty && !verificationCode.isEmpty && !password.isEmpty && !password2.isEmpty
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

                        } label: {
                            Text("전송")
                                .font(.subheadline.bold())
                                .frame(width: 60, height: 40)
                                .foregroundColor(.white)
                                .background(
                                    !phoneNumber.isEmpty ? .blue : Color(.systemGray3),
                                    in: RoundedRectangle(cornerRadius: 20)
                                )
                        }
                        .disabled(phoneNumber.isEmpty)
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

                    SecureField("비밀번호 확인", text: $password2)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
                        .scrollContentBackground(.hidden)
                        .textInputAutocapitalization(.never)
                        .disableAutocorrection(true)
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
            NavigationLink {
                ActivateView()
            } label: {
                Text("회원가입")
                    .font(.default.bold())
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical)
                    .glassEffect(.regular.tint(isSubmit ? Color(.blue) : Color(.systemGray3)).interactive())
            }
            .disabled(!isSubmit)
            .padding()
        }
        .navigationTitle("회원가입")
        .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    SignupView()
}
