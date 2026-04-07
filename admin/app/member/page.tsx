import { PageResponse } from "@/types/PageResponse";
import { Search } from "lucide-react";
import Link from "next/link";

type AdminMemberGetResponse = {
  memberId: number;
  profileUrl: string | null;
  nickname: string;
  age: number;
  gender: "MALE" | "FEMALE";
  comment: string | null;
  updatedAt: string;
  deletedAt: string | null;
};

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

function formatGender(gender: AdminMemberGetResponse["gender"]) {
  return gender === "MALE" ? "남자" : "여자";
}

function formatDate(value: string) {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return "-";
  }

  return date.toLocaleString("ko-KR", {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hourCycle: "h23",
  });
}

async function getMembers(page: number, size: number) {
  const response = await fetch(
    `${API_BASE_URL}/api/v1/admin/members?page=${page}&size=${size}`,
    { cache: "no-store" },
  );

  if (!response.ok) {
    throw new Error("회원 목록을 불러오지 못했습니다.");
  }

  return (await response.json()) as PageResponse<AdminMemberGetResponse>;
}

export default async function MemberListPage({
  searchParams,
}: {
  searchParams?: Promise<{ page?: string; size?: string }>;
}) {
  const params = await searchParams;
  const page = Number(params?.page ?? "0");
  const size = Number(params?.size ?? "20");
  const currentPage = Number.isNaN(page) || page < 0 ? 0 : page;
  const currentSize = Number.isNaN(size) || size <= 0 ? 20 : size;
  const data = await getMembers(currentPage, currentSize);

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
        {data.payload.length === 0 ? (
          <p className="text-sm text-gray-500">회원 정보가 없습니다.</p>
        ) : (
          data.payload.map((member) => (
            <div key={member.memberId} className="flex items-center gap-3">
              <div className="w-20 h-20 rounded-full overflow-hidden bg-slate-100">
                {member.profileUrl ? (
                  // eslint-disable-next-line @next/next/no-img-element
                  <img
                    src={member.profileUrl}
                    alt={member.nickname}
                    className="w-full h-full rounded-full object-cover"
                  />
                ) : (
                  <div className="w-full h-full flex items-center justify-center text-xs text-gray-400">
                    이미지 없음
                  </div>
                )}
              </div>
              <div className="flex-1">
                <div className="flex items-center justify-between">
                  <Link
                    href={`/member/${member.memberId}`}
                    className="font-bold"
                  >
                    {member.nickname}
                  </Link>
                  <p className="text-sm text-gray-500">
                    {formatDate(member.updatedAt)}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">{member.comment}</p>
                  <p className="text-sm text-gray-500">
                    {formatGender(member.gender)} · {member.age}살
                  </p>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      <div className="flex justify-center gap-2 mt-6">
        {currentPage > 0 && (
          <Link
            href={`/member?page=${currentPage - 1}&size=${currentSize}`}
            className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold"
          >
            이전
          </Link>
        )}
        {data.hasNext && (
          <Link
            href={`/member?page=${currentPage + 1}&size=${currentSize}`}
            className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold"
          >
            다음
          </Link>
        )}
      </div>
    </div>
  );
}
