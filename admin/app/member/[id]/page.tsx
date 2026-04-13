import Image from "next/image";
import { HiMiniXMark, HiMinus, HiPlus } from "react-icons/hi2";

export default async function MemberDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;

  return (
    <div>
      <div className="flex items-center justify-between gap-1 text-xs px-2 bg-slate-400 py-1">
        <div>
          <button className="bg-slate-600 text-white px-2 py-1 rounded-md">
            목록
          </button>
        </div>
        <div className="flex gap-1">
          <button className="bg-green-600 text-white px-2 py-1 rounded-md">
            이미지 제거
          </button>
          <button className="bg-blue-500 text-white px-2 py-1 rounded-md">
            닉네임 변경
          </button>
          <button className="bg-purple-500 text-white px-2 py-1 rounded-md">
            코멘트 변경
          </button>
          <button className="bg-red-500 text-white px-2 py-1 rounded-md ">
            정지
          </button>
        </div>
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">정보</h2>
        </div>
        <div className="flex flex-col gap-1">
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">ID</p>
            <p className="text-sm font-mono">{id}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">UUID</p>
            <p className="text-sm">uuid</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">휴대폰</p>
            <p className="text-sm">010-1234-5678</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">닉네임</p>
            <p className="text-sm">닉네임</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">성별</p>
            <p className="text-sm">남자</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">출생연도</p>
            <p className="text-sm">2000</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">코멘트</p>
            <p className="text-sm">코멘트</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">자기소개</p>
            <p className="text-sm">자기소개</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">생성일</p>
            <p className="text-sm">2025-01-01 12:00:00</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">수정일</p>
            <p className="text-sm">2025-01-01 12:00:00</p>
          </div>
        </div>
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">이미지</h2>
        </div>
        <div className="p-2 flex flex-col gap-2">
          <div className="flex flex-col gap-1">
            <h3 className="font-bold text-sm">공개 사진</h3>
            <div className="flex gap-2">
              <div className="relative">
                <Image
                  src="https://picsum.photos/100"
                  alt="member"
                  width={100}
                  height={100}
                  className="w-[100px] h-[100px] shrink-0 rounded-md border border-slate-300"
                  loading="eager"
                  priority
                />
                <button className="absolute top-1 right-1 bg-red-500 text-white rounded-full">
                  <HiMiniXMark className="w-4 h-4" />
                </button>
              </div>
              <div className="relative">
                <Image
                  src="https://picsum.photos/100"
                  alt="member"
                  width={100}
                  height={100}
                  className="w-[100px] h-[100px] shrink-0 rounded-md border border-slate-300"
                  loading="eager"
                  priority
                />
                <button className="absolute top-1 right-1 bg-red-500 text-white rounded-full">
                  <HiMiniXMark className="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>
          <div className="flex flex-col gap-1">
            <h3 className="font-bold text-sm">비밀 사진</h3>
            <div className="flex gap-2">
              <div className="relative">
                <Image
                  src="https://picsum.photos/100"
                  alt="member"
                  width={100}
                  height={100}
                  className="w-[100px] h-[100px] shrink-0 rounded-md border border-slate-300"
                  loading="eager"
                  priority
                />
                <button className="absolute top-1 right-1 bg-red-500 text-white rounded-full">
                  <HiMiniXMark className="w-4 h-4" />
                </button>
              </div>
              <div className="relative">
                <Image
                  src="https://picsum.photos/100"
                  alt="member"
                  width={100}
                  height={100}
                  className="w-[100px] h-[100px] shrink-0 rounded-md border border-slate-300"
                  loading="eager"
                  priority
                />
                <button className="absolute top-1 right-1 bg-red-500 text-white rounded-full">
                  <HiMiniXMark className="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">포인트 내역</h2>
        </div>
        <div className="flex items-center justify-end bg-slate-200 py-1 px-2">
          <p className="text-sm">포인트: 100P</p>
        </div>
        <div className="p-2">
          <ul className="overflow-hidden rounded-md border border-slate-200 bg-white divide-y divide-slate-100">
            <li className="flex items-center gap-3 px-3 py-3">
              <div
                className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-emerald-100 text-emerald-700"
                aria-hidden
              >
                <HiPlus className="h-5 w-5" strokeWidth={2.25} />
              </div>
              <div className="min-w-0 flex-1">
                <p className="text-sm font-semibold text-slate-800">
                  출석 체크
                </p>
                <p className="mt-0.5 text-xs text-slate-500 tabular-nums">
                  <span className="text-emerald-600 font-medium">획득</span>
                  <span className="mx-1.5 text-slate-300">·</span>
                  2026-01-01 12:00:00
                </p>
              </div>
              <p className="shrink-0 text-sm font-bold tabular-nums text-emerald-600">
                +20
              </p>
            </li>
            <li className="flex items-center gap-3 px-3 py-3">
              <div
                className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-rose-100 text-rose-700"
                aria-hidden
              >
                <HiMinus className="h-5 w-5" strokeWidth={2.25} />
              </div>
              <div className="min-w-0 flex-1">
                <p className="text-sm font-semibold text-slate-800">
                  출석 체크
                </p>
                <p className="mt-0.5 text-xs text-slate-500 tabular-nums">
                  <span className="text-rose-600 font-medium">사용</span>
                  <span className="mx-1.5 text-slate-300">·</span>
                  2026-01-01 12:00:00
                </p>
              </div>
              <p className="shrink-0 text-sm font-bold tabular-nums text-rose-600">
                -20
              </p>
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
}
