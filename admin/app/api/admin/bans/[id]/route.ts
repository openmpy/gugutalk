import { NextRequest, NextResponse } from "next/server";

export async function DELETE(
  _req: NextRequest,
  ctx: { params: Promise<{ id: string }> },
) {
  const { id } = await ctx.params;
  const base = process.env.ADMIN_API_BASE_URL ?? "http://127.0.0.1:8080";
  const root = base.endsWith("/") ? base : `${base}/`;
  const url = new URL(
    `/api/v1/admin/bans/${encodeURIComponent(id)}`,
    root,
  );

  const res = await fetch(url.toString(), { method: "DELETE", cache: "no-store" });
  return new NextResponse(null, { status: res.status });
}
