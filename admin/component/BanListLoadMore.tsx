"use client";

import {
  adminBansProxyQuery,
  type AdminBanSearchType,
  type AdminGetBanResponse,
} from "@/lib/bans";
import { formatAdminReportTypeLabel } from "@/lib/reports";
import { formatAdminMemberUpdatedAt, type CursorResponse } from "@/lib/members";
import Link from "next/link";
import { useEffect, useState } from "react";

type Props = {
  initial: CursorResponse<AdminGetBanResponse>;
  type: AdminBanSearchType;
  keyword: string;
};

export default function BanListLoadMore({
  initial,
  type,
  keyword,
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
      const qs = adminBansProxyQuery({
        type,
        keyword,
        cursorId: String(nextId),
        cursorDate: nextDateAt,
      });
      const res = await fetch(`/api/admin/bans?${qs}`);
      if (!res.ok) {
        setLoadError(`추가 목록을 불러오지 못했습니다. (${res.status})`);
        return;
      }
      const data = (await res.json()) as CursorResponse<AdminGetBanResponse>;
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
        {items.map((ban) => (
          <div
            key={ban.banId}
            className="flex flex-col border-b border-slate-200"
          >
            <div className="flex items-center">
              <div className="flex-1 p-2 text-xs">
                <div className="flex items-center justify-between">
                  <Link
                    href={`/ban/${ban.banId}`}
                    className="text-sm font-bold"
                  >
                    {formatAdminReportTypeLabel(ban.type)}
                  </Link>
                  <p>{formatAdminMemberUpdatedAt(ban.createdAt)}</p>
                </div>
                <p className="break-all">UUID: {ban.uuid}</p>
                <p>만료: {formatAdminMemberUpdatedAt(ban.expiredAt)}</p>
                <p className="line-clamp-2">
                  사유: {ban.reason ?? ""}
                </p>
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
