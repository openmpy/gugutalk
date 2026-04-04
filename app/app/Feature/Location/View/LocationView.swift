import SwiftUI

struct LocationView: View {

    @StateObject private var vm = LocationViewModel()
    @StateObject private var locationManager = LocationManager()

    var body: some View {
        NavigationStack {
            VStack {
                GenderSelector(selectGender: $vm.selectGender)

                switch vm.state {

                case .idle:
                    Spacer()
                    EmptyView()
                    Spacer()

                case .loading:
                    Spacer()
                    ProgressView()
                    Spacer()

                case .unauthorized:
                    unauthorizedSection

                case .empty:
                    Spacer()
                    Text("내역이 비어있습니다.")
                    Spacer()

                case .data:
                    listSection

                case .error(let message):
                    errorSection(message: message)
                }
            }
            .task {
                if locationManager.isAuthorized {
                    try? await locationManager.fetchLocation()
                    try? await vm.bump(
                        latitude: locationManager.latitude,
                        longitude: locationManager.longitude
                    )

                    await vm.getLocationMembers()
                } else {
                    vm.state = .unauthorized
                    locationManager.requestPermission()
                }
            }
            .onChange(of: vm.selectGender) { _, _ in
                Task {
                    await vm.getLocationMembers()
                }
            }
            .overlay {
                if vm.isLoading {
                    LoadingOverlay()
                }
            }
            .navigationTitle("거리")
            .navigationBarTitleDisplayMode(.inline)
        }
    }

    // MARK: - SECTION

    private var listSection: some View {
        ScrollView {
            LazyVStack {
                ForEach(vm.members) { it in
                    NavigationLink {
                        MemberProfileView(memberId: it.memberId)
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
                        if it.id == vm.members.last?.id && vm.hasNext {
                            Task {
                                try? await vm.loadMoreLocationMembers()
                            }
                        }
                    }
                }

                if vm.isPaging {
                    ProgressView()
                        .padding()
                }
            }
        }
        .refreshable {
            Task {
                try? await locationManager.fetchLocation()
                try? await vm.bump(
                    latitude: locationManager.latitude,
                    longitude: locationManager.longitude
                )

                await vm.getLocationMembers()
            }
        }
    }

    private var unauthorizedSection: some View {
        VStack {
            Spacer()

            Text("위치 접근 권한이 없습니다.")
                .padding(.bottom)

            Button("설정으로 이동") {
                UIApplication.shared.open(URL(string: UIApplication.openSettingsURLString)!)
            }

            Spacer()
        }
    }

    private func errorSection(message: String) -> some View {
        VStack {
            Spacer()

            Text(message)
                .padding(.bottom)

            Button("다시 시도") {
                Task {
                    await vm.getLocationMembers()
                }
            }

            Spacer()
        }
    }
}
