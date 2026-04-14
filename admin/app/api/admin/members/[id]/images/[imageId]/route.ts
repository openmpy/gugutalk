import { NextRequest, NextResponse } from "next/server";
import { buildAdminMemberDeleteImageUpstreamUrl } from "@/lib/members";
import { adminUpstreamInit } from "@/lib/adminUpstream";

export async function DELETE(
  _req: NextRequest,
  context: { params: Promise<{ id: string; imageId: string }> },
) {
  const { id, imageId } = await context.params;
  const memberId = Number(id);
  const imgId = Number(imageId);
  if (!Number.isFinite(memberId) || memberId < 1 || !Number.isFinite(imgId) || imgId < 1) {
    return NextResponse.json({ message: "Invalid id" }, { status: 400 });
  }

  const url = buildAdminMemberDeleteImageUpstreamUrl(memberId, imgId);
  const res = await fetch(url, await adminUpstreamInit({ method: "DELETE" }));
  return new NextResponse(null, { status: res.status });
}
