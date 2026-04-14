import ListButton from "@/component/ListButton";
import ReportAdminStatusBar from "@/component/ReportAdminStatusBar";
import { fetchAdminBanByReportedUuid } from "@/lib/bans";
import { formatAdminMemberDateTime } from "@/lib/members";
import {
  fetchAdminReportDetail,
  formatAdminReportStatusLabel,
  formatAdminReportTypeLabel,
} from "@/lib/reports";
import Image from "next/image";
import Link from "next/link";
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
  const banLookup = await fetchAdminBanByReportedUuid(r.reportedUuid);

  return (
    <div>
      <div className="flex items-center justify-between gap-1 text-xs px-2 bg-slate-400 py-1">
        <ListButton href="/report" />
        <ReportAdminStatusBar
          reportId={r.reportId}
          status={r.status}
          reportedUuid={r.reportedUuid}
          reportedPhoneNumber={r.reportedPhoneNumber}
        />
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
          <h2 className="font-bold">피신고자 정지 현황</h2>
        </div>
        <div className="flex flex-col gap-1 px-2 py-2">
          {!banLookup.ok ? (
            <p className="text-sm text-red-600">
              정지 정보를 불러오지 못했습니다. (HTTP {banLookup.status})
            </p>
          ) : banLookup.ban === null ? (
            <p className="text-sm text-slate-600">
              현재 정지 중인 계정이 아닙니다.
            </p>
          ) : (
            <div className="flex flex-col gap-2 border border-slate-200 rounded-md bg-white p-2 text-sm">
              <div className="flex items-center justify-between gap-2">
                <span className="font-bold">
                  {formatAdminReportTypeLabel(banLookup.ban.type)}
                </span>
                <Link
                  href={`/ban/${banLookup.ban.banId}`}
                  className="shrink-0 rounded-md bg-slate-400 px-2 py-1 text-xs text-white"
                >
                  상세보기
                </Link>
              </div>
              <p className="text-xs text-slate-600">
                정지일:{" "}
                <span className="tabular-nums text-slate-800">
                  {formatAdminMemberDateTime(banLookup.ban.createdAt)}
                </span>
              </p>
              <p className="text-xs text-slate-600">
                만료일:{" "}
                <span className="tabular-nums text-slate-800">
                  {formatAdminMemberDateTime(banLookup.ban.expiredAt)}
                </span>
              </p>
              <p className="line-clamp-3 text-xs text-slate-700">
                사유: {banLookup.ban.reason?.trim() || "—"}
              </p>
            </div>
          )}
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
                <a
                  key={img.imageId}
                  href={img.url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="relative inline-block cursor-pointer"
                >
                  <Image
                    src={img.url}
                    alt=""
                    width={100}
                    height={100}
                    className="w-[100px] h-[100px] shrink-0 rounded-md border border-slate-300 object-cover"
                    unoptimized
                  />
                </a>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
