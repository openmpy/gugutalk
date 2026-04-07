import RefreshButton from "@/components/RefreshButton";
import { AdminGetMemberResponse } from "@/types/AdminGetMemberResponse";
import { PageResponse } from "@/types/PageResponse";
import { formatDate } from "@/utils/formatDate";
import { Search, X } from "lucide-react";
import Image from "next/image";
import Link from "next/link";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

type GenderFilter = "ALL" | "MALE" | "FEMALE";

function parseGender(value: string | undefined): GenderFilter {
  if (value === "MALE" || value === "FEMALE" || value === "ALL") {
    return value;
  }
  return "ALL";
}

function memberListQuery(page: number, size: number, gender: GenderFilter) {
  const sp = new URLSearchParams();
  sp.set("gender", gender.toUpperCase());
  sp.set("page", String(page));
  sp.set("size", String(size));
  return sp.toString();
}

async function getMembers(page: number, size: number, gender: GenderFilter) {
  const qs = memberListQuery(page, size, gender);
  const response = await fetch(`${API_BASE_URL}/api/v1/admin/members?${qs}`, {
    cache: "no-store",
  });

  if (!response.ok) {
    throw new Error("회원 목록을 불러오지 못했습니다.");
  }

  return (await response.json()) as PageResponse<AdminGetMemberResponse>;
}

export default async function MemberListPage({
  searchParams,
}: {
  searchParams?: Promise<{ page?: string; size?: string; gender?: string }>;
}) {
  const params = await searchParams;
  const page = Number(params?.page ?? "0");
  const size = Number(params?.size ?? "20");
  const currentPage = Number.isNaN(page) || page < 0 ? 0 : page;
  const currentSize = Number.isNaN(size) || size <= 0 ? 20 : size;
  const currentGender = parseGender(params?.gender);
  const data = await getMembers(currentPage, currentSize, currentGender);

  return (
    <div className="max-w-7xl mx-auto">
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-bold">회원 목록</h1>
        <RefreshButton />
      </div>

      {/* 회원 카테고리 */}
      <div className="flex items-center gap-2 text-sm font-medium mb-2">
        <Link
          href={`/member?${memberListQuery(0, currentSize, "ALL")}`}
          className={`py-2 rounded-md flex-1 text-center ${
            currentGender === "ALL" ? "bg-slate-500 text-white" : "bg-slate-200"
          }`}
        >
          전체
        </Link>
        <Link
          href={`/member?${memberListQuery(0, currentSize, "FEMALE")}`}
          className={`py-2 rounded-md flex-1 text-center ${
            currentGender === "FEMALE"
              ? "bg-slate-500 text-white"
              : "bg-slate-200"
          }`}
        >
          여자
        </Link>
        <Link
          href={`/member?${memberListQuery(0, currentSize, "MALE")}`}
          className={`py-2 rounded-md flex-1 text-center ${
            currentGender === "MALE"
              ? "bg-slate-500 text-white"
              : "bg-slate-200"
          }`}
        >
          남자
        </Link>
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
              <div className="shrink-0 w-20 md:w-16 aspect-square relative">
                {member.profileUrl ? (
                  <Image
                    src={member.profileUrl}
                    alt={member.nickname}
                    fill
                    sizes="(max-width: 640px) 80px, (max-width: 768px) 112px, 128px"
                    className="rounded-full object-cover"
                    placeholder="blur"
                    blurDataURL={member.profileUrl}
                  />
                ) : (
                  <div className="w-full h-full flex items-center justify-center text-xs text-gray-400 bg-slate-100 rounded-full">
                    <X className="w-4 h-4" />
                  </div>
                )}
              </div>
              <div className="flex-1">
                <div className="flex md:items-center md:justify-between">
                  <Link
                    href={`/member/${member.memberId}`}
                    className="font-bold"
                  >
                    {member.nickname}
                  </Link>
                  <p className="hidden text-sm text-gray-500 md:block">
                    {formatDate(member.updatedAt)}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-gray-500 line-clamp-1">
                    {member.comment}
                  </p>
                  <p className="text-sm text-gray-500">
                    {member.gender === "MALE" ? "남자" : "여자"} · {member.age}
                    살
                  </p>
                  <p className="text-sm text-gray-500 md:hidden">
                    {formatDate(member.updatedAt)}
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
            href={`/member?${memberListQuery(currentPage - 1, currentSize, currentGender)}`}
            className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold"
          >
            이전
          </Link>
        )}
        {data.hasNext && (
          <Link
            href={`/member?${memberListQuery(currentPage + 1, currentSize, currentGender)}`}
            className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold"
          >
            다음
          </Link>
        )}
      </div>
    </div>
  );
}
