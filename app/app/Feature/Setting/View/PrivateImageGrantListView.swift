import SwiftUI

struct PrivateImageGrantListView: View {
    
    @StateObject private var vm = PrivateImageGrantListViewModel()
    
    var body: some View {
        VStack {
            switch vm.state {
                
            case .idle:
                EmptyView()
                
            case .loading:
                ProgressView()
                
            case .empty:
                Text("내역이 비어있습니다.")
                
            case .data:
                listSection
                
            case .error(let message):
                errorSection(message: message)
            }
        }
        .task {
            await vm.getGrantedMember()
        }
        .navigationTitle("비밀 사진 목록")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar(.hidden, for: .tabBar)
    }
    
    // MARK: - SECTION
    
    private var listSection: some View {
        ScrollView {
            LazyVStack {
                ForEach(vm.members) { it in
                    MemberSettingRow(
                        profileUrl: it.profileUrl,
                        nickname: it.nickname,
                        createdAt: it.createdAt,
                        gender: it.gender,
                        age: it.age,
                        onDelete: {
                            Task {
                                do {
                                    try await vm.close(memberId: it.memberId)
                                } catch {
                                    ToastManager.shared.show(error.localizedDescription, type: .error)
                                }
                            }
                        }
                    )
                    .onAppear {
                        if it.id == vm.members.last?.id && vm.hasNext {
                            Task {
                                try? await vm.loadMoreGrantedMember()
                            }
                        }
                    }
                }
                
                if vm.isLoading {
                    ProgressView()
                        .padding()
                }
            }
        }
    }
    
    private func errorSection(message: String) -> some View {
        VStack {
            Text(message)
                .padding(.bottom)
            
            Button("다시 시도") {
                Task {
                    await vm.getGrantedMember()
                }
            }
        }
    }
}
