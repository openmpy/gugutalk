import { X } from "lucide-react";
import Image from "next/image";

export default function MemberDetailPage() {
  return (
    <div className="max-w-7xl mx-auto">
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-2">
        <h1 className="text-2xl font-bold">홍길동</h1>
      </div>
      <div className="flex items-center gap-2 mb-4">
        <button className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold">
          닉네임
        </button>
        <button className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold">
          코멘트
        </button>
        <button className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold">
          자기소개
        </button>
        <button className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold">
          정지
        </button>
      </div>

      {/* 회원 정보 */}
      <div className="flex flex-col gap-4">
        <div>
          <h2 className="text-lg font-medium">정보</h2>
          <p className="text-gray-500">ID: 1</p>
          <p className="text-gray-500">UUID: 1234567890</p>
          <p className="text-gray-500">휴대폰: 010-0000-0000</p>
          <p className="text-gray-500">출생연도: 2000년생</p>
          <p className="text-gray-500">성별: 남자</p>
          <hr className="my-2 border-gray-200" />
          <p className="text-gray-500">생성일자: 2026-01-01 12:00:00</p>
          <p className="text-gray-500">수정일자: 2026-01-01 12:00:00</p>
          <p className="text-gray-500">탈퇴일자: 2026-01-01 12:00:00</p>
        </div>

        <div>
          <h2 className="text-lg font-medium">공개 사진</h2>
          <div className="flex gap-2 overflow-x-auto">
            {[...Array(5)].map((_, i) => (
              <div
                key={i}
                className="shrink-0 w-20 sm:w-28 md:w-32 aspect-square relative"
              >
                <Image
                  width={200}
                  height={200}
                  src="https://picsum.photos/200"
                  alt="profile"
                  placeholder="blur"
                  blurDataURL="https://picsum.photos/200"
                  loading="lazy"
                  className="rounded-md object-cover"
                />

                <button className="absolute top-1 right-1 w-4 h-4 bg-red-500 text-white rounded-full flex items-center justify-center text-xs shadow">
                  <X className="w-3 h-3" />
                </button>
              </div>
            ))}
          </div>
        </div>

        <div>
          <h2 className="text-lg font-medium">비밀 사진</h2>
          <div className="flex gap-2 overflow-x-auto">
            {[...Array(5)].map((_, i) => (
              <div
                key={i}
                className="shrink-0 w-20 sm:w-28 md:w-32 aspect-square relative"
              >
                <Image
                  width={200}
                  height={200}
                  src="https://picsum.photos/200"
                  alt="profile"
                  placeholder="blur"
                  blurDataURL="https://picsum.photos/200"
                  loading="lazy"
                  className="rounded-md object-cover"
                />

                <button className="absolute top-1 right-1 w-4 h-4 bg-red-500 text-white rounded-full flex items-center justify-center text-xs shadow">
                  <X className="w-3 h-3" />
                </button>
              </div>
            ))}
          </div>
        </div>

        <div>
          <h2 className="text-lg font-medium">코멘트</h2>
          <p className="text-gray-500">코멘트</p>
        </div>

        <div>
          <h2 className="text-lg font-medium">자기소개</h2>
          <p className="text-gray-500">자기소개</p>
        </div>
      </div>
    </div>
  );
}
