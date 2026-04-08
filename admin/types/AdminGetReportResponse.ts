export type ReportType =
  | "ABUSE"
  | "SPAM"
  | "MINOR"
  | "SEXUAL"
  | "FAKE"
  | "ETC";

export type AdminGetReportResponse = {
  reportId: number;
  type: ReportType;
  reporterNickname: string;
  reportedNickname: string;
  reason: string | null;
  createdAt: string;
};
