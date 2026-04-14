import "server-only";

import { cookies } from "next/headers";

import { ADMIN_ACCESS_COOKIE } from "@/lib/adminSession";

export async function adminUpstreamInit(init: RequestInit = {}): Promise<RequestInit> {
  const token = (await cookies()).get(ADMIN_ACCESS_COOKIE)?.value;
  const headers = new Headers(init.headers);
  if (token) headers.set("Authorization", `Bearer ${token}`);
  return {
    ...init,
    headers,
    cache: init.cache ?? "no-store",
  };
}

export async function adminAuthorizedFetch(
  input: string | URL,
  init?: RequestInit,
): Promise<Response> {
  return fetch(input, await adminUpstreamInit(init ?? {}));
}
