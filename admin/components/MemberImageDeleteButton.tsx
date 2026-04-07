"use client";

import { X } from "lucide-react";
import { useRouter } from "next/navigation";
import { useState } from "react";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

async function deleteMemberImage(memberId: string, imageId: number) {
  const res = await fetch(
    `${API_BASE_URL}/api/v1/admin/members/${memberId}/images/${imageId}`,
    { method: "DELETE" },
  );
  if (!res.ok) {
    throw new Error(`이미지 삭제에 실패했습니다. (${res.status})`);
  }
}

export default function MemberImageDeleteButton({
  memberId,
  imageId,
}: {
  memberId: string;
  imageId: number;
}) {
  const router = useRouter();
  const [pending, setPending] = useState(false);

  const onClick = async (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (!window.confirm("정말 이미지를 삭제하시겠습니까?")) {
      return;
    }
    setPending(true);
    try {
      await deleteMemberImage(memberId, imageId);
      router.refresh();
    } catch (err) {
      alert(err instanceof Error ? err.message : "오류가 발생했습니다.");
    } finally {
      setPending(false);
    }
  };

  return (
    <button
      type="button"
      onClick={onClick}
      disabled={pending}
      aria-label="이미지 삭제"
      className="absolute top-1 right-1 z-10 w-4 h-4 bg-red-500 text-white rounded-full flex items-center justify-center text-xs shadow disabled:opacity-50"
    >
      <X className="w-3 h-3" />
    </button>
  );
}
