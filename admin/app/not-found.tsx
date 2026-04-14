export default function NotFound() {
  return (
    <div className="flex min-h-[50vh] flex-col items-center justify-center gap-4 px-4 py-12 text-center">
      <h1 className="text-xl font-bold text-slate-800">
        페이지를 찾을 수 없습니다
      </h1>
      <p className="text-sm text-slate-600">
        주소가 잘못되었거나 삭제된 페이지입니다. 상단 메뉴에서 이동해 주세요.
      </p>
    </div>
  );
}
