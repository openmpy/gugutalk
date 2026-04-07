import Footer from "@/layout/Footer";
import Header from "@/layout/Header";
import type { Metadata } from "next";
import { NotoSansKR } from "./font";
import "./globals.css";

export const metadata: Metadata = {
  title: "구구톡 어드민 페이지",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko" className="h-full antialiased">
      <body
        className={`min-h-full flex flex-col bg-slate-500 ${NotoSansKR.className}`}
      >
        <Header />
        <main className="flex-1 bg-white p-4">{children}</main>
        <Footer />
      </body>
    </html>
  );
}
