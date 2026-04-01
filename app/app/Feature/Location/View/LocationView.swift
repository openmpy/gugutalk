import SwiftUI
import Toasts

struct LocationView: View {

    @StateObject private var vm = LocationViewModel()
    @StateObject private var locationManager = LocationManager()

    @Environment(\.presentToast) var presentToast

    @State private var selectGender: String = "ALL"

    var body: some View {
        NavigationStack {
            VStack {
                if !locationManager.isAuthorized {
                    Spacer()
                    VStack(spacing: 20) {
                        Text("위치 권한을 허용해주세요.")
                            .foregroundColor(.primary)

                        Button {
                            if let url = URL(string: UIApplication.openSettingsURLString) {
                                UIApplication.shared.open(url)
                            }
                        } label: {
                            Text("설정으로 이동")
                                .font(.default.bold())
                                .frame(height: 40)
                                .padding(.horizontal)
                                .foregroundColor(.white)
                                .background(.blue, in: RoundedRectangle(cornerRadius: 20))
                        }
                    }
                    Spacer()
                } else {
                    GenderSelector(selectGender: $selectGender)

                    if vm.members.isEmpty && !vm.isLoading {
                        Spacer()
                        Text("주변 회원이 없습니다.")
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
                                                let result = await vm.loadMoreLocationMembers(gender: selectGender.uppercased())
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

                                let result = await vm.getLocationMembers(gender: selectGender.uppercased())
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
            .task {
                if locationManager.isAuthorized {
                    try? await locationManager.fetchLocation()

                    let result = await vm.getLocationMembers(gender: selectGender.uppercased())
                    if case .failure(let error) = result {
                        presentToast(ToastValue(
                            icon: Image(systemName: "xmark.circle.fill"),
                            message: error.localizedDescription
                        ))
                    }
                } else {
                    locationManager.requestPermission()
                }
            }
            .onChange(of: selectGender) { _, newValue in
                Task {
                    let result = await vm.getLocationMembers(gender: newValue.uppercased())
                    if case .failure(let error) = result {
                        presentToast(ToastValue(
                            icon: Image(systemName: "xmark.circle.fill"),
                            message: error.localizedDescription
                        ))
                    }
                }
            }
            .navigationTitle("위치")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}
