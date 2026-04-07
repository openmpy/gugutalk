import Image from "next/image";

export default function ReportDetailPage() {
  return (
    <div className="max-w-7xl mx-auto">
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-2">
        <h1 className="text-2xl font-bold">도배</h1>
      </div>
      <div className="flex items-center gap-2 mb-4">
        <button className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold">
          보류
        </button>
        <button className="px-4 py-2 rounded-md bg-slate-200 text-sm font-semibold">
          처리
        </button>
      </div>

      {/* 신고 정보 */}
      <div className="flex flex-col gap-4">
        <div>
          <h2 className="text-lg font-medium">정보</h2>
          <p className="text-gray-500">ID: 1</p>
          <hr className="my-2 border-gray-200" />
          <p className="text-gray-500">신고자 UUID: 1234567890</p>
          <p className="text-gray-500">신고자: 홍길동</p>
          <p className="text-gray-500">신고자 휴대폰: 010-0000-0000</p>
          <hr className="my-2 border-gray-200" />
          <p className="text-gray-500">피신고자 UUID: 1234567890</p>
          <p className="text-gray-500">피신고자: 박명수</p>
          <p className="text-gray-500">피신고자 휴대폰: 010-0000-0000</p>
          <hr className="my-2 border-gray-200" />
          <p className="text-gray-500">신고일자: 2026-01-01 12:00:00</p>
          <p className="text-gray-500">처리일자: 2026-01-01 12:00:00</p>
        </div>

        <div>
          <h2 className="text-lg font-medium">증거 자료</h2>
          <div className="flex gap-2 overflow-x-auto">
            {[...Array(5)].map((_, i) => (
              <div
                key={i}
                className="shrink-0 w-20 sm:w-28 md:w-32 aspect-square"
              >
                <Image
                  width={200}
                  height={200}
                  src="https://picsum.photos/200"
                  alt="profile"
                  placeholder="blur"
                  blurDataURL="https://picsum.photos/200"
                  loading="lazy"
                  className="rounded-md object-cover"
                />
              </div>
            ))}
          </div>
        </div>

        <div>
          <h2 className="text-lg font-medium">추가 설명</h2>
          <p className="text-gray-500">내용</p>
        </div>
      </div>
    </div>
  );
}
