import { NextRequest, NextResponse } from "next/server";
import {
  buildAdminReportDetailUpstreamUrl,
  buildAdminReportUpdateUpstreamUrl,
  normalizeAdminReportStatus,
} from "@/lib/reports";

export async function GET(
  _req: NextRequest,
  context: { params: Promise<{ id: string }> },
) {
  const { id } = await context.params;
  const reportId = Number(id);
  if (!Number.isFinite(reportId) || reportId < 1) {
    return NextResponse.json({ message: "Invalid report id" }, { status: 400 });
  }

  const url = buildAdminReportDetailUpstreamUrl(reportId);
  const res = await fetch(url, { cache: "no-store" });
  const body = await res.text();
  return new NextResponse(body, {
    status: res.status,
    headers: {
      "Content-Type": res.headers.get("content-type") || "application/json",
    },
  });
}

export async function PUT(
  req: NextRequest,
  context: { params: Promise<{ id: string }> },
) {
  const { id } = await context.params;
  const reportId = Number(id);
  if (!Number.isFinite(reportId) || reportId < 1) {
    return NextResponse.json({ message: "Invalid report id" }, { status: 400 });
  }

  const raw = req.nextUrl.searchParams.get("status");
  if (!raw?.trim()) {
    return NextResponse.json({ message: "status is required" }, { status: 400 });
  }

  const status = normalizeAdminReportStatus(raw);
  const url = buildAdminReportUpdateUpstreamUrl(reportId, status);
  const res = await fetch(url, { method: "PUT", cache: "no-store" });
  return new NextResponse(null, { status: res.status });
}
