import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { 
  LayoutDashboard, 
  Briefcase, 
  Users, 
  FileText, 
  Calendar, 
  Settings, 
  LogOut, 
  Search,
  Bell,
  ChevronRight,
  Plus
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Separator } from "@/components/ui/separator";

export default function DashboardPage() {
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
    }
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  return (
    <div className="flex h-screen bg-slate-50/50 font-sans overflow-hidden">
      
      {/* Sidebar */}
      <aside className="w-72 bg-white border-r border-slate-200 flex flex-col shadow-sm">
        <div className="p-8">
          <div className="flex items-center gap-2 mb-12">
            <div className="p-2 bg-primary rounded-xl text-white shadow-lg shadow-primary/20">
              <LayoutDashboard size={20} />
            </div>
            <span className="text-xl font-bold text-slate-900 tracking-tight">LawAuto Pro</span>
          </div>

          <nav className="space-y-1.5">
            <NavItem icon={<LayoutDashboard size={20} />} label="Panel" active />
            <NavItem icon={<Briefcase size={20} />} label="Dosyalar" />
            <NavItem icon={<Users size={20} />} label="Müvekkiller" />
            <NavItem icon={<FileText size={20} />} label="Dilekçeler" />
            <NavItem icon={<Calendar size={20} />} label="Takvim" />
          </nav>
        </div>

        <div className="mt-auto p-8">
          <Separator className="mb-6" />
          <nav className="space-y-1.5">
            <NavItem icon={<Settings size={20} />} label="Ayarlar" />
            <Button 
              variant="ghost"
              onClick={handleLogout}
              className="w-full justify-start gap-3 px-4 py-6 text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-xl transition-all"
            >
              <LogOut size={20} />
              <span className="font-medium text-md">Çıkış Yap</span>
            </Button>
          </nav>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col overflow-hidden">
        
        {/* Header */}
        <header className="h-20 bg-white/80 backdrop-blur-md border-b border-slate-200 flex items-center justify-between px-12 sticky top-0 z-10">
          <div className="relative w-96 group">
            <Search size={18} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-primary transition-colors" />
            <Input 
              type="text" 
              placeholder="Dosya, müvekkil veya dilekçe ara..." 
              className="pl-10 h-11 bg-slate-100 border-none rounded-xl focus-visible:ring-primary/20 transition-all"
            />
          </div>

          <div className="flex items-center gap-6">
            <Button variant="ghost" size="icon" className="relative text-slate-500 hover:bg-slate-100 rounded-xl transition-all">
              <Bell size={20} />
              <span className="absolute top-2.5 right-2.5 w-2 h-2 bg-red-500 rounded-full border-2 border-white"></span>
            </Button>
            
            <Separator orientation="vertical" className="h-8" />
            
            <div className="flex items-center gap-3">
              <div className="text-right">
                <p className="text-sm font-bold text-slate-900 leading-tight">Av. Orhan Yılmaz</p>
                <p className="text-xs text-slate-500">Kıdemli Ortak</p>
              </div>
              <Avatar className="h-10 w-10 rounded-xl ring-2 ring-primary/10">
                <AvatarImage src="" />
                <AvatarFallback className="bg-primary text-white font-bold text-sm">OY</AvatarFallback>
              </Avatar>
            </div>
          </div>
        </header>

        {/* Content Area */}
        <div className="flex-1 overflow-y-auto p-12 space-y-12 bg-slate-50/50">
          
          {/* Welcome Section */}
          <div className="flex justify-between items-end">
            <div>
              <h1 className="text-4xl font-extrabold text-slate-900 mb-2 tracking-tight">Hoş Geldiniz, Orhan</h1>
              <p className="text-slate-500 text-lg font-medium">Bugün için <span className="text-primary font-bold">3 duruşmanız</span> ve <span className="text-primary font-bold">5 bekleyen dilekçeniz</span> var.</p>
            </div>
            <Button className="h-12 px-6 rounded-xl font-bold shadow-lg shadow-primary/20 hover:scale-[1.02] transition-all gap-2">
              <Plus size={20} />
              <span>Yeni Dosya Ekle</span>
            </Button>
          </div>

          {/* Stats Grid */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <StatCard label="Aktif Dosyalar" value="24" trend="+3 bu ay" />
            <StatCard label="Tamamlanan Dilekçeler" value="128" trend="+12 bu hafta" />
            <StatCard label="Müvekkil Sayısı" value="86" trend="+5 yeni" />
          </div>

          {/* Table / List Placeholder */}
          <Card className="rounded-3xl border-slate-200 shadow-sm overflow-hidden bg-white">
            <CardHeader className="p-8 border-b border-slate-100 flex-row justify-between items-center space-y-0">
              <CardTitle className="text-xl font-bold text-slate-900">Son Dosyalar</CardTitle>
              <Button variant="link" className="text-primary font-bold p-0 flex items-center gap-1">
                Tümünü Gör <ChevronRight size={16} />
              </Button>
            </CardHeader>
            <CardContent className="p-8 pt-6">
              <div className="space-y-4">
                {[1, 2, 3].map((i) => (
                  <div key={i} className="flex items-center justify-between p-5 hover:bg-slate-50 rounded-2xl transition-all group cursor-pointer border border-transparent hover:border-slate-100">
                    <div className="flex items-center gap-4">
                      <div className="w-12 h-12 bg-blue-50 text-blue-600 rounded-xl flex items-center justify-center font-bold">
                        D{i}
                      </div>
                      <div>
                        <p className="font-bold text-slate-900">Dosya #2024/{i * 123}</p>
                        <p className="text-sm text-slate-500 font-medium">Müvekkil: Ahmet Yılmaz</p>
                      </div>
                    </div>
                    <div className="flex items-center gap-8">
                      <div className="text-right">
                        <p className="text-sm font-bold text-slate-900">Ağır Ceza Mahkemesi</p>
                        <p className="text-xs text-slate-500 font-medium">Duruşma: 15.05.2024</p>
                      </div>
                      <span className="px-4 py-1.5 bg-green-50 text-green-700 text-xs font-bold rounded-full border border-green-100">
                        AÇIK
                      </span>
                      <ChevronRight size={20} className="text-slate-300 group-hover:text-primary transition-colors" />
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

        </div>
      </main>
    </div>
  );
}

function NavItem({ icon, label, active = false }) {
  return (
    <a 
      href="#" 
      className={`
        flex items-center gap-3 px-4 py-3.5 rounded-xl transition-all group
        ${active 
          ? "bg-primary text-white shadow-lg shadow-primary/20 font-bold" 
          : "text-slate-500 hover:bg-slate-100 hover:text-slate-900"}
      `}
    >
      <span className={active ? "text-white" : "text-slate-400 group-hover:text-primary transition-colors"}>
        {icon}
      </span>
      <span className="font-medium text-md">{label}</span>
    </a>
  );
}

function StatCard({ label, value, trend }) {
  return (
    <Card className="p-8 rounded-3xl border-slate-200 shadow-sm hover:shadow-md transition-all group">
      <p className="text-slate-500 text-sm font-bold mb-1 uppercase tracking-wider">{label}</p>
      <div className="flex items-baseline gap-3">
        <h4 className="text-4xl font-extrabold text-slate-900 group-hover:text-primary transition-colors">{value}</h4>
        <span className="text-xs font-bold text-green-700 bg-green-50 px-3 py-1 rounded-full border border-green-100">{trend}</span>
      </div>
    </Card>
  );
}

