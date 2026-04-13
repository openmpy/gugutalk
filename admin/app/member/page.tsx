import Image from "next/image";
import Link from "next/link";
import { IoSearch } from "react-icons/io5";

export default function MemberPage() {
  const members = Array.from({ length: 100 }, (_, i) => ({
    id: i,
    nickname: `닉네임${i + 1}`,
    phone: "010-1234-5678",
    comment: "코멘트 코멘트 코멘트 코멘트 코멘트 코멘트 코멘트 코멘트",
    age: 20,
    gender: "남자",
    time: "1시간 전",
    image: `https://picsum.photos/100?random=${i}`,
  }));

  return (
    <div>
      <div className="flex items-center justify-center bg-slate-300 py-1">
        <h1 className="font-bold">회원 관리</h1>
      </div>
      <div className="flex items-center">
        <button className="flex-1 px-2 py-1 bg-slate-400 text-white border-r border-r-slate-300">
          전체
        </button>
        <button className="flex-1 px-2 py-1 bg-slate-400 text-white border-r border-r-slate-300">
          남자
        </button>
        <button className="flex-1 px-2 py-1 bg-slate-400 text-white">
          여자
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
        {members.map((member) => (
          <div key={member.id} className="flex flex-col">
            <div className="flex items-center">
              <Image
                src={member.image}
                alt="member"
                width={100}
                height={100}
                className="w-[85px] h-[85px] shrink-0"
                loading="eager"
                priority
              />
              <div className="text-xs p-2 flex-1">
                <div className="flex items-center justify-between">
                  <Link
                    href={`/member/${member.id}`}
                    className="font-bold text-sm"
                  >
                    {member.nickname}
                  </Link>
                  <p>{member.time}</p>
                </div>
                <p>{member.phone}</p>
                <p className="line-clamp-2">{member.comment}</p>
                <p>
                  {member.age}살 · {member.gender}
                </p>
              </div>
            </div>
            <div className="flex items-center justify-end gap-1 text-xs pr-2 bg-slate-300 py-1">
              <button className="bg-green-600 text-white px-2 py-1 rounded-md">
                이미지 제거
              </button>
              <button className="bg-blue-500 text-white px-2 py-1 rounded-md">
                닉네임 변경
              </button>
              <button className="bg-purple-500 text-white px-2 py-1 rounded-md">
                코멘트 변경
              </button>
              <button className="bg-red-500 text-white px-2 py-1 rounded-md ">
                정지
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
