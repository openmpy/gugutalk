"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

type UpdateKind = "nickname" | "comment" | "bio";

const CONFIRM_MESSAGE: Record<UpdateKind, string> = {
  nickname: "정말 닉네임을 변경하시겠습니까?",
  comment: "정말 코멘트를 변경하시겠습니까?",
  bio: "정말 자기소개를 변경하시겠습니까?",
};

async function updateMember(memberId: string, kind: UpdateKind) {
  const res = await fetch(
    `${API_BASE_URL}/api/v1/admin/members/${memberId}/${kind}`,
    { method: "PUT" },
  );
  if (!res.ok) {
    throw new Error(`요청에 실패했습니다. (${res.status})`);
  }
}

export default function MemberUpdateButtons({
  memberId,
}: {
  memberId: string;
}) {
  const router = useRouter();
  const [pending, setPending] = useState<UpdateKind | null>(null);

  const onClick = async (kind: UpdateKind) => {
    if (!window.confirm(CONFIRM_MESSAGE[kind])) {
      return;
    }
    setPending(kind);
    try {
      await updateMember(memberId, kind);
      router.refresh();
    } catch (e) {
      alert(e instanceof Error ? e.message : "오류가 발생했습니다.");
    } finally {
      setPending(null);
    }
  };

  return (
    <>
      <button
        type="button"
        onClick={() => onClick("nickname")}
        disabled={pending !== null}
        className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold disabled:opacity-50"
      >
        {pending === "nickname" ? "처리중" : "닉네임"}
      </button>
      <button
        type="button"
        onClick={() => onClick("comment")}
        disabled={pending !== null}
        className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold disabled:opacity-50"
      >
        {pending === "comment" ? "처리중" : "코멘트"}
      </button>
      <button
        type="button"
        onClick={() => onClick("bio")}
        disabled={pending !== null}
        className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold disabled:opacity-50"
      >
        {pending === "bio" ? "처리중" : "자기소개"}
      </button>
    </>
  );
}
