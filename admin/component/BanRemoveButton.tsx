"use client";

import { useRouter } from "next/navigation";
import { useCallback, useState } from "react";

type Props = {
  banId: number;
};

export default function BanRemoveButton({ banId }: Props) {
  const router = useRouter();
  const [busy, setBusy] = useState(false);

  const run = useCallback(async () => {
    if (busy) return;
    if (!window.confirm("정지를 해제할까요?")) return;
    setBusy(true);
    try {
      const res = await fetch(`/api/admin/bans/${banId}`, {
        method: "DELETE",
      });
      if (!res.ok) {
        window.alert(`요청 실패 (${res.status})`);
        return;
      }
      router.push("/ban");
      router.refresh();
    } finally {
      setBusy(false);
    }
  }, [banId, busy, router]);

  return (
    <button
      type="button"
      onClick={run}
      disabled={busy}
      className="rounded-md bg-blue-500 px-2 py-1 text-white disabled:opacity-50"
    >
      {busy ? "처리 중…" : "해제"}
    </button>
  );
}
