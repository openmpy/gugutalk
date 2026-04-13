"use client";

import { useRouter } from "next/navigation";
import { IoRefresh } from "react-icons/io5";

export default function RefreshButton() {
  const router = useRouter();

  return (
    <button
      aria-label="데이터 새로고침"
      className="absolute bottom-18 right-2 z-10 rounded-3xl bg-slate-500/80 p-2 text-white"
      onClick={() => {
        router.refresh();
      }}
    >
      <IoRefresh className="w-6 h-6" />
    </button>
  );
}
