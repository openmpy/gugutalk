import type { ReportType } from "./AdminGetReportResponse";

export type ReportStatus = "PENDING" | "REJECT" | "RESOLVE";

export type AdminGetReportImageResponse = {
  imageId: number;
  url: string;
  key: string;
  sortOrder: number;
  createdAt: string;
};

export type AdminGetReportDetailResponse = {
  reportId: number;
  status: ReportStatus;
  type: ReportType;
  reporterId: number;
  reporterUuid: string;
  reporterPhone: string;
  reporterNickname: string;
  reportedId: number;
  reportedUuid: string;
  reportedPhone: string;
  reportedNickname: string;
  reason: string | null;
  createdAt: string;
  images: AdminGetReportImageResponse[];
};
