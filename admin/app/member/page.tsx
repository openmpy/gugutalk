import { Search } from "lucide-react";
import Image from "next/image";
import Link from "next/link";

export default function MemberListPage() {
  return (
    <div className="max-w-7xl mx-auto">
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-bold">회원 목록</h1>
        <button className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold">
          새로고침
        </button>
      </div>

      {/* 회원 카테고리 */}
      <div className="flex items-center gap-2 text-sm font-medium mb-2">
        <button className="py-2 rounded-md bg-slate-200 flex-1">전체</button>
        <button className="py-2 rounded-md bg-slate-200 flex-1">여자</button>
        <button className="py-2 rounded-md bg-slate-200 flex-1">남자</button>
      </div>

      {/* 회원 검색 */}
      <div className="flex items-center gap-2 mb-4">
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

      {/* 회원 상세 */}
      <div className="flex flex-col gap-4">
        <div className="flex items-center gap-3">
          <div className="w-20 h-20 rounded-full overflow-hidden">
            <Image
              width={200}
              height={200}
              src="https://picsum.photos/200"
              alt="profile"
              className="w-full h-full rounded-full"
              placeholder="blur"
              blurDataURL="https://picsum.photos/200"
              loading="lazy"
            />
          </div>
          <div className="flex-1">
            <div className="flex items-center justify-between">
              <Link href={`/member/${1}`} className="font-bold">
                닉네임
              </Link>
              <p className="text-sm text-gray-500">최근 접속일</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">코멘트</p>
              <p className="text-sm text-gray-500">성별 · 20살 · ♥ 100</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
