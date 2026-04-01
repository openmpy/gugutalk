import SwiftUI
import Toasts

struct RecentView: View {

    @StateObject private var vm = RecentViewModel()
    @StateObject private var locationManager = LocationManager()

    @Environment(\.presentToast) var presentToast

    @State private var selectGender: String = "ALL"
    @State private var showComment: Bool = false
    @State private var comment: String = ""

    var body: some View {
        NavigationStack {
            VStack {
                GenderSelector(selectGender: $selectGender)

                if vm.members.isEmpty {
                    Spacer()
                    Text("내역이 비어있습니다.")
                        .foregroundColor(.primary)
                    Spacer()
                } else {
                    ScrollView {
                        LazyVStack {
                            ForEach(vm.members) { it in
                                NavigationLink {
                                    MemberProfileView()
                                } label: {
                                    MemberRow(
                                        profileUrl: it.profileUrl,
                                        nickname: it.nickname,
                                        updatedAt: it.updatedAt,
                                        content: it.comment ?? "",
                                        gender: it.gender,
                                        age: it.age,
                                        likes: it.likes,
                                        distance: it.distance
                                    )
                                }
                                .onAppear {
                                    if it.id == vm.members.last?.id {
                                        Task {
                                            let result = await vm.loadMoreGrantedMember(gender: selectGender.uppercased())
                                            if case .failure(let error) = result {
                                                presentToast(ToastValue(
                                                    icon: Image(systemName: "xmark.circle.fill"),
                                                    message: error.localizedDescription
                                                ))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    .refreshable {
                        Task {
                            let bumpResult = await vm.bump(
                                latitude: locationManager.latitude,
                                longitude: locationManager.longitude
                            )
                            if case .failure(let error) = bumpResult {
                                presentToast(ToastValue(
                                    icon: Image(systemName: "xmark.circle.fill"),
                                    message: error.localizedDescription
                                ))
                            }

                            try? await locationManager.fetchLocation()

                            let result = await vm.getRecentMembers(gender: selectGender.uppercased())
                            if case .failure(let error) = result {
                                presentToast(ToastValue(
                                    icon: Image(systemName: "xmark.circle.fill"),
                                    message: error.localizedDescription
                                ))
                            }
                        }
                    }
                }
            }
            .task {
                try? await locationManager.fetchLocation()
                
                let result = await vm.getRecentMembers(gender: selectGender.uppercased())
                if case .failure(let error) = result {
                    presentToast(ToastValue(
                        icon: Image(systemName: "xmark.circle.fill"),
                        message: error.localizedDescription
                    ))
                }
            }
            .onChange(of: selectGender) { _, newValue in
                Task {
                    let result = await vm.getRecentMembers(gender: newValue.uppercased())
                    if case .failure(let error) = result {
                        presentToast(ToastValue(
                            icon: Image(systemName: "xmark.circle.fill"),
                            message: error.localizedDescription
                        ))
                    }
                }
            }
            .navigationTitle("최근")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    NavigationLink {
                        MemberSearchView()
                    } label: {
                        Image(systemName: "magnifyingglass")
                            .font(.title3)
                            .foregroundColor(.primary)
                    }
                }

                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        showComment = true
                    } label: {
                        Image(systemName: "square.and.pencil")
                            .font(.title3)
                            .foregroundColor(.primary)
                    }
                }
            }
            .alert("코멘트", isPresented: $showComment) {
                TextField("내용 입력", text: $comment)
                Button("작성", role: .confirm) {
                    if comment.isEmpty { return }
                }
                Button("취소", role: .cancel) { }
            }
        }
    }
}

#Preview {
    RecentView()
}
