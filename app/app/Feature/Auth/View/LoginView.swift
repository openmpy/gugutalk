import SwiftUI

struct LoginView: View {
    
    @State private var phoneNumber: String = ""
    @State private var password: String = ""
    
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
                    
                } label: {
                    Text("로그인")
                        .font(.default.bold())
                        .foregroundStyle(.white)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical)
                        .glassEffect(.regular.tint(Color(.blue)).interactive())
                }
                .padding()
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
