import type { ReportType } from "@/types/AdminGetReportResponse";

export function reportTypeLabel(type: ReportType): string {
  switch (type) {
    case "ABUSE":
      return "욕설 / 학대";
    case "SPAM":
      return "스팸 / 광고";
    case "MINOR":
      return "미성년자";
    case "SEXUAL":
      return "음란물";
    case "FAKE":
      return "도용";
    case "ETC":
      return "기타";
    default:
      return type;
  }
}
