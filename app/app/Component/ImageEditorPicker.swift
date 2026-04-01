import SwiftUI
import PhotosUI

struct ImageEditorPicker: View {
    
    let maxImages: Int
    
    @Binding var images: [PhotosPickerItem]
    @Binding var selectImages: [IdentifiableEditorImage]
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack {
                ForEach(selectImages) { item in
                    if let uiImage = item.image {
                        Image(uiImage: uiImage)
                            .resizable()
                            .scaledToFill()
                            .frame(width: 100, height: 100)
                            .clipShape(RoundedRectangle(cornerRadius: 16))
                            .overlay(alignment: .topTrailing) {
                                Button {
                                    selectImages.removeAll { $0.id == item.id }
                                } label: {
                                    Image(systemName: "xmark.circle.fill")
                                        .foregroundStyle(.white, .red)
                                        .padding(6)
                                }
                            }
                    } else {
                        AsyncImage(url: URL(string: item.url ?? "")) { phase in
                            switch phase {
                            case .empty:
                                ProgressView()
                                    .frame(width: 100, height: 100)
                                    .background(Color(.systemGray6))
                                    .clipShape(RoundedRectangle(cornerRadius: 16))
                            case .success(let image):
                                image
                                    .resizable()
                                    .scaledToFill()
                                    .frame(width: 100, height: 100)
                                    .clipShape(RoundedRectangle(cornerRadius: 16))
                                    .overlay(alignment: .topTrailing) {
                                        Button {
                                            selectImages.removeAll { $0.id == item.id }
                                        } label: {
                                            Image(systemName: "xmark.circle.fill")
                                                .foregroundStyle(.white, .red)
                                                .padding(6)
                                        }
                                    }
                            case .failure:
                                Image(systemName: "photo")
                                    .font(.title)
                                    .frame(width: 100, height: 100)
                                    .foregroundColor(Color(.systemGray3))
                                    .background(Color(.systemGray6))
                                    .clipShape(RoundedRectangle(cornerRadius: 16))
                                    .overlay(alignment: .topTrailing) {
                                        Button {
                                            selectImages.removeAll { $0.id == item.id }
                                        } label: {
                                            Image(systemName: "xmark.circle.fill")
                                                .foregroundStyle(.white, .red)
                                                .padding(6)
                                        }
                                    }
                            @unknown default:
                                EmptyView()
                            }
                        }
                        .frame(width: 100, height: 100)
                        .clipped()
                    }
                }
                
                if selectImages.count < maxImages {
                    PhotosPicker(
                        selection: $images,
                        maxSelectionCount: maxImages - selectImages.count,
                        matching: .images
                    ) {
                        Image(systemName: "plus")
                            .font(.title)
                            .foregroundStyle(Color(.systemGray3))
                            .frame(width: 100, height: 100)
                            .background(Color(.systemGray6), in: RoundedRectangle(cornerRadius: 20))
                    }
                    .onChange(of: images) { _, newItems in
                        Task {
                            for item in newItems {
                                do {
                                    if let data = try await item.loadTransferable(type: Data.self),
                                       let uiImage = UIImage(data: data) {
                                        
                                        if selectImages.count < maxImages {
                                            selectImages.append(IdentifiableEditorImage(image: uiImage))
                                        }
                                    }
                                } catch {
                                    print("이미지 로딩 실패: \(error.localizedDescription)")
                                }
                            }
                            images = []
                        }
                    }
                }
            }
        }
    }
}
