import RefreshButton from "@/components/RefreshButton";
import { AdminGetReportResponse } from "@/types/AdminGetReportResponse";
import { PageResponse } from "@/types/PageResponse";
import { formatDate } from "@/utils/formatDate";
import { reportTypeLabel } from "@/utils/reportTypeLabel";
import { Search } from "lucide-react";
import Link from "next/link";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

type ReportStatusFilter = "PENDING" | "REJECT" | "RESOLVE";

type ReportNickTarget = "reporter" | "reported";

function parseStatus(value: string | undefined): ReportStatusFilter {
  if (value === "REJECT" || value === "RESOLVE") {
    return value;
  }
  return "PENDING";
}

function parseNickTarget(value: string | undefined): ReportNickTarget {
  if (value === "reported") {
    return "reported";
  }
  return "reporter";
}

function reportListQuery(
  page: number,
  size: number,
  status: ReportStatusFilter,
  keyword?: string,
  nickTarget?: ReportNickTarget,
) {
  const sp = new URLSearchParams();
  sp.set("status", status);
  sp.set("page", String(page));
  sp.set("size", String(size));
  const trimmed = keyword?.trim();
  if (trimmed) {
    sp.set("keyword", trimmed);
  }
  if (nickTarget === "reported") {
    sp.set("nickTarget", "reported");
  }
  return sp.toString();
}

function reportSearchQuery(
  page: number,
  size: number,
  status: ReportStatusFilter,
  keyword: string,
  nickTarget: ReportNickTarget,
) {
  const sp = new URLSearchParams();
  sp.set("type", nickTarget === "reported" ? "reported" : "reporter");
  sp.set("keyword", keyword);
  sp.set("status", status);
  sp.set("page", String(page));
  sp.set("size", String(size));
  return sp.toString();
}

async function getReports(
  page: number,
  size: number,
  status: ReportStatusFilter,
  keyword: string,
  nickTarget: ReportNickTarget,
) {
  const trimmed = keyword.trim();
  const path = trimmed
    ? `/api/v1/admin/reports/search?${reportSearchQuery(page, size, status, trimmed, nickTarget)}`
    : `/api/v1/admin/reports?${reportListQuery(page, size, status)}`;

  const response = await fetch(`${API_BASE_URL}${path}`, {
    cache: "no-store",
  });

  if (!response.ok) {
    throw new Error("신고 목록을 불러오지 못했습니다.");
  }

  return (await response.json()) as PageResponse<AdminGetReportResponse>;
}

export default async function ReportListPage({
  searchParams,
}: {
  searchParams?: Promise<{
    page?: string;
    size?: string;
    status?: string;
    keyword?: string;
    nickTarget?: string;
  }>;
}) {
  const params = await searchParams;
  const page = Number(params?.page ?? "0");
  const size = Number(params?.size ?? "20");
  const currentPage = Number.isNaN(page) || page < 0 ? 0 : page;
  const currentSize = Number.isNaN(size) || size <= 0 ? 20 : size;
  const currentStatus = parseStatus(params?.status);
  const currentKeyword = params?.keyword?.trim() ?? "";
  const currentNickTarget = parseNickTarget(params?.nickTarget);
  const data = await getReports(
    currentPage,
    currentSize,
    currentStatus,
    currentKeyword,
    currentNickTarget,
  );

  return (
    <div className="max-w-7xl mx-auto">
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-bold">신고 목록</h1>
        <RefreshButton />
      </div>

      {/* 신고 분류 */}
      <div className="flex items-center gap-2 text-sm font-medium mb-2">
        <Link
          href={`/report?${reportListQuery(0, currentSize, "PENDING", currentKeyword, currentNickTarget)}`}
          className={`py-2 rounded-md flex-1 text-center ${
            currentStatus === "PENDING"
              ? "bg-slate-500 text-white"
              : "bg-slate-200"
          }`}
        >
          접수
        </Link>
        <Link
          href={`/report?${reportListQuery(0, currentSize, "REJECT", currentKeyword, currentNickTarget)}`}
          className={`py-2 rounded-md flex-1 text-center ${
            currentStatus === "REJECT"
              ? "bg-slate-500 text-white"
              : "bg-slate-200"
          }`}
        >
          반려
        </Link>
        <Link
          href={`/report?${reportListQuery(0, currentSize, "RESOLVE", currentKeyword, currentNickTarget)}`}
          className={`py-2 rounded-md flex-1 text-center ${
            currentStatus === "RESOLVE"
              ? "bg-slate-500 text-white"
              : "bg-slate-200"
          }`}
        >
          처리
        </Link>
      </div>

      {/* 신고 검색 */}
      <form method="get" action="/report" className="mb-4">
        <div className="flex items-center gap-2">
          <select
            name="nickTarget"
            defaultValue={currentNickTarget}
            className="p-2 rounded-md border border-gray-300 focus:outline-none appearance-none text-center font-medium shrink-0"
          >
            <option value="reporter">신고자</option>
            <option value="reported">피신고자</option>
          </select>

          <div className="relative w-full min-w-0">
            <input
              type="text"
              name="keyword"
              defaultValue={currentKeyword}
              placeholder="닉네임 입력"
              className="w-full px-3 py-2 pr-9 rounded-md border border-gray-300 focus:outline-none"
            />
            <input type="hidden" name="status" value={currentStatus} />
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

      {/* 신고 상세 */}
      <div className="flex flex-col gap-4">
        {data.payload.length === 0 ? (
          <p className="text-sm text-gray-500">신고 내역이 없습니다.</p>
        ) : (
          data.payload.map((report) => (
            <div key={report.reportId}>
              <div className="flex items-center justify-between">
                <Link href={`/report/${report.reportId}`} className="font-bold">
                  {reportTypeLabel(report.type)}
                </Link>
                <p className="text-sm text-gray-500">
                  {formatDate(report.createdAt)}
                </p>
              </div>
              <div>
                <p className="text-sm text-gray-500">
                  신고자: {report.reporterNickname}
                </p>
                <p className="text-sm text-gray-500">
                  피신고자: {report.reportedNickname}
                </p>
                <p className="text-sm text-gray-500 line-clamp-2">
                  사유: {report.reason?.trim() ? report.reason : "-"}
                </p>
              </div>
            </div>
          ))
        )}
      </div>

      <div className="flex justify-center gap-2 mt-6">
        {currentPage > 0 && (
          <Link
            href={`/report?${reportListQuery(currentPage - 1, currentSize, currentStatus, currentKeyword, currentNickTarget)}`}
            className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold"
          >
            이전
          </Link>
        )}
        {data.hasNext && (
          <Link
            href={`/report?${reportListQuery(currentPage + 1, currentSize, currentStatus, currentKeyword, currentNickTarget)}`}
            className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold"
          >
            다음
          </Link>
        )}
      </div>
    </div>
  );
}
