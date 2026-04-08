import { AdminGetReportDetailResponse } from "@/types/AdminGetReportDetailResponse";
import { formatDate } from "@/utils/formatDate";
import { reportTypeLabel } from "@/utils/reportTypeLabel";
import Image from "next/image";
import Link from "next/link";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

async function getReportDetail(reportId: string) {
  const response = await fetch(
    `${API_BASE_URL}/api/v1/admin/reports/${reportId}`,
    {
      cache: "no-store",
    },
  );
  if (!response.ok) {
    throw new Error("신고 상세 정보를 불러오지 못했습니다.");
  }
  return (await response.json()) as AdminGetReportDetailResponse;
}

export default async function ReportDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const data = await getReportDetail(id);
  const images = [...data.images].sort((a, b) => a.sortOrder - b.sortOrder);

  return (
    <div className="max-w-7xl mx-auto">
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-2">
        <h1 className="text-2xl font-bold">{reportTypeLabel(data.type)}</h1>
      </div>
      <div className="flex items-center gap-2 mb-4">
        <button className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold">
          보류
        </button>
        <button className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold">
          처리
        </button>
        <button className="px-4 py-2 rounded-md bg-red-500 text-sm font-semibold text-white">
          정지
        </button>
      </div>

      {/* 신고 정보 */}
      <div className="flex flex-col gap-4">
        <div>
          <h2 className="text-lg font-medium">정보</h2>
          <p className="text-gray-500">ID: {data.reportId}</p>
          <hr className="my-2 border-gray-200" />
          <p className="text-gray-500">신고자 ID: {data.reporterId}</p>
          <p className="text-gray-500">신고자 UUID: {data.reporterUuid}</p>
          <p className="text-gray-500">신고자: {data.reporterNickname}</p>
          <p className="text-gray-500">신고자 휴대폰: {data.reporterPhone}</p>
          <hr className="my-2 border-gray-200" />
          <p className="text-gray-500">피신고자 ID: {data.reportedId}</p>
          <p className="text-gray-500">피신고자 UUID: {data.reportedUuid}</p>
          <p className="text-gray-500">피신고자: {data.reportedNickname}</p>
          <p className="text-gray-500">피신고자 휴대폰: {data.reportedPhone}</p>
          <hr className="my-2 border-gray-200" />
          <p className="text-gray-500">
            신고일자: {formatDate(data.createdAt)}
          </p>
        </div>

        <div>
          <h2 className="text-lg font-medium">증거 자료</h2>
          <div className="flex gap-2 overflow-x-auto">
            {images.length === 0 ? (
              <p className="text-sm text-gray-500">첨부 이미지가 없습니다.</p>
            ) : (
              images.map((image) => (
                <div
                  key={image.imageId}
                  className="relative shrink-0 w-20 sm:w-28 md:w-32 aspect-square"
                >
                  <Link
                    href={image.url}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="relative block w-full h-full"
                  >
                    <Image
                      src={image.url}
                      alt={image.key}
                      fill
                      sizes="(max-width: 640px) 80px, (max-width: 768px) 112px, 128px"
                      className="rounded-md object-cover"
                      placeholder="blur"
                      blurDataURL={image.url}
                    />
                  </Link>
                </div>
              ))
            )}
          </div>
        </div>

        <div>
          <h2 className="text-lg font-medium">추가 설명</h2>
          <p className="text-gray-500">
            {data.reason?.trim() ? data.reason : "-"}
          </p>
        </div>
      </div>
    </div>
  );
}
