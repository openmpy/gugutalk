"use client";

import type { ReportType } from "@/types/AdminGetReportResponse";
import { reportTypeLabel } from "@/utils/reportTypeLabel";
import { useEffect, useState } from "react";

const SUSPEND_TYPES: ReportType[] = [
  "ABUSE",
  "SPAM",
  "MINOR",
  "SEXUAL",
  "FAKE",
  "ETC",
];

type MemberSuspendModalProps = {
  open: boolean;
  onClose: () => void;
  /** 페이지 진입 시 기본으로 채울 값 (회원 상세 등) */
  initialUuid?: string;
  initialNickname?: string;
  initialPhone?: string;
  /** 신고 상세 등에서 열 때 기본 선택할 정지 타입 (신고 타입과 동일) */
  initialSuspendType?: ReportType;
};

/** 디자인·UX용: 실제 API 연동 전 조회 시 닉네임·휴대폰을 채우는 목 동작 */
function mockLookupByUuid(uuid: string): { nickname: string; phone: string } {
  const trimmed = uuid.trim();
  if (!trimmed) {
    return { nickname: "", phone: "" };
  }
  return {
    nickname: `회원_${trimmed.slice(0, 8)}`,
    phone: "010-0000-0000",
  };
}

export default function MemberSuspendModal({
  open,
  onClose,
  initialUuid = "",
  initialNickname = "",
  initialPhone = "",
  initialSuspendType,
}: MemberSuspendModalProps) {
  const [uuid, setUuid] = useState(initialUuid);
  const [nickname, setNickname] = useState(initialNickname);
  const [phone, setPhone] = useState(initialPhone);
  const [suspendDays, setSuspendDays] = useState("");
  const [suspendType, setSuspendType] = useState<ReportType | "">(
    initialSuspendType ?? "",
  );
  const [suspendReason, setSuspendReason] = useState("");
  const [lookupBusy, setLookupBusy] = useState(false);

  useEffect(() => {
    if (!open) {
      return;
    }
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        onClose();
      }
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [open, onClose]);

  useEffect(() => {
    if (!open) {
      return;
    }
    const prev = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = prev;
    };
  }, [open]);

  if (!open) {
    return null;
  }

  const onLookup = () => {
    setLookupBusy(true);
    window.setTimeout(() => {
      const { nickname: n, phone: p } = mockLookupByUuid(uuid);
      setNickname(n);
      setPhone(p);
      setLookupBusy(false);
    }, 280);
  };

  const inputClass =
    "touch-manipulation w-full min-h-11 rounded-md border border-gray-300 px-3 py-2.5 text-base text-gray-900 outline-none ring-0 focus:border-slate-500 focus:outline-none focus:ring-0 md:min-h-0 md:py-2 md:text-sm";

  const btnMd =
    "touch-manipulation inline-flex min-h-11 items-center justify-center rounded-md px-4 py-2.5 text-base font-semibold md:min-h-0 md:py-2 md:text-sm";

  return (
    <div
      className="fixed inset-0 z-50 flex flex-col justify-end md:items-center md:justify-center md:p-4"
      role="dialog"
      aria-modal="true"
      aria-labelledby="suspend-modal-title"
    >
      <button
        type="button"
        className="absolute inset-0 bg-black/45 md:backdrop-blur-[1px]"
        aria-label="닫기"
        onClick={onClose}
      />
      <div className="relative z-10 flex min-h-0 max-h-[92dvh] w-full flex-col overflow-hidden rounded-t-2xl border border-b-0 border-gray-200 bg-white md:max-h-[min(90vh,720px)] md:max-w-lg md:rounded-xl md:border-b">
        <div className="min-h-0 flex-1 overflow-y-auto overscroll-y-contain px-4 pb-2 pt-4 [scrollbar-gutter:stable] md:p-6 md:pb-4">
          <h2
            id="suspend-modal-title"
            className="text-lg font-semibold text-gray-900"
          >
            회원 정지
          </h2>

          <div className="mt-5 flex flex-col gap-4">
            <div>
              <label
                htmlFor="suspend-uuid"
                className="mb-1.5 block text-xs font-medium text-gray-600"
              >
                UUID
              </label>
              <div className="flex flex-col gap-2 md:flex-row md:items-stretch">
                <input
                  id="suspend-uuid"
                  type="text"
                  value={uuid}
                  onChange={(e) => setUuid(e.target.value)}
                  placeholder="회원 UUID 입력"
                  className={`${inputClass} min-w-0 font-mono text-base md:flex-1 md:text-[13px]`}
                  autoComplete="off"
                />
                <button
                  type="button"
                  onClick={onLookup}
                  disabled={lookupBusy || !uuid.trim()}
                  className={`${btnMd} shrink-0 bg-slate-700 text-white disabled:cursor-not-allowed disabled:bg-slate-400 md:w-auto w-full`}
                >
                  {lookupBusy ? "조회중" : "조회"}
                </button>
              </div>
            </div>

            <div>
              <label
                htmlFor="suspend-nickname"
                className="mb-1.5 block text-xs font-medium text-gray-600"
              >
                닉네임
              </label>
              <input
                id="suspend-nickname"
                type="text"
                readOnly
                value={nickname}
                placeholder="닉네임"
                className={`${inputClass} bg-slate-50 text-gray-700`}
              />
            </div>

            <div>
              <label
                htmlFor="suspend-phone"
                className="mb-1.5 block text-xs font-medium text-gray-600"
              >
                휴대폰 번호
              </label>
              <input
                id="suspend-phone"
                type="text"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                placeholder="01000000000"
                className={inputClass}
              />
            </div>

            <div>
              <label
                htmlFor="suspend-days"
                className="mb-1.5 block text-xs font-medium text-gray-600"
              >
                정지 일수
              </label>
              <input
                id="suspend-days"
                type="number"
                min={1}
                max={365}
                step={1}
                inputMode="numeric"
                value={suspendDays}
                onChange={(e) => setSuspendDays(e.target.value)}
                placeholder="최대 365일"
                className={inputClass}
              />
            </div>

            <div>
              <label
                htmlFor="suspend-type"
                className="mb-1.5 block text-xs font-medium text-gray-600"
              >
                정지 타입
              </label>
              <select
                id="suspend-type"
                value={suspendType}
                onChange={(e) =>
                  setSuspendType(e.target.value as ReportType | "")
                }
                className={`${inputClass} appearance-none bg-white`}
              >
                <option value="">선택</option>
                {SUSPEND_TYPES.map((t) => (
                  <option key={t} value={t}>
                    {reportTypeLabel(t)}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label
                htmlFor="suspend-reason"
                className="mb-1.5 block text-xs font-medium text-gray-600"
              >
                사유
              </label>
              <textarea
                id="suspend-reason"
                value={suspendReason}
                onChange={(e) => setSuspendReason(e.target.value)}
                placeholder="정지 사유 입력"
                rows={4}
                className={`${inputClass} min-h-[120px] resize-y md:min-h-[100px]`}
              />
            </div>
          </div>
        </div>

        <div className="flex shrink-0 flex-col-reverse gap-2 border-t border-gray-100 bg-white px-4 py-3 pb-[max(0.75rem,env(safe-area-inset-bottom))] pt-3 md:flex-row md:justify-end md:px-6 md:py-4 md:pb-4">
          <button
            type="button"
            onClick={onClose}
            className={`${btnMd} w-full border border-gray-300 bg-white text-gray-700 hover:bg-gray-50 md:w-auto`}
          >
            취소
          </button>
          <button
            type="button"
            className={`${btnMd} w-full bg-red-500 text-white hover:bg-red-600 md:w-auto`}
          >
            정지
          </button>
        </div>
      </div>
    </div>
  );
}
