import { NextRequest, NextResponse } from "next/server";
import {
  buildAdminBanAddUpstreamUrl,
  buildAdminBansUpstreamUrl,
  normalizeAdminBanType,
} from "@/lib/bans";
import { adminUpstreamInit } from "@/lib/adminUpstream";

export async function POST(req: NextRequest) {
  const body = await req.text();
  const res = await fetch(
    buildAdminBanAddUpstreamUrl(),
    await adminUpstreamInit({
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body,
    }),
  );
  const text = await res.text();
  return new NextResponse(text.length > 0 ? text : null, {
    status: res.status,
    headers: {
      "Content-Type": res.headers.get("content-type") || "application/json",
    },
  });
}

export async function GET(req: NextRequest) {
  const sp = req.nextUrl.searchParams;
  const type = normalizeAdminBanType(sp.get("type") ?? undefined);
  const keyword = sp.get("keyword") ?? "";
  const sizeRaw = sp.get("size");
  const size = sizeRaw ? Number(sizeRaw) : 20;
  const cursorId = sp.get("cursorId") ?? undefined;
  const cursorDate = sp.get("cursorDate") ?? undefined;

  const url = buildAdminBansUpstreamUrl({
    type,
    keyword,
    size: Number.isFinite(size) && size > 0 ? size : 20,
    ...(cursorId && cursorDate ? { cursorId, cursorDate } : {}),
  });

  const res = await fetch(url, await adminUpstreamInit());
  const body = await res.text();
  return new NextResponse(body, {
    status: res.status,
    headers: {
      "Content-Type": res.headers.get("content-type") || "application/json",
    },
  });
}
