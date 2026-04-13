"use client";

import { useRouter } from "next/navigation";
import { useCallback, useState } from "react";
import BanAddPanel from "@/component/BanAddPanel";

type Props = { memberId: number; uuid: string; phoneNumber: string };

export default function MemberAdminSanitizeBar({
  memberId,
  uuid,
  phoneNumber,
}: Props) {
  const router = useRouter();
  const [busy, setBusy] = useState<"nickname" | "comment" | "bio" | null>(null);
  const [banOpen, setBanOpen] = useState(false);

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
      <BanAddPanel
        controlledOpen={banOpen}
        onRequestClose={() => setBanOpen(false)}
        initialUuid={uuid}
        initialPhoneNumber={phoneNumber}
      />
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
      <button
        type="button"
        onClick={() => setBanOpen(true)}
        className="rounded-md bg-red-500 px-2 py-1 text-white"
      >
        정지
      </button>
    </div>
  );
}
