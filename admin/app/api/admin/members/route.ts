import { NextRequest, NextResponse } from "next/server";
import {
  buildAdminMembersUpstreamUrl,
  normalizeAdminMemberGender,
  normalizeAdminMemberType,
} from "@/lib/members";
import { adminUpstreamInit } from "@/lib/adminUpstream";

export async function GET(req: NextRequest) {
  const sp = req.nextUrl.searchParams;
  const type = normalizeAdminMemberType(sp.get("type") ?? undefined);
  const keyword = sp.get("keyword") ?? "";
  const gender = normalizeAdminMemberGender(sp.get("gender") ?? undefined);
  const sizeRaw = sp.get("size");
  const size = sizeRaw ? Number(sizeRaw) : 20;
  const cursorId = sp.get("cursorId") ?? undefined;
  const cursorDate = sp.get("cursorDate") ?? undefined;

  const url = buildAdminMembersUpstreamUrl({
    type,
    keyword,
    gender,
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
