"use client";

import { useState } from "react";

export default function AdminLogoutButton() {
  const [busy, setBusy] = useState(false);

  async function logout() {
    setBusy(true);
    try {
      await fetch("/api/auth/logout", { method: "POST" });
      window.location.href = "/login";
    } finally {
      setBusy(false);
    }
  }

  return (
    <button
      type="button"
      onClick={logout}
      disabled={busy}
      className="rounded-md border border-white/40 px-2 py-1 text-sm text-white hover:bg-white/10 disabled:opacity-50"
    >
      {busy ? "…" : "로그아웃"}
    </button>
  );
}
