export type AdminGetMemberResponse = {
  memberId: number;
  profileUrl: string | null;
  nickname: string;
  age: number;
  gender: "MALE" | "FEMALE";
  comment: string | null;
  updatedAt: string;
  deletedAt: string | null;
};
