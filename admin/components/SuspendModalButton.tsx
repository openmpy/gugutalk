"use client";

import MemberSuspendModal from "@/components/MemberSuspendModal";
import type { ReportType } from "@/types/AdminGetReportResponse";
import { useState } from "react";

export default function SuspendModalButton({
  uuid,
  nickname,
  phoneNumber,
  initialSuspendType,
}: {
  uuid: string;
  nickname: string;
  phoneNumber: string;
  /** 신고 상세 등: 해당 신고 타입을 정지 타입 기본값으로 */
  initialSuspendType?: ReportType;
}) {
  const [open, setOpen] = useState(false);
  const [modalKey, setModalKey] = useState(0);

  return (
    <>
      <button
        type="button"
        onClick={() => {
          setModalKey((k) => k + 1);
          setOpen(true);
        }}
        className="px-4 py-2 rounded-md bg-red-500 text-sm font-semibold text-white hover:bg-red-600"
      >
        정지
      </button>
      <MemberSuspendModal
        key={modalKey}
        open={open}
        onClose={() => setOpen(false)}
        initialUuid={uuid}
        initialNickname={nickname}
        initialPhone={phoneNumber}
        initialSuspendType={initialSuspendType}
      />
    </>
  );
}
