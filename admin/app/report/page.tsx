import Link from "next/link";
import { IoSearch } from "react-icons/io5";

export default function ReportPage() {
  const reports = Array.from({ length: 100 }, (_, i) => ({
    id: i,
    type: "도배",
    reporterNickname: `신고자 닉네임${i + 1}`,
    reportedNickname: `피신고자 닉네임${i + 1}`,
    reason: "사유",
    createdAt: "2026-04-12 12:00:00",
  }));

  return (
    <div>
      <div className="flex items-center justify-center bg-slate-300 py-1">
        <h1 className="font-bold">신고 내역</h1>
      </div>
      <div className="flex items-center">
        <button className="flex-1 px-2 py-1 bg-slate-400 text-white border-r border-r-slate-300">
          접수
        </button>
        <button className="flex-1 px-2 py-1 bg-slate-400 text-white border-r border-r-slate-300">
          반려
        </button>
        <button className="flex-1 px-2 py-1 bg-slate-400 text-white">
          처분
        </button>
      </div>
      <div className="flex flex-wrap items-center">
        <select className="h-9 w-[85px] shrink-0 border border-l-0 border-slate-300 bg-white px-2 text-base focus:outline-none">
          <option value="nickname">닉네임</option>
          <option value="uuid">UUID</option>
          <option value="phone">휴대폰</option>
        </select>
        <input
          type="text"
          placeholder="검색어를 입력해주세요."
          className="h-9 min-w-0 flex-1 border border-l-0 border-r-0 border-slate-300 pl-2 text-base focus:outline-none"
        />
        <button className="h-9 shrink-0 text-slate-300 border border-l-0 border-r-0 border-slate-300 px-2 text-base">
          <IoSearch className="w-4 h-4" />
        </button>
      </div>
      <div className="flex flex-col">
        {reports.map((report) => (
          <div key={report.id} className="flex flex-col">
            <div className="flex items-center">
              <div className="text-xs p-2 flex-1">
                <div className="flex items-center justify-between">
                  <Link
                    href={`/report/${report.id}`}
                    className="font-bold text-sm"
                  >
                    {report.type}
                  </Link>
                  <p>{report.createdAt}</p>
                </div>
                <p>신고자: {report.reporterNickname}</p>
                <p>피신고자: {report.reportedNickname}</p>
                <p className="line-clamp-2">사유: {report.reason}</p>
              </div>
            </div>
            <div className="flex items-center justify-end gap-1 text-xs pr-2 bg-slate-300 py-1">
              <button className="bg-red-500 text-white px-2 py-1 rounded-md">
                반려
              </button>
              <button className="bg-blue-500 text-white px-2 py-1 rounded-md">
                처분
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
