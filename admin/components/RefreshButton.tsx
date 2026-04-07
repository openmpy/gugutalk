"use client";

import { useRouter } from "next/navigation";

export default function RefreshButton() {
  const router = useRouter();

  return (
    <button
      type="button"
      onClick={() => router.refresh()}
      className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold"
    >
      새로고침
    </button>
  );
}
