import ListButton from "@/component/ListButton";
import ReportAdminStatusBar from "@/component/ReportAdminStatusBar";
import { formatAdminMemberDateTime } from "@/lib/members";
import {
  fetchAdminReportDetail,
  formatAdminReportStatusLabel,
  formatAdminReportTypeLabel,
} from "@/lib/reports";
import Image from "next/image";
import { notFound } from "next/navigation";

export default async function ReportDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const reportId = Number(id);
  if (!Number.isFinite(reportId) || reportId < 1) {
    notFound();
  }

  const result = await fetchAdminReportDetail(reportId);
  if (!result.ok) {
    notFound();
  }

  const r = result.data;
  const sortedImages = [...r.images].sort((a, b) => a.index - b.index);

  return (
    <div>
      <div className="flex items-center justify-between gap-1 text-xs px-2 bg-slate-400 py-1">
        <ListButton href="/report" />
        <ReportAdminStatusBar reportId={r.reportId} status={r.status} />
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">신고자</h2>
        </div>
        <div className="flex flex-col gap-1">
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">ID</p>
            <p className="text-sm font-mono">{r.reporterId}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">UUID</p>
            <p className="text-sm break-all">{r.reporterUuid}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">휴대폰</p>
            <p className="text-sm">{r.reporterPhoneNumber}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">닉네임</p>
            <p className="text-sm">{r.reporterNickname}</p>
          </div>
        </div>
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">피신고자</h2>
        </div>
        <div className="flex flex-col gap-1">
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">ID</p>
            <p className="text-sm font-mono">{r.reportedId}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">UUID</p>
            <p className="text-sm break-all">{r.reportedUuid}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">휴대폰</p>
            <p className="text-sm">{r.reportedPhoneNumber}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">닉네임</p>
            <p className="text-sm">{r.reportedNickname}</p>
          </div>
        </div>
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">신고 내용</h2>
        </div>
        <div className="flex flex-col gap-1">
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">ID</p>
            <p className="text-sm font-mono">{r.reportId}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">상태</p>
            <p className="text-sm">{formatAdminReportStatusLabel(r.status)}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">유형</p>
            <p className="text-sm">{formatAdminReportTypeLabel(r.type)}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">사유</p>
            <p className="text-sm whitespace-pre-wrap">
              {r.reason?.trim() || "—"}
            </p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">신고일</p>
            <p className="text-sm">{formatAdminMemberDateTime(r.createdAt)}</p>
          </div>
        </div>
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">증거 자료</h2>
        </div>
        <div className="p-2 flex flex-col">
          {sortedImages.length === 0 ? (
            <p className="text-sm text-slate-500">첨부된 이미지가 없습니다.</p>
          ) : (
            <div className="flex flex-wrap gap-2">
              {sortedImages.map((img) => (
                <div key={img.imageId} className="relative">
                  <Image
                    src={img.url}
                    alt=""
                    width={100}
                    height={100}
                    className="w-[100px] h-[100px] shrink-0 rounded-md border border-slate-300 object-cover"
                    unoptimized
                  />
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
