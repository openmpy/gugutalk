import type { ReportType } from "@/types/AdminGetReportResponse";

export type AdminBanGetResponse = {
  banId: number;
  type: ReportType;
  uuid: string;
  nickname: string | null;
  reason: string | null;
  expiredAt: string;
};
