"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useCallback, useState } from "react";

type Props = { memberId: number };

export default function MemberAdminSanitizeBar({ memberId }: Props) {
  const router = useRouter();
  const [busy, setBusy] = useState<"nickname" | "comment" | "bio" | null>(null);

  const run = useCallback(
    async (field: "nickname" | "comment" | "bio") => {
      if (busy) return;
      if (!window.confirm("정말 변경하시겠습니까?")) return;
      setBusy(field);
      try {
        const res = await fetch(`/api/admin/members/${memberId}/${field}`, {
          method: "PUT",
        });
        if (!res.ok) {
          window.alert(`요청 실패 (${res.status})`);
          return;
        }
        router.refresh();
      } finally {
        setBusy(null);
      }
    },
    [busy, memberId, router],
  );

  return (
    <div className="flex flex-wrap gap-1">
      <button
        type="button"
        disabled={busy !== null}
        onClick={() => run("nickname")}
        className="rounded-md bg-blue-500 px-2 py-1 text-white disabled:opacity-50"
      >
        {busy === "nickname" ? "처리 중…" : "닉네임 변경"}
      </button>
      <button
        type="button"
        disabled={busy !== null}
        onClick={() => run("comment")}
        className="rounded-md bg-purple-500 px-2 py-1 text-white disabled:opacity-50"
      >
        {busy === "comment" ? "처리 중…" : "코멘트 변경"}
      </button>
      <button
        type="button"
        disabled={busy !== null}
        onClick={() => run("bio")}
        className="rounded-md bg-orange-500 px-2 py-1 text-white disabled:opacity-50"
      >
        {busy === "bio" ? "처리 중…" : "자기소개 변경"}
      </button>
      <Link href="/ban" className="rounded-md bg-red-500 px-2 py-1 text-white">
        정지
      </Link>
    </div>
  );
}
