import "server-only";

import { adminAuthorizedFetch } from "@/lib/adminUpstream";
import type { AdminGetMemberDetailResponse } from "@/lib/members";
import {
  buildAdminMemberDetailUpstreamUrl,
  buildAdminMembersUpstreamUrl,
  type AdminGetMemberResponse,
  type AdminMemberSearchType,
  type CursorResponse,
} from "@/lib/members";

export async function fetchAdminMembers(params: {
  type: AdminMemberSearchType;
  keyword: string;
  gender: "ALL" | "MALE" | "FEMALE";
  cursorId?: string;
  cursorDate?: string;
  size?: number;
}): Promise<
  | { ok: true; data: CursorResponse<AdminGetMemberResponse> }
  | { ok: false; status: number }
> {
  const res = await adminAuthorizedFetch(buildAdminMembersUpstreamUrl(params));
  if (!res.ok) return { ok: false, status: res.status };

  const data = (await res.json()) as CursorResponse<AdminGetMemberResponse>;
  return { ok: true, data };
}

export async function fetchAdminMemberDetail(
  memberId: number,
): Promise<
  | { ok: true; data: AdminGetMemberDetailResponse }
  | { ok: false; status: number }
> {
  const res = await adminAuthorizedFetch(
    buildAdminMemberDetailUpstreamUrl(memberId),
  );
  if (!res.ok) return { ok: false, status: res.status };

  const data = (await res.json()) as AdminGetMemberDetailResponse;
  return { ok: true, data };
}
