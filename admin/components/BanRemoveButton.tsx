"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

async function removeBan(banId: string) {
  const res = await fetch(`${API_BASE_URL}/api/v1/admin/bans/${banId}`, {
    method: "DELETE",
  });
  if (!res.ok) {
    throw new Error(`요청에 실패했습니다. (${res.status})`);
  }
}

export default function BanRemoveButton({ banId }: { banId: string }) {
  const router = useRouter();
  const [busy, setBusy] = useState(false);

  const onRemove = async () => {
    if (!window.confirm("이 정지를 해제하시겠습니까?")) {
      return;
    }
    setBusy(true);
    try {
      await removeBan(banId);
      router.push("/ban");
      router.refresh();
    } catch (e) {
      alert(e instanceof Error ? e.message : "오류가 발생했습니다.");
    } finally {
      setBusy(false);
    }
  };

  return (
    <button
      type="button"
      onClick={onRemove}
      disabled={busy}
      className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold disabled:opacity-60"
    >
      {busy ? "처리중" : "해제"}
    </button>
  );
}
