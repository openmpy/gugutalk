import SwiftUI

struct MemberProfileView: View {
    
    @State private var showMenu: Bool = false
    @State private var showMessage: Bool = false
    @State private var showBlock: Bool = false
    @State private var message: String = ""
    
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
                
                VStack(alignment: .leading, spacing: 10) {
                    HStack {
                        Text("닉네임")
                            .font(.title.bold())
                            .foregroundColor(.primary)
                        
                        Spacer()
                        
                        Text("방금 전")
                            .font(.default)
                            .foregroundColor(.gray)
                    }
                    
                    HStack {
                        Text("남자")
                        Text("·")
                        Text("20살")
                        Text("·")
                        Text("♥ 100")
                        
                        Spacer()
                        
                        Text("12.3km")
                    }
                    .font(.default)
                    .foregroundColor(.gray)
                    .padding(.bottom)
                    
                    Text("자기소개")
                        .foregroundColor(.primary)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding()
                        .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
                }
                .padding()
            }
        }
        .navigationTitle("프로필")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
        .toolbar {
            ToolbarItem(placement: .bottomBar) {
                HStack {
                    Button {
                        // 하트
                    } label: {
                        Image(systemName: "heart.fill")
                            .font(.title3)
                            .foregroundColor(.red)
                    }
                    
                    Spacer()
                    
                    Button {
                        showMessage = true
                    } label: {
                        Image(systemName: "envelope.fill")
                            .font(.title3)
                            .foregroundColor(.blue)
                    }
                    
                    Spacer()
                    
                    Button {
                        // 비밀사진
                    } label: {
                        Image(systemName: "photo.fill")
                            .font(.title3)
                            .foregroundColor(.green)
                    }
                    
                    Spacer()
                    
                    Button {
                        showBlock = true
                    } label: {
                        Image(systemName: "nosign")
                            .font(.title3)
                            .foregroundColor(.orange)
                    }
                }
                .padding(.horizontal)
                .padding(.vertical, 8)
            }
            
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
                    Button("신고하기", role: .destructive) {
                    }
                    Button("취소", role: .cancel) {}
                }
            }
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
