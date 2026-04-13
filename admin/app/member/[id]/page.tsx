import Image from "next/image";
import { HiMiniXMark } from "react-icons/hi2";

export default async function MemberPage({
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
            이미지 변경
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
        <div className="p-2 flex flex-col gap-1">
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
        <div className="p-2 flex flex-col gap-1">
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
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">포인트 내역</h2>
        </div>
        <div className="flex items-center justify-end bg-slate-200 py-1 px-2">
          <p className="text-sm">포인트: 100P</p>
        </div>
        <div className="w-full overflow-x-auto">
          <table className="w-full min-w-max text-sm border-collapse">
            <thead className="bg-slate-100">
              <tr>
                <th className="text-center py-1 px-2 w-16">유형</th>
                <th className="text-center py-1 px-2 w-16">수치</th>
                <th className="text-center py-1 px-2 w-48">내용</th>
                <th className="text-left py-1 px-2 w-44">일시</th>
              </tr>
            </thead>
            <tbody>
              <tr className="border-b border-slate-100">
                <td className="px-2 py-1 text-center w-16">
                  <span className="inline-block rounded-md bg-green-100 text-green-700 text-xs font-semibold px-2 py-1">
                    획득
                  </span>
                </td>
                <td className="text-center py-1 px-2 font-bold text-green-600 w-16">
                  +20
                </td>
                <td className="text-center py-1 px-2 w-48">출석 체크</td>
                <td className="text-left py-1 px-2 w-44">
                  2026-01-01 12:00:00
                </td>
              </tr>
              <tr className="border-b border-slate-100">
                <td className="px-2 py-1 text-center w-16">
                  <span className="inline-block rounded-md bg-red-100 text-red-700 text-xs font-semibold px-2 py-1">
                    사용
                  </span>
                </td>
                <td className="text-center py-1 px-2 font-bold text-red-600 w-16">
                  -20
                </td>
                <td className="text-center py-1 px-2 w-48">출석 체크</td>
                <td className="text-left py-1 px-2 w-44">
                  2026-01-01 12:00:00
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
