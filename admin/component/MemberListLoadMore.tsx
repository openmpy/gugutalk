"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import {
  adminMembersProxyQuery,
  formatAdminMemberUpdatedAt,
  type AdminGetMemberResponse,
  type AdminMemberSearchType,
  type CursorResponse,
} from "@/lib/members";

function genderLabel(g: "MALE" | "FEMALE"): string {
  return g === "MALE" ? "남자" : "여자";
}

type Props = {
  initial: CursorResponse<AdminGetMemberResponse>;
  type: AdminMemberSearchType;
  keyword: string;
  gender: "ALL" | "MALE" | "FEMALE";
};

export default function MemberListLoadMore({
  initial,
  type,
  keyword,
  gender,
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
      const qs = adminMembersProxyQuery({
        type,
        keyword,
        gender,
        cursorId: String(nextId),
        cursorDate: nextDateAt,
      });
      const res = await fetch(`/api/admin/members?${qs}`);
      if (!res.ok) {
        setLoadError(`추가 목록을 불러오지 못했습니다. (${res.status})`);
        return;
      }
      const data = (await res.json()) as CursorResponse<AdminGetMemberResponse>;
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
        {items.map((member) => (
          <div key={member.memberId} className="flex flex-col border-b border-slate-200">
            <div className="flex items-center">
              {member.profileUrl ? (
                // eslint-disable-next-line @next/next/no-img-element
                <img
                  src={member.profileUrl}
                  alt=""
                  width={85}
                  height={85}
                  className="h-[85px] w-[85px] shrink-0 object-cover"
                />
              ) : (
                <div
                  className="flex h-[85px] w-[85px] shrink-0 items-center justify-center bg-slate-100 text-xs text-slate-400"
                  aria-hidden
                >
                  없음
                </div>
              )}
              <div className="flex-1 p-2 text-xs">
                <div className="flex items-center justify-between">
                  <Link href={`/member/${member.memberId}`} className="text-sm font-bold">
                    {member.nickname}
                  </Link>
                  <p>{formatAdminMemberUpdatedAt(member.updatedAt)}</p>
                </div>
                <p>{member.phoneNumber}</p>
                <p className="line-clamp-2">{member.comment ?? ""}</p>
                <p>
                  {member.age}살 · {genderLabel(member.gender)}
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
