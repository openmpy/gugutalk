import { NextRequest, NextResponse } from "next/server";
import { buildAdminMemberDetailUpstreamUrl } from "@/lib/members";
import { adminUpstreamInit } from "@/lib/adminUpstream";

export async function GET(
  _req: NextRequest,
  context: { params: Promise<{ id: string }> },
) {
  const { id } = await context.params;
  const memberId = Number(id);
  if (!Number.isFinite(memberId) || memberId < 1) {
    return NextResponse.json({ message: "Invalid member id" }, { status: 400 });
  }

  const url = buildAdminMemberDetailUpstreamUrl(memberId);
  const res = await fetch(url, await adminUpstreamInit());
  const body = await res.text();
  return new NextResponse(body, {
    status: res.status,
    headers: {
      "Content-Type": res.headers.get("content-type") || "application/json",
    },
  });
}
