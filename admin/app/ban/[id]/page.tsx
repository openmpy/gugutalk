export default function BanDetailPage() {
  return (
    <div className="max-w-7xl mx-auto">
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-2">
        <h1 className="text-2xl font-bold">도배</h1>
      </div>
      <div className="flex items-center gap-2 mb-4">
        <button className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold">
          해제
        </button>
      </div>

      {/* 정지 정보 */}
      <div className="flex flex-col gap-4">
        <div>
          <h2 className="text-lg font-medium">정보</h2>
          <p className="text-gray-500">ID: 1</p>
          <p className="text-gray-500">UUID: 1234567890</p>
          <p className="text-gray-500">닉네임: 홍길동</p>
          <p className="text-gray-500">휴대폰: 010-0000-0000</p>
          <hr className="my-2 border-gray-200" />
          <p className="text-gray-500">정지 일자: 2026-01-01 12:00:00</p>
          <p className="text-gray-500">해제 일자: 2026-01-01 12:00:00</p>
        </div>

        <div>
          <h2 className="text-lg font-medium">사유</h2>
          <p className="text-gray-500">내용</p>
        </div>
      </div>
    </div>
  );
}
