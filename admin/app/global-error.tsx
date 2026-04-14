"use client";

import { useEffect } from "react";
import "./globals.css";

export default function GlobalError({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  useEffect(() => {
    console.error("[admin] root error:", error);
  }, [error]);

  return (
    <html lang="ko" className="h-full min-h-dvh antialiased">
      <body className="flex h-full min-h-dvh flex-col items-center justify-center bg-slate-200 p-6">
        <div className="w-full max-w-md space-y-4 rounded-lg bg-white p-8 text-center shadow-sm">
          <h1 className="text-xl font-bold text-slate-800">
            심각한 오류가 발생했습니다
          </h1>
          <p className="text-sm text-slate-600">
            페이지를 새로고침하거나 잠시 후 다시 시도해 주세요.
          </p>
          {process.env.NODE_ENV === "development" && (
            <pre className="max-h-40 overflow-auto rounded bg-slate-100 p-3 text-left text-xs text-red-800">
              {error.message}
              {error.digest ? `\n(digest: ${error.digest})` : ""}
            </pre>
          )}
          <div className="pt-2">
            <button
              type="button"
              onClick={() => reset()}
              className="rounded-md bg-slate-600 px-4 py-2 text-sm font-semibold text-white"
            >
              다시 시도
            </button>
          </div>
        </div>
      </body>
    </html>
  );
}
