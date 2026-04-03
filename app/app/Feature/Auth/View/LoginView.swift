import SwiftUI

struct LoginView: View {

    @AppStorage("isLoggedIn") private var isLoggedIn: Bool = false

    @StateObject private var vm = LoginViewModel()

    var body: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: 20) {
                VStack(alignment: .leading, spacing: 12) {
                    Text("휴대폰")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundStyle(.primary)

                    TextField("휴대폰 번호", text: $vm.phoneNumber)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
                        .textInputAutocapitalization(.never)
                        .disableAutocorrection(true)
                        .keyboardType(.numberPad)
                }

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
                }

                NavigationLink {
                    SignupView()
                } label: {
                    Text("회원가입")
                        .font(.subheadline)
                        .foregroundStyle(.blue)
                }
                .frame(maxWidth: .infinity)

                Spacer()
            }
            .padding()
            .background(Color(.systemBackground))
            .onTapGesture { hideKeyboard() }
            .safeAreaInset(edge: .bottom) {
                Button {
                    Task {
                        do {
                            try await vm.login()
                            isLoggedIn = true
                        } catch {
                            ToastManager.shared.show(error.localizedDescription, type: .error)
                        }
                    }
                } label: {
                    Text("로그인")
                        .font(.default.bold())
                        .foregroundStyle(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical)
                        .glassEffect(
                            .regular
                                .tint(vm.isSubmittable ? Color(.blue) : Color(.systemGray3))
                                .interactive()
                        )
                }
                .padding()
                .disabled(!vm.isSubmittable || vm.isLoading)
            }
            .navigationTitle("로그인")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}
