import BanRemoveButton from "@/components/BanRemoveButton";
import { AdminBanGetDetailResponse } from "@/types/AdminBanGetDetailResponse";
import { formatDate } from "@/utils/formatDate";
import { reportTypeLabel } from "@/utils/reportTypeLabel";
import { notFound } from "next/navigation";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

async function getBanDetail(banId: string): Promise<AdminBanGetDetailResponse | null> {
  const response = await fetch(
    `${API_BASE_URL}/api/v1/admin/bans/${banId}`,
    { cache: "no-store" },
  );
  if (response.status === 404) {
    return null;
  }
  if (!response.ok) {
    throw new Error("정지 상세 정보를 불러오지 못했습니다.");
  }
  return (await response.json()) as AdminBanGetDetailResponse;
}

export default async function BanDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const data = await getBanDetail(id);
  if (!data) {
    notFound();
  }

  return (
    <div className="max-w-7xl mx-auto">
      <div className="flex items-center justify-between mb-2">
        <h1 className="text-2xl font-bold">{reportTypeLabel(data.type)}</h1>
      </div>
      <div className="flex items-center gap-2 mb-4">
        <BanRemoveButton banId={String(data.banId)} />
      </div>

      <div className="flex flex-col gap-4">
        <div>
          <h2 className="text-lg font-medium">정보</h2>
          <p className="text-gray-500">ID: {data.banId}</p>
          <p className="text-gray-500">UUID: {data.uuid}</p>
          <p className="text-gray-500">닉네임: {data.nickname ?? "-"}</p>
          <p className="text-gray-500">휴대폰: {data.phoneNumber}</p>
          <hr className="my-2 border-gray-200" />
          <p className="text-gray-500">정지 일자: {formatDate(data.createdAt)}</p>
          <p className="text-gray-500">해제 예정: {formatDate(data.expiredAt)}</p>
        </div>

        <div>
          <h2 className="text-lg font-medium">사유</h2>
          <p className="text-gray-500 whitespace-pre-wrap">
            {data.reason?.trim() ? data.reason : "-"}
          </p>
        </div>
      </div>
    </div>
  );
}
