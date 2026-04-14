export type AdminGetBanResponse = {
  banId: number;
  type: string;
  uuid: string;
  reason: string | null;
  createdAt: string;
  expiredAt: string;
};

const BAN_SEARCH_TYPES = ["UUID", "PHONE"] as const;
export type AdminBanSearchType = (typeof BAN_SEARCH_TYPES)[number];

export function normalizeAdminBanType(
  value: string | undefined,
): AdminBanSearchType {
  const upper = (value ?? "UUID").toUpperCase();
  return upper === "PHONE" ? "PHONE" : "UUID";
}

export function buildAdminBanAddUpstreamUrl(): string {
  const base = process.env.ADMIN_API_BASE_URL ?? "http://127.0.0.1:8080";
  return new URL(
    "/api/v1/admin/bans",
    base.endsWith("/") ? base : `${base}/`,
  ).toString();
}

export function buildAdminBansUpstreamUrl(params: {
  type: AdminBanSearchType;
  keyword: string;
  cursorId?: string;
  cursorDate?: string;
  size?: number;
}): string {
  const base = process.env.ADMIN_API_BASE_URL ?? "http://127.0.0.1:8080";
  const url = new URL(
    "/api/v1/admin/bans",
    base.endsWith("/") ? base : `${base}/`,
  );
  url.searchParams.set("type", params.type);
  url.searchParams.set("keyword", params.keyword);
  url.searchParams.set("size", String(params.size ?? 20));
  if (params.cursorId && params.cursorDate) {
    url.searchParams.set("cursorId", params.cursorId);
    url.searchParams.set("cursorDate", params.cursorDate);
  }
  return url.toString();
}

export function adminBansProxyQuery(params: {
  type: AdminBanSearchType;
  keyword: string;
  cursorId?: string;
  cursorDate?: string;
  size?: number;
}): string {
  const q = new URLSearchParams();
  q.set("type", params.type);
  q.set("keyword", params.keyword);
  q.set("size", String(params.size ?? 20));
  if (params.cursorId && params.cursorDate) {
    q.set("cursorId", params.cursorId);
    q.set("cursorDate", params.cursorDate);
  }
  return q.toString();
}

export type AdminBanHistoryItem = {
  type: string;
  phoneNumber: string;
  reason: string | null;
  createdAt: string;
  expiredAt: string;
};

export type AdminGetBanDetailResponse = {
  banId: number;
  type: string;
  uuid: string;
  phoneNumber: string;
  reason: string | null;
  createdAt: string;
  expiredAt: string;
  histories: AdminBanHistoryItem[];
};

export function buildAdminBanDetailUpstreamUrl(banId: number): string {
  const base = process.env.ADMIN_API_BASE_URL ?? "http://127.0.0.1:8080";
  const root = base.endsWith("/") ? base : `${base}/`;
  return new URL(`/api/v1/admin/bans/${banId}`, root).toString();
}
