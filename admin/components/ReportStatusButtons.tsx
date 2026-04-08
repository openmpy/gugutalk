"use client";

import type { ReportStatus } from "@/types/AdminGetReportDetailResponse";
import { useRouter } from "next/navigation";
import { useState } from "react";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

async function updateReportStatus(
  reportId: string,
  status: Exclude<ReportStatus, "PENDING">,
) {
  const sp = new URLSearchParams({ status });
  const res = await fetch(
    `${API_BASE_URL}/api/v1/admin/reports/${reportId}?${sp.toString()}`,
    { method: "PUT" },
  );
  if (!res.ok) {
    throw new Error(`요청에 실패했습니다. (${res.status})`);
  }
}

export default function ReportStatusButtons({
  reportId,
  currentStatus,
}: {
  reportId: string;
  currentStatus: ReportStatus;
}) {
  const router = useRouter();
  const [pending, setPending] = useState<"REJECT" | "RESOLVE" | null>(null);

  const onReject = async () => {
    if (!window.confirm("이 신고를 반려 처리하시겠습니까?")) {
      return;
    }
    setPending("REJECT");
    try {
      await updateReportStatus(reportId, "REJECT");
      router.refresh();
    } catch (e) {
      alert(e instanceof Error ? e.message : "오류가 발생했습니다.");
    } finally {
      setPending(null);
    }
  };

  const onResolve = async () => {
    if (!window.confirm("이 신고를 처리로 바꾸시겠습니까?")) {
      return;
    }
    setPending("RESOLVE");
    try {
      await updateReportStatus(reportId, "RESOLVE");
      router.refresh();
    } catch (e) {
      alert(e instanceof Error ? e.message : "오류가 발생했습니다.");
    } finally {
      setPending(null);
    }
  };

  const busy = pending !== null;

  return (
    <>
      <button
        type="button"
        onClick={onReject}
        disabled={busy || currentStatus === "REJECT"}
        className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold disabled:opacity-50"
      >
        {pending === "REJECT" ? "처리중" : "반려"}
      </button>
      <button
        type="button"
        onClick={onResolve}
        disabled={busy || currentStatus === "RESOLVE"}
        className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold disabled:opacity-50"
      >
        {pending === "RESOLVE" ? "처리중" : "처리"}
      </button>
    </>
  );
}
