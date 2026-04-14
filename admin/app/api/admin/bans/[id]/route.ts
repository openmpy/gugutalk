import { NextRequest, NextResponse } from "next/server";

function upstreamBanUrl(id: string) {
  const base = process.env.ADMIN_API_BASE_URL ?? "http://127.0.0.1:8080";
  const root = base.endsWith("/") ? base : `${base}/`;
  return new URL(
    `/api/v1/admin/bans/${encodeURIComponent(id)}`,
    root,
  ).toString();
}

export async function GET(
  _req: NextRequest,
  ctx: { params: Promise<{ id: string }> },
) {
  const { id } = await ctx.params;
  const res = await fetch(upstreamBanUrl(id), { cache: "no-store" });
  const body = await res.text();
  return new NextResponse(body.length > 0 ? body : null, {
    status: res.status,
    headers: {
      "Content-Type": res.headers.get("content-type") || "application/json",
    },
  });
}

export async function DELETE(
  _req: NextRequest,
  ctx: { params: Promise<{ id: string }> },
) {
  const { id } = await ctx.params;
  const res = await fetch(upstreamBanUrl(id), {
    method: "DELETE",
    cache: "no-store",
  });
  return new NextResponse(null, { status: res.status });
}
