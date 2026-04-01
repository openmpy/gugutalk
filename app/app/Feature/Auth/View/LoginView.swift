import SwiftUI
import Toasts

struct LoginView: View {
    
    @AppStorage("isLoggedIn") private var isLoggedIn: Bool = false
    
    @StateObject private var vm = LoginViewModel()
    
    @Environment(\.presentToast) var presentToast
    
    @State private var phoneNumber: String = ""
    @State private var password: String = ""
    
    private var isPhoneNumberValid: Bool {
        phoneNumber.starts(with: "010") && phoneNumber.count == 11
    }
    private var isSubmit: Bool {
        isPhoneNumberValid && !password.isEmpty
    }
    
    var body: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: 20) {
                VStack(alignment: .leading, spacing: 12) {
                    Text("휴대폰")
                        .font(.subheadline)
                        .fontWeight(.semibold)
                        .foregroundStyle(.primary)
                    
                    TextField("휴대폰 번호", text: $phoneNumber)
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
            .background(Color(.systemBackground))
            .onTapGesture {
                hideKeyboard()
            }
            .safeAreaInset(edge: .bottom) {
                Button {
                    Task {
                        let result = await vm.login(phoneNumber: phoneNumber, password: password)
                        switch result {
                        case .success:
                            isLoggedIn = true
                        case .failure(let error):
                            let toast = ToastValue(
                                icon: Image(systemName: "xmark.circle.fill").foregroundColor(.red),
                                message: error.localizedDescription
                            )
                            presentToast(toast)
                        }
                    }
                } label: {
                    Text("로그인")
                        .font(.default.bold())
                        .foregroundStyle(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical)
                        .glassEffect(.regular.tint(isSubmit ? Color(.blue) : Color(.systemGray3)).interactive())
                }
                .padding()
                .disabled(!isSubmit || vm.isLoading)
            }
            .padding()
            .navigationTitle("로그인")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

#Preview {
    LoginView()
}
