import RefreshButton from "@/component/RefreshButton";
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
      <body className="h-full flex flex-col bg-slate-200 overflow-hidden">
        <main className="flex-1 overflow-hidden">
          <div className="max-w-4xl mx-auto bg-white h-full flex flex-col overflow-y-auto relative">
            <Header />
            {children}
            <Footer />

            <RefreshButton />
          </div>
        </main>
      </body>
    </html>
  );
}
