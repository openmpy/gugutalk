export type AdminGetReportResponse = {
  reportId: number;
  type: string;
  reporterNickname: string;
  reportedNickname: string;
  reason: string | null;
  hasImage: boolean;
  createdAt: string;
};

export type AdminReportImageResponse = {
  imageId: number;
  index: number;
  url: string;
};

const STATUSES = ["PENDING", "REJECT", "RESOLVE"] as const;
export type AdminReportListStatus = (typeof STATUSES)[number];

export type AdminGetReportDetailResponse = {
  reportId: number;
  reporterId: number;
  reporterUuid: string;
  reporterPhoneNumber: string;
  reporterNickname: string;
  reportedId: number;
  reportedUuid: string;
  reportedPhoneNumber: string;
  reportedNickname: string;
  type: string;
  reason: string | null;
  status: AdminReportListStatus;
  createdAt: string;
  images: AdminReportImageResponse[];
};

const SEARCH_TYPES = ["NICKNAME", "UUID", "PHONE"] as const;
export type AdminReportSearchType = (typeof SEARCH_TYPES)[number];

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

const REPORT_STATUS_LABELS: Record<AdminReportListStatus, string> = {
  PENDING: "대기",
  REJECT: "반려",
  RESOLVE: "처분",
};

export function formatAdminReportStatusLabel(status: string): string {
  const upper = status.toUpperCase() as AdminReportListStatus;
  return REPORT_STATUS_LABELS[upper] ?? status;
}

export function buildAdminReportUpdateUpstreamUrl(
  reportId: number,
  status: AdminReportListStatus,
): string {
  const base = process.env.ADMIN_API_BASE_URL ?? "http://127.0.0.1:8080";
  const root = base.endsWith("/") ? base : `${base}/`;
  const url = new URL(`/api/v1/admin/reports/${reportId}`, root);
  url.searchParams.set("status", status);
  return url.toString();
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

export function buildAdminReportDetailUpstreamUrl(reportId: number): string {
  const base = process.env.ADMIN_API_BASE_URL ?? "http://127.0.0.1:8080";
  const root = base.endsWith("/") ? base : `${base}/`;
  return new URL(`/api/v1/admin/reports/${reportId}`, root).toString();
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
