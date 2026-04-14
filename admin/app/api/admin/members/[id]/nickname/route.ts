import { NextRequest, NextResponse } from "next/server";
import { buildAdminMemberSanitizeUpstreamUrl } from "@/lib/members";
import { adminUpstreamInit } from "@/lib/adminUpstream";

export async function PUT(
  _req: NextRequest,
  context: { params: Promise<{ id: string }> },
) {
  const { id } = await context.params;
  const memberId = Number(id);
  if (!Number.isFinite(memberId) || memberId < 1) {
    return NextResponse.json({ message: "Invalid member id" }, { status: 400 });
  }

  const url = buildAdminMemberSanitizeUpstreamUrl(memberId, "nickname");
  const res = await fetch(url, await adminUpstreamInit({ method: "PUT" }));
  return new NextResponse(null, { status: res.status });
}
