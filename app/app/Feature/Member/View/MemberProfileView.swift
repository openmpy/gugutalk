import SwiftUI

struct MemberProfileView: View {

    var body: some View {
        VStack {
            Text("프로필")
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
                        // 쪽지
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
                        // 차단
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
                    // 더보기
                } label: {
                    Image(systemName: "ellipsis")
                        .font(.title3)
                        .foregroundColor(.primary)
                }
            }
        }
    }
}

#Preview {
    MemberProfileView()
}
