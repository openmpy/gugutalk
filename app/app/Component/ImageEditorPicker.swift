import SwiftUI
import PhotosUI
import UniformTypeIdentifiers

struct ImageEditorPicker: View {

    let maxImages: Int

    @Binding var images: [PhotosPickerItem]
    @Binding var selectImages: [IdentifiableEditorImage]

    @State private var draggingItem: IdentifiableEditorImage?
    @State private var hasChangedLocation = false

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack {
                ForEach(selectImages) { item in
                    imageView(for: item)
                        .opacity(draggingItem?.id == item.id && hasChangedLocation ? 0.5 : 1.0)
                        .onDrag {
                            draggingItem = item
                            return NSItemProvider(object: item.id.uuidString as NSString)
                        }
                        .onDrop(of: [UTType.text], delegate: ImageDropDelegate(
                            item: item,
                            items: $selectImages,
                            draggingItem: $draggingItem,
                            hasChangedLocation: $hasChangedLocation
                        ))
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

    // MARK: - 이미지 뷰 빌더
    
    @ViewBuilder
    private func imageView(for item: IdentifiableEditorImage) -> some View {
        if let uiImage = item.image {
            Image(uiImage: uiImage)
                .resizable()
                .scaledToFill()
                .frame(width: 100, height: 100)
                .clipShape(RoundedRectangle(cornerRadius: 16))
                .overlay(alignment: .topTrailing) {
                    deleteButton(for: item)
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
                            deleteButton(for: item)
                        }
                case .failure:
                    Image(systemName: "photo")
                        .font(.title)
                        .frame(width: 100, height: 100)
                        .foregroundColor(Color(.systemGray3))
                        .background(Color(.systemGray6))
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                        .overlay(alignment: .topTrailing) {
                            deleteButton(for: item)
                        }
                @unknown default:
                    EmptyView()
                }
            }
            .frame(width: 100, height: 100)
            .clipped()
        }
    }

    private func deleteButton(for item: IdentifiableEditorImage) -> some View {
        Button {
            selectImages.removeAll { $0.id == item.id }
        } label: {
            Image(systemName: "xmark.circle.fill")
                .foregroundStyle(.white, .red)
                .padding(6)
        }
    }
}

// MARK: - Drop Delegate

struct ImageDropDelegate: DropDelegate {
    let item: IdentifiableEditorImage
    @Binding var items: [IdentifiableEditorImage]
    @Binding var draggingItem: IdentifiableEditorImage?
    @Binding var hasChangedLocation: Bool

    func dropEntered(info: DropInfo) {
        guard let draggingItem, draggingItem.id != item.id,
              let fromIndex = items.firstIndex(where: { $0.id == draggingItem.id }),
              let toIndex = items.firstIndex(where: { $0.id == item.id })
        else { return }

        hasChangedLocation = true
        items.move(fromOffsets: IndexSet(integer: fromIndex), toOffset: toIndex > fromIndex ? toIndex + 1 : toIndex)
    }

    func dropUpdated(info: DropInfo) -> DropProposal? {
        DropProposal(operation: .move)
    }

    func performDrop(info: DropInfo) -> Bool {
        hasChangedLocation = false
        draggingItem = nil
        return true
    }
}
