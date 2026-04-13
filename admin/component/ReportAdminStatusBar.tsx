"use client";

import {
  formatAdminReportStatusLabel,
  type AdminReportListStatus,
} from "@/lib/reports";
import { useRouter } from "next/navigation";
import { useCallback, useState } from "react";

type Props = {
  reportId: number;
  status: AdminReportListStatus;
};

export default function ReportAdminStatusBar({ reportId, status }: Props) {
  const router = useRouter();
  const [busy, setBusy] = useState<AdminReportListStatus | null>(null);

  const run = useCallback(
    async (next: AdminReportListStatus) => {
      if (busy !== null) return;
      if (next === status) return;
      const label = formatAdminReportStatusLabel(next);
      if (!window.confirm(`신고 상태를 ${label}(으)로 변경할까요?`)) return;
      setBusy(next);
      try {
        const res = await fetch(
          `/api/admin/reports/${reportId}?status=${encodeURIComponent(next)}`,
          { method: "PUT" },
        );
        if (!res.ok) {
          window.alert(`요청 실패 (${res.status})`);
          return;
        }
        router.refresh();
      } finally {
        setBusy(null);
      }
    },
    [busy, reportId, router, status],
  );

  return (
    <div className="flex flex-wrap gap-1">
      <button
        type="button"
        disabled={busy !== null}
        onClick={() => run("PENDING")}
        className="rounded-md bg-blue-500 px-2 py-1 text-white disabled:opacity-50"
      >
        {busy === "PENDING" ? "처리 중…" : "대기"}
      </button>
      <button
        type="button"
        disabled={busy !== null}
        onClick={() => run("REJECT")}
        className="rounded-md bg-orange-500 px-2 py-1 text-white disabled:opacity-50"
      >
        {busy === "REJECT" ? "처리 중…" : "반려"}
      </button>
      <button
        type="button"
        disabled={busy !== null}
        onClick={() => run("RESOLVE")}
        className="rounded-md bg-purple-500 px-2 py-1 text-white disabled:opacity-50"
      >
        {busy === "RESOLVE" ? "처리 중…" : "처분"}
      </button>
    </div>
  );
}
