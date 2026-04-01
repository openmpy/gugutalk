import SwiftUI
import PhotosUI

struct ImagePicker: View {
    
    let maxImages: Int
    
    @Binding var images: [PhotosPickerItem]
    @Binding var selectImages: [IdentifiableImage]
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack {
                ForEach(selectImages) { item in
                    Image(uiImage: item.image)
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
                                            selectImages.append(IdentifiableImage(image: uiImage))
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
