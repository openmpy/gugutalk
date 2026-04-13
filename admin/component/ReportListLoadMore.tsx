"use client";

import { formatAdminMemberUpdatedAt, type CursorResponse } from "@/lib/members";
import {
  adminReportsProxyQuery,
  formatAdminReportTypeLabel,
  type AdminGetReportResponse,
  type AdminReportListStatus,
  type AdminReportSearchType,
} from "@/lib/reports";
import Link from "next/link";
import { useEffect, useState } from "react";
import { IoImage } from "react-icons/io5";

type Props = {
  initial: CursorResponse<AdminGetReportResponse>;
  type: AdminReportSearchType;
  keyword: string;
  status: AdminReportListStatus;
};

export default function ReportListLoadMore({
  initial,
  type,
  keyword,
  status,
}: Props) {
  const [items, setItems] = useState(initial.payload);
  const [nextId, setNextId] = useState(initial.nextId);
  const [nextDateAt, setNextDateAt] = useState(initial.nextDateAt);
  const [hasNext, setHasNext] = useState(initial.hasNext);
  const [loading, setLoading] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);

  useEffect(() => {
    setItems(initial.payload);
    setNextId(initial.nextId);
    setNextDateAt(initial.nextDateAt);
    setHasNext(initial.hasNext);
    setLoadError(null);
    setLoading(false);
  }, [initial.payload, initial.nextId, initial.nextDateAt, initial.hasNext]);

  async function loadMore() {
    if (nextId == null || nextDateAt == null || loading) return;
    setLoading(true);
    setLoadError(null);
    try {
      const qs = adminReportsProxyQuery({
        type,
        keyword,
        status,
        cursorId: String(nextId),
        cursorDate: nextDateAt,
      });
      const res = await fetch(`/api/admin/reports?${qs}`);
      if (!res.ok) {
        setLoadError(`추가 목록을 불러오지 못했습니다. (${res.status})`);
        return;
      }
      const data = (await res.json()) as CursorResponse<AdminGetReportResponse>;
      setItems((prev) => [...prev, ...data.payload]);
      setNextId(data.nextId);
      setNextDateAt(data.nextDateAt);
      setHasNext(data.hasNext);
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <div className="flex flex-col">
        {items.map((report) => (
          <div
            key={report.reportId}
            className="flex flex-col border-b border-slate-200"
          >
            <div className="flex items-center">
              <div className="flex-1 p-2 text-xs">
                <div className="flex items-center justify-between">
                  <div className="flex min-w-0 items-center gap-1">
                    <Link
                      href={`/report/${report.reportId}`}
                      className="text-sm font-bold"
                    >
                      {formatAdminReportTypeLabel(report.type)}
                    </Link>
                    {report.hasImage ? (
                      <IoImage
                        className="h-3 w-3 shrink-0 text-gray-400"
                        aria-label="첨부 이미지 있음"
                      />
                    ) : null}
                  </div>
                  <p>{formatAdminMemberUpdatedAt(report.createdAt)}</p>
                </div>
                <p>신고자: {report.reporterNickname}</p>
                <p>피신고자: {report.reportedNickname}</p>
                <p className="line-clamp-2">사유: {report.reason ?? ""}</p>
              </div>
            </div>
          </div>
        ))}
      </div>
      {loadError ? (
        <p className="border-t border-slate-200 bg-slate-50 px-2 py-2 text-center text-sm text-red-600">
          {loadError}
        </p>
      ) : null}
      {hasNext ? (
        <div className="border-t border-slate-200 bg-slate-100 px-2 py-2 text-center">
          <button
            type="button"
            onClick={loadMore}
            disabled={loading}
            className="text-sm font-medium text-slate-700 underline disabled:opacity-50"
          >
            {loading ? "불러오는 중…" : "더 보기"}
          </button>
        </div>
      ) : null}
    </>
  );
}
