export type PageResponse<T> = {
  payload: T[];
  page: number;
  hasNext: boolean;
};
