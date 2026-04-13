import MemberListLoadMore from "@/component/MemberListLoadMore";
import {
  fetchAdminMembers,
  normalizeAdminMemberGender,
  normalizeAdminMemberType,
  type AdminMemberSearchType,
} from "@/lib/members";
import Link from "next/link";
import { IoSearch } from "react-icons/io5";

function firstString(v: string | string[] | undefined): string | undefined {
  if (Array.isArray(v)) return v[0];
  return v;
}

function memberListHref(base: {
  type: AdminMemberSearchType;
  keyword: string;
  gender: "ALL" | "MALE" | "FEMALE";
}) {
  const q = new URLSearchParams();
  q.set("type", base.type);
  if (base.keyword) q.set("keyword", base.keyword);
  if (base.gender !== "ALL") q.set("gender", base.gender);
  const s = q.toString();
  return s ? `/member?${s}` : "/member";
}

export default async function MemberPage({
  searchParams,
}: {
  searchParams: Promise<Record<string, string | string[] | undefined>>;
}) {
  const sp = await searchParams;
  const type = normalizeAdminMemberType(firstString(sp.type));
  const keyword = firstString(sp.keyword) ?? "";
  const gender = normalizeAdminMemberGender(firstString(sp.gender));

  const result = await fetchAdminMembers({ type, keyword, gender });

  const filterBase = { type, keyword, gender };

  return (
    <div>
      <div className="flex items-center justify-center bg-slate-300 py-1">
        <h1 className="font-bold">회원 관리</h1>
      </div>
      <div className="flex items-center">
        <Link
          href={memberListHref({ ...filterBase, gender: "ALL" })}
          className={`flex-1 border-r border-r-slate-300 px-2 py-1 text-center text-white ${
            gender === "ALL" ? "bg-slate-500" : "bg-slate-400"
          }`}
        >
          전체
        </Link>
        <Link
          href={memberListHref({ ...filterBase, gender: "MALE" })}
          className={`flex-1 border-r border-r-slate-300 px-2 py-1 text-center text-white ${
            gender === "MALE" ? "bg-slate-500" : "bg-slate-400"
          }`}
        >
          남자
        </Link>
        <Link
          href={memberListHref({ ...filterBase, gender: "FEMALE" })}
          className={`flex-1 px-2 py-1 text-center text-white ${
            gender === "FEMALE" ? "bg-slate-500" : "bg-slate-400"
          }`}
        >
          여자
        </Link>
      </div>
      <form method="get" className="flex flex-wrap items-center">
        <input type="hidden" name="gender" value={gender} />
        <select
          name="type"
          defaultValue={type}
          className="h-9 w-[85px] shrink-0 border border-l-0 border-slate-300 bg-white px-2 text-base focus:outline-none"
        >
          <option value="NICKNAME">닉네임</option>
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
          className="h-9 shrink-0 border border-l-0 border-slate-300 px-2 text-base text-slate-600"
          aria-label="검색"
        >
          <IoSearch className="h-4 w-4" />
        </button>
      </form>
      {!result.ok ? (
        <p className="px-2 py-4 text-sm text-red-600">
          회원 목록을 불러오지 못했습니다. (HTTP {result.status}) 서버 주소는
          환경 변수 <span className="font-mono">ADMIN_API_BASE_URL</span> 로
          설정할 수 있습니다.
        </p>
      ) : result.data.payload.length === 0 ? (
        <p className="px-2 py-4 text-sm text-slate-600 text-center">
          조회된 회원이 없습니다.
        </p>
      ) : (
        <MemberListLoadMore
          key={JSON.stringify({ type, keyword, gender })}
          initial={result.data}
          type={type}
          keyword={keyword}
          gender={gender}
        />
      )}
    </div>
  );
}
