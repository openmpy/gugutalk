import AdminLoginForm from "@/component/AdminLoginForm";

function safeRedirectPath(from: string | string[] | undefined): string {
  const raw = Array.isArray(from) ? from[0] : from;
  if (!raw || typeof raw !== "string") return "/member";
  if (!raw.startsWith("/") || raw.startsWith("//")) return "/member";
  return raw;
}

export default async function LoginPage({
  searchParams,
}: {
  searchParams: Promise<{ from?: string | string[] }>;
}) {
  const sp = await searchParams;
  const redirectTo = safeRedirectPath(sp.from);

  return (
    <div className="flex min-h-[60vh] flex-col justify-center px-4 py-8">
      <h1 className="mb-6 text-center text-xl font-bold text-slate-800">
        관리자 로그인
      </h1>
      <AdminLoginForm redirectTo={redirectTo} />
    </div>
  );
}
