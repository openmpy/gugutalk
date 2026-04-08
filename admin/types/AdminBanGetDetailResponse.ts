import type { ReportType } from "@/types/AdminGetReportResponse";

export type AdminBanGetDetailResponse = {
  banId: number;
  type: ReportType;
  uuid: string;
  nickname: string | null;
  phoneNumber: string;
  reason: string | null;
  createdAt: string;
  expiredAt: string;
};
