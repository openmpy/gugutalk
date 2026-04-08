import SuspendModalButton from "@/components/SuspendModalButton";
import { AdminBanGetResponse } from "@/types/AdminBanGetResponse";
import { PageResponse } from "@/types/PageResponse";
import { formatDate } from "@/utils/formatDate";
import { reportTypeLabel } from "@/utils/reportTypeLabel";
import { Search } from "lucide-react";
import Link from "next/link";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

type BanSearchField = "uuid" | "nickname" | "reason";

function parseSearchField(value: string | undefined): BanSearchField {
  if (value === "nickname" || value === "reason") {
    return value;
  }
  return "uuid";
}

function banListQuery(page: number, size: number, field: BanSearchField, keyword: string) {
  const sp = new URLSearchParams();
  sp.set("page", String(page));
  sp.set("size", String(size));
  sp.set("field", field);
  const trimmed = keyword.trim();
  if (trimmed) {
    sp.set("keyword", trimmed);
  }
  return sp.toString();
}

function matchesSearch(
  ban: AdminBanGetResponse,
  field: BanSearchField,
  keyword: string,
): boolean {
  const k = keyword.trim().toLowerCase();
  if (!k) {
    return true;
  }
  if (field === "uuid") {
    return ban.uuid.toLowerCase().includes(k);
  }
  if (field === "nickname") {
    return (ban.nickname ?? "").toLowerCase().includes(k);
  }
  return (ban.reason ?? "").toLowerCase().includes(k);
}

async function getBans(page: number, size: number) {
  const sp = new URLSearchParams();
  sp.set("page", String(page));
  sp.set("size", String(size));
  const response = await fetch(
    `${API_BASE_URL}/api/v1/admin/bans?${sp.toString()}`,
    { cache: "no-store" },
  );
  if (!response.ok) {
    throw new Error("정지 목록을 불러오지 못했습니다.");
  }
  return (await response.json()) as PageResponse<AdminBanGetResponse>;
}

export default async function BanListPage({
  searchParams,
}: {
  searchParams?: Promise<{
    page?: string;
    size?: string;
    keyword?: string;
    field?: string;
  }>;
}) {
  const params = await searchParams;
  const page = Number(params?.page ?? "0");
  const size = Number(params?.size ?? "20");
  const currentPage = Number.isNaN(page) || page < 0 ? 0 : page;
  const currentSize = Number.isNaN(size) || size <= 0 ? 20 : size;
  const currentKeyword = params?.keyword?.trim() ?? "";
  const currentField = parseSearchField(params?.field);

  const data = await getBans(currentPage, currentSize);
  const filtered = data.payload.filter((b) =>
    matchesSearch(b, currentField, currentKeyword),
  );

  return (
    <div className="max-w-7xl mx-auto">
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-bold">정지 목록</h1>
        <div className="flex items-center gap-2">
          <SuspendModalButton
            uuid=""
            nickname=""
            phoneNumber=""
          />
        </div>
      </div>

      <form method="get" action="/ban" className="mb-4">
        <div className="flex items-center gap-2">
          <select
            name="field"
            defaultValue={currentField}
            className="p-2 rounded-md border border-gray-300 focus:outline-none appearance-none text-center font-medium shrink-0"
          >
            <option value="uuid">UUID</option>
            <option value="nickname">닉네임</option>
            <option value="reason">사유</option>
          </select>

          <div className="relative w-full min-w-0">
            <input
              type="text"
              name="keyword"
              defaultValue={currentKeyword}
              placeholder="검색어 입력 (현재 페이지 내)"
              className="w-full px-3 py-2 pr-9 rounded-md border border-gray-300 focus:outline-none"
            />
            <input type="hidden" name="page" value="0" />
            <input type="hidden" name="size" value={String(currentSize)} />
            <button
              type="submit"
              className="absolute right-3 top-1/2 -translate-y-1/2"
              aria-label="검색"
            >
              <Search className="text-gray-400 w-4 h-4" />
            </button>
          </div>
        </div>
      </form>

      <div className="flex flex-col gap-4">
        {filtered.length === 0 ? (
          <p className="text-sm text-gray-500">
            {data.payload.length === 0
              ? "정지 내역이 없습니다."
              : "검색 결과가 없습니다."}
          </p>
        ) : (
          filtered.map((ban) => (
            <div key={ban.banId}>
              <div className="flex items-center justify-between">
                <Link href={`/ban/${ban.banId}`} className="font-bold">
                  {reportTypeLabel(ban.type)}
                </Link>
                <p className="text-sm text-gray-500">
                  해제 예정: {formatDate(ban.expiredAt)}
                </p>
              </div>
              <div>
                <p className="text-sm text-gray-500">UUID: {ban.uuid}</p>
                <p className="text-sm text-gray-500">
                  닉네임: {ban.nickname ?? "-"}
                </p>
                <p className="text-sm text-gray-500 line-clamp-2">
                  사유: {ban.reason?.trim() ? ban.reason : "-"}
                </p>
              </div>
            </div>
          ))
        )}
      </div>

      <div className="flex justify-center gap-2 mt-6">
        {currentPage > 0 && (
          <Link
            href={`/ban?${banListQuery(currentPage - 1, currentSize, currentField, currentKeyword)}`}
            className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold"
          >
            이전
          </Link>
        )}
        {data.hasNext && (
          <Link
            href={`/ban?${banListQuery(currentPage + 1, currentSize, currentField, currentKeyword)}`}
            className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold"
          >
            다음
          </Link>
        )}
      </div>
    </div>
  );
}
