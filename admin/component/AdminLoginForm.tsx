"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";

type Props = { redirectTo: string };

export default function AdminLoginForm({ redirectTo }: Props) {
  const router = useRouter();
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          phoneNumber: phoneNumber.trim(),
          password,
        }),
      });
      const text = await res.text();
      if (!res.ok) {
        let msg = `로그인에 실패했습니다. (${res.status})`;
        try {
          const j = JSON.parse(text) as { message?: string };
          if (typeof j.message === "string" && j.message.trim()) msg = j.message;
        } catch {
          if (text.trim()) msg = text.trim();
        }
        setError(msg);
        return;
      }
      router.push(redirectTo);
      router.refresh();
    } finally {
      setLoading(false);
    }
  }

  return (
    <form
      onSubmit={onSubmit}
      className="mx-auto w-full max-w-sm space-y-4 rounded-lg border border-slate-200 bg-slate-50 p-6 shadow-sm"
    >
      <label className="block text-sm font-medium text-slate-700">
        휴대폰 번호
        <input
          type="text"
          name="phoneNumber"
          autoComplete="username"
          value={phoneNumber}
          onChange={(e) => setPhoneNumber(e.target.value)}
          className="mt-1 w-full rounded border border-slate-300 px-3 py-2 text-base"
          required
        />
      </label>
      <label className="block text-sm font-medium text-slate-700">
        비밀번호
        <input
          type="password"
          name="password"
          autoComplete="current-password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="mt-1 w-full rounded border border-slate-300 px-3 py-2 text-base"
          required
        />
      </label>
      {error ? (
        <p className="text-sm text-red-600" role="alert">
          {error}
        </p>
      ) : null}
      <button
        type="submit"
        disabled={loading}
        className="w-full rounded-md bg-slate-600 py-2 font-semibold text-white disabled:opacity-50"
      >
        {loading ? "확인 중…" : "로그인"}
      </button>
    </form>
  );
}
