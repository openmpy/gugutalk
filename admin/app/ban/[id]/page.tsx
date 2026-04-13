const banHistorySamples = [
  {
    type: "도배",
    phone: "010-1234-5678",
    reason: "사유",
    suspendedAt: "2026-01-01 12:00:00",
    releasedAt: "2026-01-01 12:00:00",
  },
  {
    type: "도배",
    phone: "010-1234-5678",
    reason: "사유",
    suspendedAt: "2026-01-01 12:00:00",
    releasedAt: "2026-01-01 12:00:00",
  },
] as const;

export default async function BanDetailPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;

  return (
    <div>
      <div className="flex items-center justify-between gap-1 text-xs px-2 bg-slate-400 py-1">
        <div>
          <button className="bg-slate-600 text-white px-2 py-1 rounded-md">
            목록
          </button>
        </div>
        <div className="flex gap-1">
          <button className="bg-blue-500 text-white px-2 py-1 rounded-md">
            해제
          </button>
        </div>
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">정보</h2>
        </div>
        <div className="flex flex-col gap-1">
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">ID</p>
            <p className="text-sm font-mono">{id}</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">유형</p>
            <p className="text-sm">도배</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">UUID</p>
            <p className="text-sm">uuid</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">휴대폰</p>
            <p className="text-sm">010-1234-5678</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">사유</p>
            <p className="text-sm">사유</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">정지일</p>
            <p className="text-sm">2025-01-01 12:00:00</p>
          </div>
          <div className="flex flex-col border-b border-slate-200 px-2 py-1">
            <p className="text-sm font-bold">해제일</p>
            <p className="text-sm">2025-01-01 12:00:00</p>
          </div>
        </div>
      </div>
      <div>
        <div className="flex items-center justify-center bg-slate-300 py-1">
          <h2 className="font-bold">정지 기록</h2>
        </div>
        <div className="flex flex-col gap-2 p-2">
          {banHistorySamples.map((row, index) => (
            <article
              key={index}
              className="overflow-hidden rounded-lg border border-slate-200 bg-white"
            >
              <div className="min-w-0 p-3">
                <div className="mb-2 flex flex-wrap items-center justify-between gap-2">
                  <span className="inline-flex items-center rounded-md bg-slate-100 px-2 py-0.5 text-xs font-bold text-slate-700">
                    {row.type}
                  </span>
                  <span className="text-[11px] font-medium tabular-nums text-slate-400">
                    #{index + 1}
                  </span>
                </div>
                <dl className="space-y-2 text-sm">
                  {(
                    [
                      ["휴대폰", row.phone, true] as const,
                      ["사유", row.reason, false] as const,
                      ["정지일", row.suspendedAt, false] as const,
                      ["해제일", row.releasedAt, false] as const,
                    ] as const
                  ).map(([label, value, mono]) => (
                    <div
                      key={label}
                      className="grid grid-cols-[4.5rem_1fr] gap-x-2 sm:grid-cols-[5.5rem_1fr]"
                    >
                      <dt className="text-xs font-semibold text-slate-500">
                        {label}
                      </dt>
                      <dd
                        className={`min-w-0 text-xs text-slate-800 ${mono ? "break-all" : ""} ${label === "정지일" || label === "해제일" ? "tabular-nums" : ""}`}
                      >
                        {value}
                      </dd>
                    </div>
                  ))}
                </dl>
              </div>
            </article>
          ))}
        </div>
      </div>
    </div>
  );
}
