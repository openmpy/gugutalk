import Link from "next/link";

export default function Header() {
  return (
    <header className="bg-slate-500 p-4">
      <div className="max-w-7xl mx-auto">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold text-white">구구톡 관리자</h1>
          <div className="flex items-center gap-4">
            <Link
              href="/member"
              className="text-white text-lg font-semibold hover:text-slate-300"
            >
              회원 목록
            </Link>
            <Link
              href="/ban"
              className="text-white text-lg font-semibold hover:text-slate-300"
            >
              정지 목록
            </Link>
          </div>
        </div>
      </div>
    </header>
  );
}
