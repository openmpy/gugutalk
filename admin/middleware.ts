import type { NextRequest } from "next/server";
import { NextResponse } from "next/server";

import { ADMIN_ACCESS_COOKIE } from "@/lib/adminSession";

const LOGIN_PATH = "/login";

function hasAccessCookie(req: NextRequest): boolean {
  return Boolean(req.cookies.get(ADMIN_ACCESS_COOKIE)?.value);
}

export function middleware(req: NextRequest) {
  const { pathname } = req.nextUrl;

  if (pathname === LOGIN_PATH || pathname.startsWith(`${LOGIN_PATH}/`)) {
    if (hasAccessCookie(req)) {
      return NextResponse.redirect(new URL("/member", req.url));
    }
    return NextResponse.next();
  }

  if (
    pathname.startsWith("/api/auth/login") ||
    pathname === "/api/auth/logout"
  ) {
    return NextResponse.next();
  }

  if (pathname.startsWith("/api/admin")) {
    if (!hasAccessCookie(req)) {
      return NextResponse.json({ message: "Unauthorized" }, { status: 401 });
    }
    return NextResponse.next();
  }

  if (!hasAccessCookie(req)) {
    const url = req.nextUrl.clone();
    url.pathname = LOGIN_PATH;
    url.search = "";
    url.searchParams.set("from", pathname);
    return NextResponse.redirect(url);
  }

  return NextResponse.next();
}

export const config = {
  matcher: [
    "/((?!_next/static|_next/image|favicon.ico|.*\\.(?:svg|png|jpg|jpeg|gif|webp)$).*)",
  ],
};
