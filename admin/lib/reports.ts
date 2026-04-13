import type { CursorResponse } from "@/lib/members";

export type AdminGetReportResponse = {
  reportId: number;
  type: string;
  reporterNickname: string;
  reportedNickname: string;
  reason: string | null;
  hasImage: boolean;
  createdAt: string;
};

const SEARCH_TYPES = ["NICKNAME", "UUID", "PHONE"] as const;
export type AdminReportSearchType = (typeof SEARCH_TYPES)[number];

const STATUSES = ["PENDING", "REJECT", "RESOLVE"] as const;
export type AdminReportListStatus = (typeof STATUSES)[number];

export function normalizeAdminReportType(
  value: string | undefined,
): AdminReportSearchType {
  const upper = (value ?? "NICKNAME").toUpperCase();
  return SEARCH_TYPES.includes(upper as AdminReportSearchType)
    ? (upper as AdminReportSearchType)
    : "NICKNAME";
}

export function normalizeAdminReportStatus(
  value: string | undefined,
): AdminReportListStatus {
  const upper = (value ?? "PENDING").toUpperCase();
  return STATUSES.includes(upper as AdminReportListStatus)
    ? (upper as AdminReportListStatus)
    : "PENDING";
}

const REPORT_TYPE_LABELS: Record<string, string> = {
  ABUSE: "욕설 / 비방",
  SPAM: "스팸 / 광고",
  MINOR: "미성년자",
  SEXUAL: "음란물",
  FAKE: "도용",
  ETC: "기타",
};

export function formatAdminReportTypeLabel(apiType: string): string {
  return REPORT_TYPE_LABELS[apiType.toUpperCase()] ?? apiType;
}

export function buildAdminReportsUpstreamUrl(params: {
  type: AdminReportSearchType;
  keyword: string;
  status: AdminReportListStatus;
  cursorId?: string;
  cursorDate?: string;
  size?: number;
}): string {
  const base = process.env.ADMIN_API_BASE_URL ?? "http://127.0.0.1:8080";
  const url = new URL(
    "/api/v1/admin/reports",
    base.endsWith("/") ? base : `${base}/`,
  );
  url.searchParams.set("type", params.type);
  url.searchParams.set("keyword", params.keyword);
  url.searchParams.set("status", params.status);
  url.searchParams.set("size", String(params.size ?? 20));
  if (params.cursorId && params.cursorDate) {
    url.searchParams.set("cursorId", params.cursorId);
    url.searchParams.set("cursorDate", params.cursorDate);
  }
  return url.toString();
}

export function adminReportsProxyQuery(params: {
  type: AdminReportSearchType;
  keyword: string;
  status: AdminReportListStatus;
  cursorId?: string;
  cursorDate?: string;
  size?: number;
}): string {
  const q = new URLSearchParams();
  q.set("type", params.type);
  q.set("keyword", params.keyword);
  q.set("status", params.status);
  q.set("size", String(params.size ?? 20));
  if (params.cursorId && params.cursorDate) {
    q.set("cursorId", params.cursorId);
    q.set("cursorDate", params.cursorDate);
  }
  return q.toString();
}

export async function fetchAdminReports(params: {
  type: AdminReportSearchType;
  keyword: string;
  status: AdminReportListStatus;
  cursorId?: string;
  cursorDate?: string;
  size?: number;
}): Promise<
  | { ok: true; data: CursorResponse<AdminGetReportResponse> }
  | { ok: false; status: number }
> {
  const res = await fetch(buildAdminReportsUpstreamUrl(params), {
    cache: "no-store",
  });
  if (!res.ok) return { ok: false, status: res.status };

  const data = (await res.json()) as CursorResponse<AdminGetReportResponse>;
  return { ok: true, data };
}
