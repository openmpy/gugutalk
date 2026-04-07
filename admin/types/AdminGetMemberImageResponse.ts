export type AdminGetMemberImageResponse = {
  imageId: number;
  url: string;
  key: string;
  type: "PUBLIC" | "PRIVATE";
  sortOrder: number;
  createdAt: string;
};
