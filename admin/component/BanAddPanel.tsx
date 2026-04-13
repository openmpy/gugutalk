"use client";

import { useRouter } from "next/navigation";
import { useCallback, useEffect, useId, useState } from "react";
import { IoClose } from "react-icons/io5";

const REPORT_TYPE_OPTIONS = [
  { value: "ABUSE", label: "욕설 / 비방" },
  { value: "SPAM", label: "스팸 / 광고" },
  { value: "MINOR", label: "미성년자" },
  { value: "SEXUAL", label: "음란물" },
  { value: "FAKE", label: "도용" },
  { value: "ETC", label: "기타" },
] as const;

const fieldClass =
  "w-full rounded-md border border-slate-300 bg-white px-2 py-2 text-base outline-none placeholder:text-slate-400";

const labelClass = "text-xs font-semibold text-slate-600";

type BanAddPanelProps = {
  /** 부모가 열림 상태를 제어할 때(회원 상세 등). 미지정이면 페이지용 내부 상태 사용 */
  controlledOpen?: boolean;
  onRequestClose?: () => void;
  initialUuid?: string;
  initialPhoneNumber?: string;
};

export default function BanAddPanel({
  controlledOpen,
  onRequestClose,
  initialUuid,
  initialPhoneNumber,
}: BanAddPanelProps = {}) {
  const router = useRouter();
  const formId = useId();
  const [internalOpen, setInternalOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const isControlled = controlledOpen !== undefined;
  const open = isControlled ? controlledOpen : internalOpen;
  const [type, setType] =
    useState<(typeof REPORT_TYPE_OPTIONS)[number]["value"]>("SPAM");
  const [uuid, setUuid] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [reason, setReason] = useState("");
  const [days, setDays] = useState("7");

  const close = useCallback(() => {
    if (isControlled) {
      onRequestClose?.();
    } else {
      setInternalOpen(false);
    }
  }, [isControlled, onRequestClose]);

  useEffect(() => {
    if (!open) return;
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") close();
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [open, close]);

  useEffect(() => {
    if (!open) return;
    if (initialUuid !== undefined) setUuid(initialUuid);
    if (initialPhoneNumber !== undefined) setPhoneNumber(initialPhoneNumber);
  }, [open, initialUuid, initialPhoneNumber]);

  useEffect(() => {
    if (!open) return;
    const prev = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = prev;
    };
  }, [open]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (submitting) return;
    const daysNum = Number(days);
    if (!Number.isFinite(daysNum) || daysNum < 1) {
      window.alert("정지 일수를 확인해 주세요.");
      return;
    }
    setSubmitting(true);
    try {
      const res = await fetch("/api/admin/bans", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          uuid: uuid.trim(),
          phoneNumber: phoneNumber.trim(),
          type,
          reason: reason.trim() === "" ? null : reason.trim(),
          days: daysNum,
        }),
      });
      if (!res.ok) {
        let msg = `등록 실패 (${res.status})`;
        try {
          const err = (await res.json()) as { message?: string };
          if (err.message) msg = err.message;
        } catch {
          /* ignore */
        }
        window.alert(msg);
        return;
      }
      setType("SPAM");
      setUuid("");
      setPhoneNumber("");
      setReason("");
      setDays("7");
      close();
      router.refresh();
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <>
      {!isControlled ? (
        <button
          type="button"
          onClick={() => setInternalOpen(true)}
          className="flex w-full items-center justify-center py-1 text-white bg-red-500"
        >
          추가
        </button>
      ) : null}

      {open ? (
        <div
          className="fixed inset-0 z-50 flex items-end justify-center md:items-center md:p-4"
          role="presentation"
        >
          <button
            type="button"
            className="modal-backdrop absolute inset-0 bg-slate-900/50 backdrop-blur-[1px]"
            aria-label="닫기"
            onClick={close}
          />
          <div
            role="dialog"
            aria-modal="true"
            aria-labelledby={`${formId}-title`}
            className="relative z-10 flex max-h-[min(92vh,640px)] w-full max-w-none flex-col rounded-t-md border border-slate-200 bg-white md:max-h-[85vh] md:max-w-md md:rounded-md"
          >
            <div className="flex shrink-0 items-center justify-between border-b border-slate-200 bg-slate-50 p-2 md:rounded-t-md">
              <h2 id={`${formId}-title`} className="text-base font-bold">
                정지 추가
              </h2>
              <button
                type="button"
                onClick={close}
                className="rounded-lg p-1.5 text-slate-500 hover:bg-slate-200 hover:text-slate-800"
                aria-label="닫기"
              >
                <IoClose className="h-6 w-6" />
              </button>
            </div>

            <form
              id={`${formId}-form`}
              onSubmit={handleSubmit}
              className="flex min-h-0 flex-1 flex-col"
            >
              <div className="min-h-0 flex flex-1 flex-col gap-2 overflow-y-auto px-2 py-3">
                <div className="flex flex-col gap-1">
                  <label htmlFor={`${formId}-type`} className={labelClass}>
                    유형
                  </label>
                  <select
                    id={`${formId}-type`}
                    value={type}
                    onChange={(e) =>
                      setType(
                        e.target
                          .value as (typeof REPORT_TYPE_OPTIONS)[number]["value"],
                      )
                    }
                    className={`${fieldClass}`}
                    required
                  >
                    {REPORT_TYPE_OPTIONS.map((opt) => (
                      <option key={opt.value} value={opt.value}>
                        {opt.label}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="flex flex-col gap-1">
                  <label htmlFor={`${formId}-uuid`} className={labelClass}>
                    UUID
                  </label>
                  <input
                    id={`${formId}-uuid`}
                    value={uuid}
                    onChange={(e) => setUuid(e.target.value)}
                    className={`${fieldClass}`}
                    placeholder="UUID 입력"
                    autoComplete="off"
                    required
                  />
                </div>

                <div className="flex flex-col gap-1">
                  <label
                    htmlFor={`${formId}-phone`}
                    className={`${labelClass} text-base`}
                  >
                    휴대폰 번호
                  </label>
                  <input
                    id={`${formId}-phone`}
                    type="tel"
                    inputMode="numeric"
                    value={phoneNumber}
                    onChange={(e) => setPhoneNumber(e.target.value)}
                    className={`${fieldClass}`}
                    placeholder="휴대폰 번호 입력"
                    autoComplete="tel"
                    required
                  />
                </div>

                <div className="flex flex-col gap-1">
                  <label htmlFor={`${formId}-reason`} className={labelClass}>
                    사유
                  </label>
                  <textarea
                    id={`${formId}-reason`}
                    value={reason}
                    onChange={(e) => setReason(e.target.value)}
                    rows={3}
                    className={`${fieldClass} resize-y min-h-20`}
                    placeholder="정지 사유 입력"
                  />
                </div>

                <div className="flex flex-col gap-1">
                  <label htmlFor={`${formId}-days`} className={labelClass}>
                    정지 일수
                  </label>
                  <input
                    id={`${formId}-days`}
                    type="number"
                    min={1}
                    max={3650}
                    step={1}
                    value={days}
                    onChange={(e) => setDays(e.target.value)}
                    className={`${fieldClass}`}
                    required
                  />
                </div>
              </div>

              <div className="flex shrink-0 gap-2 border-t border-slate-200 bg-slate-50 px-2 py-3 md:rounded-b-md">
                <button
                  type="button"
                  disabled={submitting}
                  onClick={close}
                  className="flex-1 rounded-lg bg-slate-300 px-3 py-2.5 text-sm font-semibold disabled:opacity-50"
                >
                  취소
                </button>
                <button
                  type="submit"
                  disabled={submitting}
                  className="flex-1 rounded-lg bg-red-500 px-3 py-2.5 text-sm font-semibold text-white disabled:opacity-50"
                >
                  {submitting ? "등록 중…" : "등록"}
                </button>
              </div>
            </form>
          </div>
        </div>
      ) : null}
    </>
  );
}
