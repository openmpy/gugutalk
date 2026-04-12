import Header from "@/component/Header";
import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "구구톡 관리자 대시보드",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko" className="h-full antialiased">
      <body className="min-h-full flex flex-col">
        <Header />
        <main>{children}</main>
      </body>
    </html>
  );
}
