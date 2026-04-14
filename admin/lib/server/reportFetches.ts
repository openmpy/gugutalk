import "server-only";

import type { CursorResponse } from "@/lib/members";
import { adminAuthorizedFetch } from "@/lib/adminUpstream";
import {
  buildAdminReportDetailUpstreamUrl,
  buildAdminReportsUpstreamUrl,
  type AdminGetReportDetailResponse,
  type AdminGetReportResponse,
  type AdminReportListStatus,
  type AdminReportSearchType,
} from "@/lib/reports";

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
  const res = await adminAuthorizedFetch(buildAdminReportsUpstreamUrl(params));
  if (!res.ok) return { ok: false, status: res.status };

  const data = (await res.json()) as CursorResponse<AdminGetReportResponse>;
  return { ok: true, data };
}

export async function fetchAdminReportDetail(
  reportId: number,
): Promise<
  | { ok: true; data: AdminGetReportDetailResponse }
  | { ok: false; status: number }
> {
  const res = await adminAuthorizedFetch(
    buildAdminReportDetailUpstreamUrl(reportId),
  );
  if (!res.ok) return { ok: false, status: res.status };

  const data = (await res.json()) as AdminGetReportDetailResponse;
  return { ok: true, data };
}
