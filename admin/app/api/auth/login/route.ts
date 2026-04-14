import { NextResponse } from "next/server";

import {
  ACCESS_COOKIE_MAX_AGE,
  ADMIN_ACCESS_COOKIE,
  ADMIN_REFRESH_COOKIE,
  REFRESH_COOKIE_MAX_AGE,
  adminSessionCookieOptions,
  buildAdminLoginUpstreamUrl,
} from "@/lib/adminSession";

type UpstreamLoginBody = {
  memberId: number;
  accessToken: string;
  refreshToken: string;
};

export async function POST(req: Request) {
  let body: unknown;
  try {
    body = await req.json();
  } catch {
    return NextResponse.json({ message: "Invalid JSON" }, { status: 400 });
  }

  if (
    typeof body !== "object" ||
    body === null ||
    typeof (body as { phoneNumber?: unknown }).phoneNumber !== "string" ||
    typeof (body as { password?: unknown }).password !== "string"
  ) {
    return NextResponse.json(
      { message: "phoneNumber과 password가 필요합니다." },
      { status: 400 },
    );
  }

  const { phoneNumber, password } = body as {
    phoneNumber: string;
    password: string;
  };

  const upstream = buildAdminLoginUpstreamUrl();
  const res = await fetch(upstream, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ phoneNumber, password }),
    cache: "no-store",
  });

  const text = await res.text();
  if (!res.ok) {
    return new NextResponse(text.length > 0 ? text : null, {
      status: res.status,
      headers: {
        "Content-Type": res.headers.get("content-type") || "application/json",
      },
    });
  }

  let data: UpstreamLoginBody;
  try {
    data = JSON.parse(text) as UpstreamLoginBody;
  } catch {
    return NextResponse.json(
      { message: "로그인 응답을 해석하지 못했습니다." },
      { status: 502 },
    );
  }

  if (
    typeof data.accessToken !== "string" ||
    typeof data.refreshToken !== "string"
  ) {
    return NextResponse.json(
      { message: "로그인 응답 형식이 올바르지 않습니다." },
      { status: 502 },
    );
  }

  const out = NextResponse.json({ ok: true, memberId: data.memberId });
  out.cookies.set(
    ADMIN_ACCESS_COOKIE,
    data.accessToken,
    adminSessionCookieOptions(ACCESS_COOKIE_MAX_AGE),
  );
  out.cookies.set(
    ADMIN_REFRESH_COOKIE,
    data.refreshToken,
    adminSessionCookieOptions(REFRESH_COOKIE_MAX_AGE),
  );
  return out;
}
