export type AdminMemberGender = "MALE" | "FEMALE";

export type AdminGetMemberResponse = {
  memberId: number;
  uuid: string;
  phoneNumber: string;
  profileUrl: string | null;
  nickname: string;
  gender: AdminMemberGender;
  age: number;
  comment: string | null;
  updatedAt: string;
};

export type CursorResponse<T> = {
  payload: T[];
  nextId: number | null;
  nextDateAt: string | null;
  hasNext: boolean;
};

const ADMIN_TYPES = ["NICKNAME", "UUID", "PHONE"] as const;
export type AdminMemberSearchType = (typeof ADMIN_TYPES)[number];

export function normalizeAdminMemberType(value: string | undefined): AdminMemberSearchType {
  const upper = (value ?? "NICKNAME").toUpperCase();
  return ADMIN_TYPES.includes(upper as AdminMemberSearchType)
    ? (upper as AdminMemberSearchType)
    : "NICKNAME";
}

export function normalizeAdminMemberGender(value: string | undefined): "ALL" | "MALE" | "FEMALE" {
  const upper = (value ?? "ALL").toUpperCase();
  if (upper === "MALE" || upper === "FEMALE") return upper;
  return "ALL";
}

/** SSR/CSR 로캘 차이(오전 vs AM 등)로 hydration이 깨지지 않게 서울 기준·24시·숫자만 조합 */
export function formatAdminMemberUpdatedAt(iso: string): string {
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return iso;

  const parts = new Intl.DateTimeFormat("en-CA", {
    timeZone: "Asia/Seoul",
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
  }).formatToParts(d);

  const map: Partial<Record<Intl.DateTimeFormatPartTypes, string>> = {};
  for (const p of parts) {
    if (p.type !== "literal") map[p.type] = p.value;
  }

  const y = map.year ?? "";
  const m = (map.month ?? "").padStart(2, "0");
  const day = (map.day ?? "").padStart(2, "0");
  const h = (map.hour ?? "").padStart(2, "0");
  const min = (map.minute ?? "").padStart(2, "0");
  return `${y}.${m}.${day}. ${h}:${min}`;
}

export function buildAdminMembersUpstreamUrl(params: {
  type: AdminMemberSearchType;
  keyword: string;
  gender: "ALL" | "MALE" | "FEMALE";
  cursorId?: string;
  cursorDate?: string;
  size?: number;
}): string {
  const base = process.env.ADMIN_API_BASE_URL ?? "http://127.0.0.1:8080";
  const url = new URL("/api/v1/admin/members", base.endsWith("/") ? base : `${base}/`);
  url.searchParams.set("type", params.type);
  url.searchParams.set("keyword", params.keyword);
  url.searchParams.set("gender", params.gender);
  url.searchParams.set("size", String(params.size ?? 20));
  if (params.cursorId && params.cursorDate) {
    url.searchParams.set("cursorId", params.cursorId);
    url.searchParams.set("cursorDate", params.cursorDate);
  }
  return url.toString();
}

/** 브라우저 → Next `/api/admin/members` 호출용 (프록시가 백엔드로 전달) */
export function adminMembersProxyQuery(params: {
  type: AdminMemberSearchType;
  keyword: string;
  gender: "ALL" | "MALE" | "FEMALE";
  cursorId?: string;
  cursorDate?: string;
  size?: number;
}): string {
  const q = new URLSearchParams();
  q.set("type", params.type);
  q.set("keyword", params.keyword);
  q.set("gender", params.gender);
  q.set("size", String(params.size ?? 20));
  if (params.cursorId && params.cursorDate) {
    q.set("cursorId", params.cursorId);
    q.set("cursorDate", params.cursorDate);
  }
  return q.toString();
}

export async function fetchAdminMembers(params: {
  type: AdminMemberSearchType;
  keyword: string;
  gender: "ALL" | "MALE" | "FEMALE";
  cursorId?: string;
  cursorDate?: string;
  size?: number;
}): Promise<{ ok: true; data: CursorResponse<AdminGetMemberResponse> } | { ok: false; status: number }> {
  const res = await fetch(buildAdminMembersUpstreamUrl(params), { cache: "no-store" });
  if (!res.ok) return { ok: false, status: res.status };

  const data = (await res.json()) as CursorResponse<AdminGetMemberResponse>;
  return { ok: true, data };
}
