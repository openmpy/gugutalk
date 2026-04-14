export const ADMIN_ACCESS_COOKIE = "admin_access_token";
export const ADMIN_REFRESH_COOKIE = "admin_refresh_token";

/** 백엔드 액세스 JWT와 맞추기 어려우면 환경 변수로 조정 */
export const ACCESS_COOKIE_MAX_AGE = Number(
  process.env.ADMIN_ACCESS_COOKIE_MAX_AGE ?? 3600,
);

export const REFRESH_COOKIE_MAX_AGE = 60 * 60 * 24 * 30;

export function adminSessionCookieOptions(maxAge: number) {
  return {
    httpOnly: true,
    sameSite: "lax" as const,
    path: "/",
    secure: process.env.NODE_ENV === "production",
    maxAge,
  };
}

export function buildAdminLoginUpstreamUrl(): string {
  const base = process.env.ADMIN_API_BASE_URL ?? "http://127.0.0.1:8080";
  const root = base.endsWith("/") ? base : `${base}/`;
  return new URL("/api/v1/admin/login", root).toString();
}

export function buildAuthLogoutUpstreamUrl(refreshToken: string): string {
  const base = process.env.ADMIN_API_BASE_URL ?? "http://127.0.0.1:8080";
  const root = base.endsWith("/") ? base : `${base}/`;
  const url = new URL("/api/v1/auth/logout", root);
  url.searchParams.set("refreshToken", refreshToken);
  return url.toString();
}
