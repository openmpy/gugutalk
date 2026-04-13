"use client";

import { useRouter } from "next/navigation";
import { useCallback, useState } from "react";
import { HiMiniXMark } from "react-icons/hi2";

type Props = { memberId: number; imageId: number };

export default function MemberAdminImageDeleteButton({
  memberId,
  imageId,
}: Props) {
  const router = useRouter();
  const [busy, setBusy] = useState(false);

  const onClick = useCallback(async () => {
    if (busy) return;
    if (!window.confirm("이미지를 삭제할까요?")) return;
    setBusy(true);
    try {
      const res = await fetch(
        `/api/admin/members/${memberId}/images/${imageId}`,
        {
          method: "DELETE",
        },
      );
      if (!res.ok) {
        window.alert(`삭제 실패 (${res.status})`);
        return;
      }
      router.refresh();
    } finally {
      setBusy(false);
    }
  }, [busy, memberId, imageId, router]);

  return (
    <button
      type="button"
      disabled={busy}
      onClick={onClick}
      className="absolute top-1 right-1 rounded-full bg-red-500 p-0.5 text-white disabled:opacity-50"
      aria-label="이미지 삭제"
    >
      <HiMiniXMark className="h-4 w-4" />
    </button>
  );
}
