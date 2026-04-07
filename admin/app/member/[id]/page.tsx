import { AdminGetMemberDetailResponse } from "@/types/AdminGetMemberDetailResponse";
import { AdminGetMemberImageResponse } from "@/types/AdminGetMemberImageResponse";
import { formatDate } from "@/utils/formatDate";
import { X } from "lucide-react";
import Image from "next/image";
import Link from "next/link";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

async function getMemberDetail(memberId: string) {
  const response = await fetch(
    `${API_BASE_URL}/api/v1/admin/members/${memberId}`,
    {
      cache: "no-store",
    },
  );
  if (!response.ok) {
    throw new Error("회원 상세 정보를 불러오지 못했습니다.");
  }
  return (await response.json()) as AdminGetMemberDetailResponse;
}

export default async function MemberDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;
  const data = await getMemberDetail(id);
  const publicImages = data.images.filter(
    (image: AdminGetMemberImageResponse) => image.type === "PUBLIC",
  );
  const privateImages = data.images.filter(
    (image: AdminGetMemberImageResponse) => image.type === "PRIVATE",
  );

  return (
    <div className="max-w-7xl mx-auto">
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-2">
        <h1 className="text-2xl font-bold">{data.nickname}</h1>
      </div>
      <div className="flex items-center gap-2 mb-4">
        <button className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold">
          닉네임
        </button>
        <button className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold">
          코멘트
        </button>
        <button className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold">
          자기소개
        </button>
        <button className="px-4 py-2 rounded-md bg-red-500 text-sm font-semibold text-white">
          정지
        </button>
      </div>

      {/* 회원 정보 */}
      <div className="flex flex-col gap-4">
        <div>
          <h2 className="text-lg font-medium">정보</h2>
          <p className="text-gray-500">ID: {data.memberId}</p>
          <p className="text-gray-500">UUID: {data.uuid}</p>
          <p className="text-gray-500">휴대폰: {data.phoneNumber}</p>
          <p className="text-gray-500">출생연도: {data.birthYear}년생</p>
          <p className="text-gray-500">
            성별: {data.gender === "MALE" ? "남자" : "여자"}
          </p>
          <hr className="my-2 border-gray-200" />
          <p className="text-gray-500">
            생성일자: {formatDate(data.createdAt)}
          </p>
          <p className="text-gray-500">
            수정일자: {formatDate(data.updatedAt)}
          </p>
          <p className="text-gray-500">
            탈퇴일자: {formatDate(data.deletedAt)}
          </p>
        </div>

        <div>
          <h2 className="text-lg font-medium">공개 사진</h2>
          <div className="flex gap-2 overflow-x-auto">
            {publicImages.length === 0 ? (
              <p className="text-sm text-gray-500">공개 사진이 없습니다.</p>
            ) : (
              publicImages.map((image) => (
                <div
                  key={image.imageId}
                  className="relative shrink-0 w-20 sm:w-28 md:w-32 aspect-square"
                >
                  <Link
                    href={image.url}
                    target="_blank"
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

                  <button className="absolute top-1 right-1 w-4 h-4 bg-red-500 text-white rounded-full flex items-center justify-center text-xs shadow">
                    <X className="w-3 h-3" />
                  </button>
                </div>
              ))
            )}
          </div>
        </div>

        <div>
          <h2 className="text-lg font-medium">비밀 사진</h2>
          <div className="flex gap-2 overflow-x-auto">
            {privateImages.length === 0 ? (
              <p className="text-sm text-gray-500">비밀 사진이 없습니다.</p>
            ) : (
              privateImages.map((image) => (
                <div
                  key={image.imageId}
                  className="relative shrink-0 w-20 sm:w-28 md:w-32 aspect-square"
                >
                  <Link
                    href={image.url}
                    target="_blank"
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

                  <button className="absolute top-1 right-1 w-4 h-4 bg-red-500 text-white rounded-full flex items-center justify-center text-xs shadow">
                    <X className="w-3 h-3" />
                  </button>
                </div>
              ))
            )}
          </div>
        </div>

        <div>
          <h2 className="text-lg font-medium">코멘트</h2>
          <p className="text-gray-500">{data.comment ?? "-"}</p>
        </div>

        <div>
          <h2 className="text-lg font-medium">자기소개</h2>
          <p className="text-gray-500">{data.bio ?? "-"}</p>
        </div>
      </div>
    </div>
  );
}
