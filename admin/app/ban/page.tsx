import { Search } from "lucide-react";
import Link from "next/link";

export default function BanListPage() {
  return (
    <div className="max-w-7xl mx-auto">
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-bold">정지 목록</h1>
        <button className="px-4 py-2 rounded-md bg-red-500 text-sm font-semibold text-white">
          정지
        </button>
      </div>

      {/* 신고 검색 */}
      <div className="flex items-center gap-2 mb-4">
        <select className="p-2 rounded-md border border-gray-300 focus:outline-none appearance-none text-center font-medium">
          <option value="uuid">UUID</option>
          <option value="nickname">닉네임</option>
          <option value="phone">휴대폰</option>
        </select>

        <div className="relative w-full">
          <input
            type="text"
            placeholder="내용 입력"
            className="w-full p-2 pr-9 rounded-md border border-gray-300 focus:outline-none"
          />
          <button className="absolute right-3 top-1/2 -translate-y-1/2">
            <Search className="text-gray-400 w-4 h-4" />
          </button>
        </div>
      </div>

      {/* 정지 상세 */}
      <div className="flex flex-col gap-4">
        <div>
          <div className="flex items-center justify-between">
            <Link href={`/ban/1`} className="font-bold">
              도배
            </Link>
            <p className="text-sm text-gray-500">해제일</p>
          </div>
          <div>
            <p className="text-sm text-gray-500">UUID: 1234567890</p>
            <p className="text-sm text-gray-500">닉네임: 홍길동</p>
            <p className="text-sm text-gray-500 line-clamp-2">사유: 내용</p>
          </div>
        </div>
      </div>
    </div>
  );
}
