import ReportListLoadMore from "@/component/ReportListLoadMore";
import {
  fetchAdminReports,
  normalizeAdminReportStatus,
  normalizeAdminReportType,
  type AdminReportListStatus,
  type AdminReportSearchType,
} from "@/lib/reports";
import Link from "next/link";
import { IoSearch } from "react-icons/io5";

function firstString(v: string | string[] | undefined): string | undefined {
  if (Array.isArray(v)) return v[0];
  return v;
}

function reportListHref(base: {
  type: AdminReportSearchType;
  keyword: string;
  status: AdminReportListStatus;
}) {
  const q = new URLSearchParams();
  q.set("type", base.type);
  if (base.keyword) q.set("keyword", base.keyword);
  if (base.status !== "PENDING") q.set("status", base.status);
  const s = q.toString();
  return s ? `/report?${s}` : "/report";
}

export default async function ReportPage({
  searchParams,
}: {
  searchParams: Promise<Record<string, string | string[] | undefined>>;
}) {
  const sp = await searchParams;
  const type = normalizeAdminReportType(firstString(sp.type));
  const keyword = firstString(sp.keyword) ?? "";
  const status = normalizeAdminReportStatus(firstString(sp.status));

  const result = await fetchAdminReports({ type, keyword, status });

  const filterBase = { type, keyword, status };

  return (
    <div>
      <div className="flex items-center justify-center bg-slate-300 py-1">
        <h1 className="font-bold">신고 내역</h1>
      </div>
      <div className="flex items-center">
        <Link
          href={reportListHref({ ...filterBase, status: "PENDING" })}
          className={`flex-1 border-r border-r-slate-300 px-2 py-1 text-center text-white ${
            status === "PENDING" ? "bg-slate-500" : "bg-slate-400"
          }`}
        >
          접수
        </Link>
        <Link
          href={reportListHref({ ...filterBase, status: "REJECT" })}
          className={`flex-1 border-r border-r-slate-300 px-2 py-1 text-center text-white ${
            status === "REJECT" ? "bg-slate-500" : "bg-slate-400"
          }`}
        >
          반려
        </Link>
        <Link
          href={reportListHref({ ...filterBase, status: "RESOLVE" })}
          className={`flex-1 px-2 py-1 text-center text-white ${
            status === "RESOLVE" ? "bg-slate-500" : "bg-slate-400"
          }`}
        >
          처분
        </Link>
      </div>
      <form method="get" className="flex flex-wrap items-center">
        <input type="hidden" name="status" value={status} />
        <select
          name="type"
          defaultValue={type}
          className="h-9 w-[85px] shrink-0 border border-l-0 border-slate-300 bg-white px-2 text-base focus:outline-none"
        >
          <option value="NICKNAME">닉네임</option>
          <option value="UUID">UUID</option>
          <option value="PHONE">휴대폰</option>
        </select>
        <input
          type="text"
          name="keyword"
          defaultValue={keyword}
          placeholder="검색어를 입력해주세요."
          className="h-9 min-w-0 flex-1 border border-l-0 border-r-0 border-slate-300 pl-2 text-base focus:outline-none"
        />
        <button
          type="submit"
          className="h-9 shrink-0 border border-l-0 border-r-0 border-slate-300 px-2 text-base text-slate-600"
          aria-label="검색"
        >
          <IoSearch className="h-4 w-4" />
        </button>
      </form>
      {!result.ok ? (
        <p className="px-2 py-4 text-sm text-red-600">
          신고 목록을 불러오지 못했습니다. (HTTP {result.status}) 서버 주소는
          환경 변수 <span className="font-mono">ADMIN_API_BASE_URL</span> 로
          설정할 수 있습니다.
        </p>
      ) : result.data.payload.length === 0 ? (
        <p className="px-2 py-4 text-center text-sm text-slate-600">
          조회된 신고가 없습니다.
        </p>
      ) : (
        <ReportListLoadMore
          key={JSON.stringify({ type, keyword, status })}
          initial={result.data}
          type={type}
          keyword={keyword}
          status={status}
        />
      )}
    </div>
  );
}
