import BanRemoveButton from "@/component/BanRemoveButton";
import ListButton from "@/component/ListButton";
import { fetchAdminBanDetail } from "@/lib/bans";
import { formatAdminMemberDateTime } from "@/lib/members";
import { formatAdminReportTypeLabel } from "@/lib/reports";
import { notFound } from "next/navigation";

export default async function BanDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const banId = Number(id);
  if (!Number.isFinite(banId) || banId < 1) {
    notFound();
  }

  const result = await fetchAdminBanDetail(banId);
  if (!result.ok) {
    notFound();
  }

  const b = result.data;

  return (
    <div>
      <div className="flex items-center justify-between gap-1 text-xs px-2 bg-slate-400 py-1">
        <ListButton href="/ban" />
        <div className="flex gap-1">
          <BanRemoveButton banId={banId} />
        </div>
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">정보</h2>
        </div>
        <div className="flex flex-col gap-1">
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">ID</p>
            <p className="text-sm font-mono">{b.banId}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">유형</p>
            <p className="text-sm">{formatAdminReportTypeLabel(b.type)}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">UUID</p>
            <p className="text-sm break-all">{b.uuid}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">휴대폰</p>
            <p className="text-sm">{b.phoneNumber}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">사유</p>
            <p className="text-sm whitespace-pre-wrap">
              {b.reason?.trim() || "—"}
            </p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">정지일</p>
            <p className="text-sm">{formatAdminMemberDateTime(b.createdAt)}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">만료일</p>
            <p className="text-sm">{formatAdminMemberDateTime(b.expiredAt)}</p>
          </div>
        </div>
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">정지 기록</h2>
        </div>
        <div className="flex flex-col gap-2 px-2 py-2">
          {b.histories.length === 0 ? (
            <p className="text-sm text-slate-500">기록이 없습니다.</p>
          ) : (
            b.histories.map((row, index) => (
              <article
                key={`${row.createdAt}-${index}`}
                className="flex flex-col gap-2 border border-slate-200 rounded-md bg-white p-2 text-sm"
              >
                <div className="flex items-center justify-between gap-2">
                  <span className="font-bold">
                    {formatAdminReportTypeLabel(row.type)}
                  </span>
                  <span className="shrink-0 text-[11px] font-medium tabular-nums text-slate-400">
                    #{index + 1}
                  </span>
                </div>
                <p className="text-xs text-slate-600">
                  휴대폰:{" "}
                  <span className="break-all text-slate-800">
                    {row.phoneNumber}
                  </span>
                </p>
                <p className="text-xs text-slate-600">
                  정지일:{" "}
                  <span className="tabular-nums text-slate-800">
                    {formatAdminMemberDateTime(row.createdAt)}
                  </span>
                </p>
                <p className="text-xs text-slate-600">
                  만료일:{" "}
                  <span className="tabular-nums text-slate-800">
                    {formatAdminMemberDateTime(row.expiredAt)}
                  </span>
                </p>
                <p className="line-clamp-3 text-xs text-slate-700">
                  사유: {row.reason?.trim() || "—"}
                </p>
              </article>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
