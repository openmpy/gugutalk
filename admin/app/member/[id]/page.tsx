import Image from "next/image";
import { notFound } from "next/navigation";
import { HiMiniXMark, HiMinus, HiPlus } from "react-icons/hi2";
import ListButton from "@/component/ListButton";
import MemberAdminSanitizeBar from "@/component/MemberAdminSanitizeBar";
import {
  adminMemberGenderLabel,
  fetchAdminMemberDetail,
  formatAdminMemberDateTime,
  type AdminPointTransactionResponse,
} from "@/lib/members";

function PointRow({ tx }: { tx: AdminPointTransactionResponse }) {
  const isEarn = tx.type === "EARN";
  const signed = isEarn ? tx.amount : -tx.amount;
  const label = isEarn ? "획득" : "사용";

  return (
    <li className="flex items-center gap-3 px-3 py-3">
      <div
        className={
          isEarn
            ? "flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-emerald-100 text-emerald-700"
            : "flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-rose-100 text-rose-700"
        }
        aria-hidden
      >
        {isEarn ? (
          <HiPlus className="h-5 w-5" strokeWidth={2.25} />
        ) : (
          <HiMinus className="h-5 w-5" strokeWidth={2.25} />
        )}
      </div>
      <div className="min-w-0 flex-1">
        <p className="text-sm font-semibold text-slate-800">
          {tx.description?.trim() || "포인트"}
        </p>
        <p className="mt-0.5 text-xs text-slate-500 tabular-nums">
          <span className={isEarn ? "text-emerald-600 font-medium" : "text-rose-600 font-medium"}>
            {label}
          </span>
          <span className="mx-1.5 text-slate-300">·</span>
          {formatAdminMemberDateTime(tx.createdAt)}
        </p>
      </div>
      <p
        className={
          isEarn
            ? "shrink-0 text-sm font-bold tabular-nums text-emerald-600"
            : "shrink-0 text-sm font-bold tabular-nums text-rose-600"
        }
      >
        {signed > 0 ? `+${signed}` : signed}
      </p>
    </li>
  );
}

export default async function MemberDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const memberId = Number(id);
  if (!Number.isFinite(memberId) || memberId < 1) {
    notFound();
  }

  const result = await fetchAdminMemberDetail(memberId);
  if (!result.ok) {
    notFound();
  }

  const m = result.data;

  return (
    <div>
      <div className="flex items-center justify-between gap-1 text-xs px-2 bg-slate-400 py-1">
        <ListButton href="/member" />
        <MemberAdminSanitizeBar memberId={m.memberId} />
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">정보</h2>
        </div>
        <div className="flex flex-col gap-1">
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">ID</p>
            <p className="text-sm font-mono">{m.memberId}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">UUID</p>
            <p className="text-sm break-all">{m.uuid}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">휴대폰</p>
            <p className="text-sm">{m.phoneNumber}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">닉네임</p>
            <p className="text-sm">{m.nickname}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">성별</p>
            <p className="text-sm">{adminMemberGenderLabel(m.gender)}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">출생연도</p>
            <p className="text-sm">{m.birthYear}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">코멘트</p>
            <p className="text-sm whitespace-pre-wrap">{m.comment ?? "—"}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">자기소개</p>
            <p className="text-sm whitespace-pre-wrap">{m.bio ?? "—"}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">생성일</p>
            <p className="text-sm">{formatAdminMemberDateTime(m.createdAt)}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">수정일</p>
            <p className="text-sm">{formatAdminMemberDateTime(m.updatedAt)}</p>
          </div>
        </div>
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">이미지</h2>
        </div>
        <div className="p-2 flex flex-col gap-2">
          <div className="flex flex-col gap-1">
            <h3 className="font-bold text-sm">공개 사진</h3>
            {m.publicImages.length === 0 ? (
              <p className="text-sm text-slate-500">등록된 공개 사진이 없습니다.</p>
            ) : (
              <div className="flex flex-wrap gap-2">
                {m.publicImages.map((img) => (
                  <div key={img.imageId} className="relative">
                    <Image
                      src={img.url}
                      alt=""
                      width={100}
                      height={100}
                      className="w-[100px] h-[100px] shrink-0 rounded-md border border-slate-300 object-cover"
                      unoptimized
                    />
                    <button
                      type="button"
                      className="absolute top-1 right-1 bg-red-500 text-white rounded-full"
                      aria-label="이미지 삭제"
                    >
                      <HiMiniXMark className="w-4 h-4" />
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
          <div className="flex flex-col gap-1">
            <h3 className="font-bold text-sm">비밀 사진</h3>
            {m.privateImages.length === 0 ? (
              <p className="text-sm text-slate-500">등록된 비밀 사진이 없습니다.</p>
            ) : (
              <div className="flex flex-wrap gap-2">
                {m.privateImages.map((img) => (
                  <div key={img.imageId} className="relative">
                    <Image
                      src={img.url}
                      alt=""
                      width={100}
                      height={100}
                      className="w-[100px] h-[100px] shrink-0 rounded-md border border-slate-300 object-cover"
                      unoptimized
                    />
                    <button
                      type="button"
                      className="absolute top-1 right-1 bg-red-500 text-white rounded-full"
                      aria-label="이미지 삭제"
                    >
                      <HiMiniXMark className="w-4 h-4" />
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">포인트 내역</h2>
        </div>
        <div className="flex items-center justify-end bg-slate-200 py-1 px-2">
          <p className="text-sm">포인트: {m.point}P</p>
        </div>
        <div className="p-2">
          {m.pointTransactions.length === 0 ? (
            <p className="text-sm text-slate-500 px-1">포인트 내역이 없습니다.</p>
          ) : (
            <ul className="overflow-hidden rounded-md border border-slate-200 bg-white divide-y divide-slate-100">
              {m.pointTransactions.map((tx) => (
                <PointRow key={tx.pointTransactionId} tx={tx} />
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}
