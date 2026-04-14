import BanAddPanel from "@/component/BanAddPanel";
import BanListLoadMore from "@/component/BanListLoadMore";
import { normalizeAdminBanType } from "@/lib/bans";
import { fetchAdminBans } from "@/lib/server/banFetches";
import { IoSearch } from "react-icons/io5";

function firstString(v: string | string[] | undefined): string | undefined {
  if (Array.isArray(v)) return v[0];
  return v;
}

export default async function BanPage({
  searchParams,
}: {
  searchParams: Promise<Record<string, string | string[] | undefined>>;
}) {
  const sp = await searchParams;
  const type = normalizeAdminBanType(firstString(sp.type));
  const keyword = firstString(sp.keyword) ?? "";

  const result = await fetchAdminBans({ type, keyword });

  return (
    <div>
      <div className="flex items-center justify-center bg-slate-300 py-1">
        <h1 className="font-bold">정지 내역</h1>
      </div>
      <BanAddPanel />
      <form method="get" className="flex flex-wrap items-center">
        <select
          name="type"
          defaultValue={type}
          className="h-9 w-[85px] shrink-0 border border-l-0 border-slate-300 bg-white px-2 text-base focus:outline-none"
        >
          <option value="UUID">UUID</option>
          <option value="PHONE">휴대폰</option>
        </select>
        <input
          type="text"
          name="keyword"
          defaultValue={keyword}
          placeholder="검색어를 입력해주세요."
          className="h-9 min-w-0 flex-1 border border-l-0 border-r-0 border-slate-300 pl-2 text-base focus:outline-none"
        />
        <button
          type="submit"
          className="h-9 shrink-0 border border-l-0 border-r-0 border-slate-300 px-2 text-base text-slate-600"
          aria-label="검색"
        >
          <IoSearch className="h-4 w-4" />
        </button>
      </form>
      {!result.ok ? (
        <p className="px-2 py-4 text-sm text-red-600">
          정지 목록을 불러오지 못했습니다. (HTTP {result.status}) 서버 주소는
          환경 변수 <span className="font-mono">ADMIN_API_BASE_URL</span> 로
          설정할 수 있습니다.
        </p>
      ) : result.data.payload.length === 0 ? (
        <p className="px-2 py-4 text-center text-sm text-slate-600">
          조회된 정지 내역이 없습니다.
        </p>
      ) : (
        <BanListLoadMore
          key={JSON.stringify({ type, keyword })}
          initial={result.data}
          type={type}
          keyword={keyword}
        />
      )}
    </div>
  );
}
