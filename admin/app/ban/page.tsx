export default function BanListPage() {
  return (
    <div className="max-w-7xl mx-auto">
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-bold">정지 목록</h1>
        <button className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold">
          추가
        </button>
      </div>

      {/* 정지 상세 */}
      <div className="flex flex-col gap-4">
        <div>
          <div className="flex items-center justify-between">
            <p className="font-bold">도배</p>
            <p className="text-sm text-gray-500">해제일</p>
          </div>
          <div>
            <p className="text-sm text-gray-500">UUID: 1234567890</p>
            <p className="text-sm text-gray-500">닉네임: 박명수</p>
            <p className="text-sm text-gray-500 line-clamp-2">사유: 내용</p>
          </div>
        </div>
        <div>
          <div className="flex items-center justify-between">
            <p className="font-bold">도배</p>
            <p className="text-sm text-gray-500">해제일</p>
          </div>
          <div>
            <p className="text-sm text-gray-500">UUID: 1234567890</p>
            <p className="text-sm text-gray-500">닉네임: 박명수</p>
            <p className="text-sm text-gray-500 line-clamp-2">사유: 내용</p>
          </div>
        </div>
      </div>
    </div>
  );
}
