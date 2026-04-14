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

export type AdminMemberImageResponse = {
  imageId: number;
  index: number;
  url: string;
};

export type AdminPointTransactionType = "EARN" | "USE";

export type AdminPointTransactionResponse = {
  pointTransactionId: number;
  type: AdminPointTransactionType;
  amount: number;
  description: string | null;
  createdAt: string;
};

export type AdminGetMemberDetailResponse = {
  memberId: number;
  uuid: string;
  phoneNumber: string;
  nickname: string;
  gender: AdminMemberGender;
  birthYear: number;
  comment: string | null;
  bio: string | null;
  createdAt: string;
  updatedAt: string;
  publicImages: AdminMemberImageResponse[];
  privateImages: AdminMemberImageResponse[];
  point: number;
  pointTransactions: AdminPointTransactionResponse[];
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

/** 생성·수정일 등 초 단위까지 (서울, 24시) */
export function formatAdminMemberDateTime(iso: string): string {
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return iso;

  const parts = new Intl.DateTimeFormat("en-CA", {
    timeZone: "Asia/Seoul",
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false,
  }).formatToParts(d);

  const map: Partial<Record<Intl.DateTimeFormatPartTypes, string>> = {};
  for (const p of parts) {
    if (p.type !== "literal") map[p.type] = p.value;
  }

  const y = map.year ?? "";
  const mo = (map.month ?? "").padStart(2, "0");
  const day = (map.day ?? "").padStart(2, "0");
  const h = (map.hour ?? "").padStart(2, "0");
  const min = (map.minute ?? "").padStart(2, "0");
  const sec = (map.second ?? "").padStart(2, "0");
  return `${y}.${mo}.${day}. ${h}:${min}:${sec}`;
}

export function adminMemberGenderLabel(gender: AdminMemberGender): string {
  if (gender === "MALE") return "남자";
  if (gender === "FEMALE") return "여자";
  return gender;
}

export function buildAdminMemberDetailUpstreamUrl(memberId: number): string {
  const base = process.env.ADMIN_API_BASE_URL ?? "http://127.0.0.1:8080";
  const root = base.endsWith("/") ? base : `${base}/`;
  return new URL(`/api/v1/admin/members/${memberId}`, root).toString();
}

export type AdminMemberSanitizeField = "nickname" | "comment" | "bio";

export function buildAdminMemberSanitizeUpstreamUrl(
  memberId: number,
  field: AdminMemberSanitizeField,
): string {
  const base = process.env.ADMIN_API_BASE_URL ?? "http://127.0.0.1:8080";
  const root = base.endsWith("/") ? base : `${base}/`;
  return new URL(`/api/v1/admin/members/${memberId}/${field}`, root).toString();
}

export function buildAdminMemberDeleteImageUpstreamUrl(
  memberId: number,
  imageId: number,
): string {
  const base = process.env.ADMIN_API_BASE_URL ?? "http://127.0.0.1:8080";
  const root = base.endsWith("/") ? base : `${base}/`;
  return new URL(`/api/v1/admin/members/${memberId}/images/${imageId}`, root).toString();
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
