import Footer from "@/layout/Footer";
import Header from "@/layout/Header";
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
      <body className="min-h-full flex flex-col bg-slate-500">
        <Header />
        <main className="flex-1 bg-white">{children}</main>
        <Footer />
      </body>
    </html>
  );
}
