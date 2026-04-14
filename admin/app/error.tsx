"use client";

import { useEffect } from "react";

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  useEffect(() => {
    console.error("[admin] route error:", error);
  }, [error]);

  return (
    <div className="flex min-h-[50vh] flex-col items-center justify-center gap-6 px-4 py-12 text-center">
      <div className="max-w-md space-y-2">
        <h1 className="text-xl font-bold text-slate-800">오류가 발생했습니다</h1>
        <p className="text-sm text-slate-600">
          일시적인 문제일 수 있습니다. 다시 시도해 주세요.
        </p>
        {process.env.NODE_ENV === "development" && (
          <pre className="mt-4 max-h-40 overflow-auto rounded bg-slate-100 p-3 text-left text-xs text-red-800">
            {error.message}
            {error.digest ? `\n(digest: ${error.digest})` : ""}
          </pre>
        )}
      </div>
      <button
        type="button"
        onClick={() => reset()}
        className="rounded-md bg-slate-600 px-4 py-2 text-sm font-semibold text-white"
      >
        다시 시도
      </button>
    </div>
  );
}
