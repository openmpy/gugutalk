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
    <html lang="ko" className="h-full min-h-dvh antialiased">
      <body className="flex h-full min-h-dvh flex-col overflow-hidden bg-slate-200">
        <main className="flex min-h-0 flex-1 overflow-hidden">
          <div className="relative mx-auto flex h-full min-h-0 w-full max-w-4xl flex-col bg-white">
            <Header />
            <div className="min-h-0 flex-1 overflow-y-auto">{children}</div>
            <Footer />
            <RefreshButton />
          </div>
        </main>
      </body>
    </html>
  );
}
