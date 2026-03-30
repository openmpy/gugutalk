import SwiftUI

struct MemberProfileView: View {
    
    @State private var showMenu: Bool = false
    @State private var showMessage: Bool = false
    @State private var showBlock: Bool = false
    @State private var goReport: Bool = false
    @State private var message: String = ""
    
    @Namespace var namespace
    
    var body: some View {
        VStack {
            ScrollView {
                TabView {
                    ForEach(0..<10) { i in
                        Image(systemName: "person.fill")
                            .resizable()
                            .scaledToFit()
                            .padding(100)
                            .frame(maxWidth: .infinity, maxHeight: .infinity)
                            .foregroundColor(Color(.systemGray3))
                            .background(Color(.systemGray6))
                            .tag(i)
                    }
                }
                .tabViewStyle(PageTabViewStyle())
                .aspectRatio(4/3, contentMode: .fit)
                .clipped()
                
                MemberProfileInfo(
                    nickname: "닉네임",
                    updatedAt: "2026-03-30T12:00:00.0000",
                    gender: "MALE",
                    age: 20,
                    bio: "자기소개",
                    likes: 100,
                    distance: 12.34
                )
            }
        }
        .safeAreaInset(edge: .bottom) {
            GlassEffectContainer {
                HStack(spacing: 25) {
                    Button {
                        
                    } label: {
                        Image(systemName: "heart.fill")
                            .font(.title)
                            .frame(width: 60, height: 60)
                            .foregroundColor(.red)
                            .glassEffect(.regular.interactive())
                            .glassEffectUnion(id: 1, namespace: namespace)
                    }
                    
                    Button {
                        showMessage = true
                    } label: {
                        Image(systemName: "envelope.fill")
                            .font(.title)
                            .frame(width: 60, height: 60)
                            .foregroundColor(.blue)
                            .glassEffect(.regular.interactive())
                            .glassEffectUnion(id: 1, namespace: namespace)
                    }
                    
                    Button {
                        
                    } label: {
                        Image(systemName: "photo.fill")
                            .font(.title)
                            .frame(width: 60, height: 60)
                            .foregroundColor(.green)
                            .glassEffect(.regular.interactive())
                            .glassEffectUnion(id: 1, namespace: namespace)
                    }
                    
                    Button {
                        showBlock = true
                    } label: {
                        Image(systemName: "nosign")
                            .font(.title)
                            .frame(width: 60, height: 60)
                            .foregroundColor(.orange)
                            .glassEffect(.regular.interactive())
                            .glassEffectUnion(id: 1, namespace: namespace)
                    }
                }
            }
        }
        .navigationTitle("프로필")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button {
                    showMenu = true
                } label: {
                    Image(systemName: "ellipsis")
                        .font(.title3)
                        .foregroundColor(.primary)
                }
                .confirmationDialog("메뉴", isPresented: $showMenu) {
                    Button("비밀 사진 공개") {
                    }
                    
                    Button("신고", role: .destructive) {
                        goReport = true
                    }
                    
                    Button("취소", role: .cancel) { }
                }
            }
        }
        .navigationDestination(isPresented: $goReport) {
            ReportView()
        }
        .alert("쪽지", isPresented: $showMessage) {
            TextField("내용 입력", text: $message)
            
            Button("전송", role: .confirm) {
                if message.isEmpty {
                    return
                }
            }
            Button("취소", role: .cancel) { }
        }
        .alert("차단", isPresented: $showBlock) {
            Button("차단", role: .destructive) {
            }
            Button("취소", role: .cancel) { }
        } message: {
            Text("채팅 내역이 모두 삭제되며 서로의 목록에서도 표시되지 않습니다.")
        }
    }
}

#Preview {
    MemberProfileView()
}
