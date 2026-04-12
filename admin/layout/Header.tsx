import Link from "next/link";

export default function Header() {
  return (
    <header className="bg-slate-500 p-4 flex items-center justify-between">
      <h1 className="text-white font-bold text-2xl">구구톡</h1>
      <div className="flex items-center gap-4 text-white font-semibold">
        <Link href="/member">회원</Link>
        <Link href="/">신고</Link>
        <Link href="/">정지</Link>
      </div>
    </header>
  );
}
