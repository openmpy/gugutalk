import { AdminGetMemberImageResponse } from "./AdminGetMemberImageResponse";

export type AdminGetMemberDetailResponse = {
  memberId: number;
  uuid: string;
  phoneNumber: string;
  nickname: string;
  birthYear: number;
  gender: "MALE" | "FEMALE";
  bio: string | null;
  comment: string | null;
  createdAt: string;
  updatedAt: string;
  deletedAt: string | null;
  images: AdminGetMemberImageResponse[];
};
