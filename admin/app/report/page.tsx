import RefreshButton from "@/components/RefreshButton";
import { AdminGetReportResponse } from "@/types/AdminGetReportResponse";
import { PageResponse } from "@/types/PageResponse";
import { formatDate } from "@/utils/formatDate";
import { reportTypeLabel } from "@/utils/reportTypeLabel";
import Link from "next/link";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

type ReportStatusFilter = "PENDING" | "REJECT" | "RESOLVE";

function parseStatus(value: string | undefined): ReportStatusFilter {
  if (value === "REJECT" || value === "RESOLVE") {
    return value;
  }
  return "PENDING";
}

function reportListQuery(
  page: number,
  size: number,
  status: ReportStatusFilter,
) {
  const sp = new URLSearchParams();
  sp.set("status", status);
  sp.set("page", String(page));
  sp.set("size", String(size));
  return sp.toString();
}

async function getReports(
  page: number,
  size: number,
  status: ReportStatusFilter,
) {
  const qs = reportListQuery(page, size, status);
  const response = await fetch(`${API_BASE_URL}/api/v1/admin/reports?${qs}`, {
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
  }>;
}) {
  const params = await searchParams;
  const page = Number(params?.page ?? "0");
  const size = Number(params?.size ?? "20");
  const currentPage = Number.isNaN(page) || page < 0 ? 0 : page;
  const currentSize = Number.isNaN(size) || size <= 0 ? 20 : size;
  const currentStatus = parseStatus(params?.status);
  const data = await getReports(currentPage, currentSize, currentStatus);

  return (
    <div className="max-w-7xl mx-auto">
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-2xl font-bold">신고 목록</h1>
        <RefreshButton />
      </div>

      {/* 신고 분류 */}
      <div className="flex items-center gap-2 text-sm font-medium mb-4">
        <Link
          href={`/report?${reportListQuery(0, currentSize, "PENDING")}`}
          className={`py-2 rounded-md flex-1 text-center ${
            currentStatus === "PENDING"
              ? "bg-slate-500 text-white"
              : "bg-slate-200"
          }`}
        >
          접수
        </Link>
        <Link
          href={`/report?${reportListQuery(0, currentSize, "REJECT")}`}
          className={`py-2 rounded-md flex-1 text-center ${
            currentStatus === "REJECT"
              ? "bg-slate-500 text-white"
              : "bg-slate-200"
          }`}
        >
          보류
        </Link>
        <Link
          href={`/report?${reportListQuery(0, currentSize, "RESOLVE")}`}
          className={`py-2 rounded-md flex-1 text-center ${
            currentStatus === "RESOLVE"
              ? "bg-slate-500 text-white"
              : "bg-slate-200"
          }`}
        >
          처리
        </Link>
      </div>

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
            href={`/report?${reportListQuery(currentPage - 1, currentSize, currentStatus)}`}
            className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold"
          >
            이전
          </Link>
        )}
        {data.hasNext && (
          <Link
            href={`/report?${reportListQuery(currentPage + 1, currentSize, currentStatus)}`}
            className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold"
          >
            다음
          </Link>
        )}
      </div>
    </div>
  );
}
