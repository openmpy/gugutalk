import Image from "next/image";
import { HiMiniXMark } from "react-icons/hi2";
import ListButton from "@/component/ListButton";

export default async function ReportDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;

  return (
    <div>
      <div className="flex items-center justify-between gap-1 text-xs px-2 bg-slate-400 py-1">
        <ListButton href="/report" />
        <div className="flex gap-1">
          <button className="bg-red-500 text-white px-2 py-1 rounded-md">
            반려
          </button>
          <button className="bg-blue-500 text-white px-2 py-1 rounded-md">
            처분
          </button>
        </div>
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">신고자</h2>
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
        </div>
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">피신고자</h2>
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
        </div>
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">신고 내용</h2>
        </div>
        <div className="flex flex-col gap-1">
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">ID</p>
            <p className="text-sm font-mono">{id}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">유형</p>
            <p className="text-sm">도배</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">사유</p>
            <p className="text-sm">사유</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">신고일</p>
            <p className="text-sm">2026-04-12 12:00:00</p>
          </div>
        </div>
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">증거 자료</h2>
        </div>
        <div className="p-2 flex flex-col">
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
  );
}
