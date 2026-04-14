import { cookies } from "next/headers";
import { NextResponse } from "next/server";

import {
  ADMIN_ACCESS_COOKIE,
  ADMIN_REFRESH_COOKIE,
  buildAuthLogoutUpstreamUrl,
} from "@/lib/adminSession";

export async function POST() {
  const jar = await cookies();
  const access = jar.get(ADMIN_ACCESS_COOKIE)?.value;
  const refresh = jar.get(ADMIN_REFRESH_COOKIE)?.value;

  if (refresh && access) {
    try {
      await fetch(buildAuthLogoutUpstreamUrl(refresh), {
        method: "DELETE",
        headers: { Authorization: `Bearer ${access}` },
        cache: "no-store",
      });
    } catch {
      /* 서버 로그아웃 실패해도 쿠키는 제거 */
    }
  }

  const res = NextResponse.json({ ok: true });
  res.cookies.delete(ADMIN_ACCESS_COOKIE);
  res.cookies.delete(ADMIN_REFRESH_COOKIE);
  return res;
}
