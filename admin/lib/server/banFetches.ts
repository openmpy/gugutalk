import "server-only";

import type { CursorResponse } from "@/lib/members";
import { adminAuthorizedFetch } from "@/lib/adminUpstream";
import {
  buildAdminBanDetailUpstreamUrl,
  buildAdminBansUpstreamUrl,
  type AdminBanSearchType,
  type AdminGetBanDetailResponse,
  type AdminGetBanResponse,
} from "@/lib/bans";

export async function fetchAdminBans(params: {
  type: AdminBanSearchType;
  keyword: string;
  cursorId?: string;
  cursorDate?: string;
  size?: number;
}): Promise<
  | { ok: true; data: CursorResponse<AdminGetBanResponse> }
  | { ok: false; status: number }
> {
  const res = await adminAuthorizedFetch(buildAdminBansUpstreamUrl(params));
  if (!res.ok) return { ok: false, status: res.status };

  const data = (await res.json()) as CursorResponse<AdminGetBanResponse>;
  return { ok: true, data };
}

export async function fetchAdminBanDetail(
  banId: number,
): Promise<
  | { ok: true; data: AdminGetBanDetailResponse }
  | { ok: false; status: number }
> {
  const res = await adminAuthorizedFetch(buildAdminBanDetailUpstreamUrl(banId));
  if (!res.ok) return { ok: false, status: res.status };

  const data = (await res.json()) as AdminGetBanDetailResponse;
  return { ok: true, data };
}

export async function fetchAdminBanByReportedUuid(
  reportedUuid: string,
): Promise<
  | { ok: true; ban: AdminGetBanResponse | null }
  | { ok: false; status: number }
> {
  const keyword = reportedUuid.trim();
  if (!keyword) return { ok: true, ban: null };

  const result = await fetchAdminBans({
    type: "UUID",
    keyword,
    size: 1,
  });
  if (!result.ok) return { ok: false, status: result.status };

  return { ok: true, ban: result.data.payload[0] ?? null };
}
