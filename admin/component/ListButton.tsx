"use client";

import { useRouter } from "next/navigation";

type ListButtonProps = {
  href: string;
};

export default function ListButton({ href }: ListButtonProps) {
  const router = useRouter();

  return (
    <div>
      <button
        type="button"
        className="bg-slate-600 text-white px-2 py-1 rounded-md"
        onClick={() => router.push(href)}
      >
        목록
      </button>
    </div>
  );
}
