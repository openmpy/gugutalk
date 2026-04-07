import { Search } from "lucide-react";

export default function ReportListPage() {
  return (
    <div className="max-w-7xl mx-auto">
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-bold">신고 목록</h1>
        <button className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold">
          새로고침
        </button>
      </div>

      {/* 신고 분류 */}
      <div className="flex items-center gap-2 text-sm font-medium mb-2">
        <button className="py-2 rounded-md bg-slate-200 flex-1">보류</button>
        <button className="py-2 rounded-md bg-slate-200 flex-1">완료</button>
      </div>

      {/* 신고 검색 */}
      <div className="flex items-center gap-2 mb-4">
        <select className="p-2 rounded-md border border-gray-300 focus:outline-none appearance-none text-center font-medium">
          <option value="reporter">신고자</option>
          <option value="reported">피신고자</option>
        </select>

        <div className="relative w-full">
          <input
            type="text"
            placeholder="닉네임 입력"
            className="w-full p-2 pr-9 rounded-md border border-gray-300 focus:outline-none"
          />
          <button className="absolute right-3 top-1/2 -translate-y-1/2">
            <Search className="text-gray-400 w-4 h-4" />
          </button>
        </div>
      </div>

      {/* 신고 상세 */}
      <div className="flex flex-col gap-4">
        <div>
          <div className="flex items-center justify-between">
            <p className="font-bold">도배</p>
            <p className="text-sm text-gray-500">신고일</p>
          </div>
          <div>
            <p className="text-sm text-gray-500">신고자: 유재석</p>
            <p className="text-sm text-gray-500">피신고자: 박명수</p>
            <p className="text-sm text-gray-500 line-clamp-2">사유: 내용</p>
          </div>
        </div>
        <div>
          <div className="flex items-center justify-between">
            <p className="font-bold">도배</p>
            <p className="text-sm text-gray-500">신고일</p>
          </div>
          <div>
            <p className="text-sm text-gray-500">신고자: 유재석</p>
            <p className="text-sm text-gray-500">피신고자: 박명수</p>
            <p className="text-sm text-gray-500 line-clamp-2">사유: 내용</p>
          </div>
        </div>
      </div>
    </div>
  );
}
