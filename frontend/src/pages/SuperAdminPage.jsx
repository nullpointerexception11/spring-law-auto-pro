import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  ShieldAlert,
  Plus,
  Building2,
  Users,
  Search,
  Bell,
  LogOut,
  MoreVertical,
  ArrowUpRight,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { useAuthStore } from "@/store/useAuthStore";
import { ROUTES } from "@/lib/constants";

export default function SuperAdminPage() {
  const navigate = useNavigate();
  const role = useAuthStore((state) => state.role);
  const logout = useAuthStore((state) => state.logout);
  const [orgs] = useState([
    { id: "1", name: "Yılmaz Hukuk Bürosu", adminCount: 2, lawyerCount: 8, status: "ACTIVE", createdAt: "2024-05-01" },
    { id: "2", name: "Demir & Ortakları", adminCount: 1, lawyerCount: 4, status: "ACTIVE", createdAt: "2024-05-03" },
    { id: "3", name: "Global Avukatlık Ltd.", adminCount: 3, lawyerCount: 15, status: "PENDING", createdAt: "2024-05-07" },
  ]);

  useEffect(() => {
    if ((role || "").trim().toUpperCase() !== "PLATFORM_ADMIN") {
      navigate(ROUTES.DASHBOARD);
    }
  }, [navigate, role]);

  const handleLogout = () => {
    logout();
    navigate(ROUTES.LOGIN);
  };

  return (
    <div className="flex h-screen bg-slate-50/50 font-sans overflow-hidden">
      <aside className="w-72 bg-slate-900 border-r border-slate-800 flex flex-col shadow-xl text-slate-300">
        <div className="p-8">
          <div className="flex items-center gap-2 mb-12">
            <div className="p-2 bg-indigo-500 rounded-xl text-white shadow-lg shadow-indigo-500/20">
              <ShieldAlert size={20} />
            </div>
            <span className="text-xl font-bold text-white tracking-tight italic">SuperAdmin</span>
          </div>

          <div className="mb-6">
            <Badge variant="outline" className="border-indigo-400/30 text-indigo-200 bg-indigo-500/10">
              Önizleme
            </Badge>
          </div>

          <nav className="space-y-1.5">
            <NavItem icon={<Building2 size={20} />} label="Şirketler" active />
            <NavItem icon={<Users size={20} />} label="Yöneticiler" disabled />
            <NavItem icon={<ShieldAlert size={20} />} label="Sistem Logları" disabled />
          </nav>
        </div>

        <div className="mt-auto p-8 border-t border-slate-800">
          <Button
            variant="ghost"
            onClick={handleLogout}
            className="w-full justify-start gap-3 px-4 py-6 text-slate-400 hover:text-white hover:bg-slate-800 rounded-xl transition-all"
          >
            <LogOut size={20} />
            <span className="font-medium text-md">Çıkış Yap</span>
          </Button>
        </div>
      </aside>

      <main className="flex-1 flex flex-col overflow-hidden">
        <header className="h-20 bg-white border-b border-slate-200 flex items-center justify-between px-12 sticky top-0 z-10">
          <div className="flex items-center gap-4">
            <h2 className="text-xl font-bold text-slate-900">Sistem Paneli</h2>
            <div className="px-3 py-1 bg-indigo-50 text-indigo-600 rounded-full text-xs font-bold border border-indigo-100 uppercase tracking-wider">
              Kontrol Merkezi
            </div>
          </div>

          <div className="flex items-center gap-6">
            <Button variant="ghost" size="icon" className="relative text-slate-500 hover:bg-slate-100 rounded-xl transition-all">
              <Bell size={20} />
              <span className="absolute top-2.5 right-2.5 w-2 h-2 bg-indigo-500 rounded-full border-2 border-white" />
            </Button>

            <Separator orientation="vertical" className="h-8" />

            <div className="flex items-center gap-3">
              <div className="text-right">
                <p className="text-sm font-bold text-slate-900 leading-tight">LawAuto Master</p>
                <p className="text-xs text-indigo-500 font-bold uppercase tracking-tighter">Süper Yönetici</p>
              </div>
              <Avatar className="h-10 w-10 rounded-xl ring-2 ring-indigo-500/10">
                <AvatarFallback className="bg-indigo-600 text-white font-bold text-sm">SA</AvatarFallback>
              </Avatar>
            </div>
          </div>
        </header>

        <div className="flex-1 overflow-y-auto p-12 space-y-8 bg-slate-50/50">
          <Card className="rounded-3xl border-amber-200 bg-amber-50/70 shadow-sm">
            <CardContent className="p-5 flex items-start gap-3">
              <Badge variant="outline" className="border-amber-200 bg-background text-amber-700">
                Önizleme
              </Badge>
              <p className="text-sm text-amber-900/90">
                Bu ekran şu anda backend'e bağlı değil. Şirket listesi ve yönetim akışları yalnızca arayüz önizlemesi olarak tutuluyor.
              </p>
            </CardContent>
          </Card>

          <div className="flex justify-between items-end gap-4">
            <div>
              <h1 className="text-4xl font-extrabold text-slate-900 mb-2 tracking-tight">Müşteri Yönetimi</h1>
              <p className="text-slate-500 text-lg font-medium">Sistemdeki tüm hukuk bürolarını buradan yönetebilirsiniz.</p>
            </div>

            <Button
              className="h-12 px-6 rounded-xl font-bold shadow-lg shadow-indigo-600/20 bg-indigo-600/50 hover:bg-indigo-600/50 transition-all gap-2"
              disabled
              title="Önizleme"
            >
              <Plus size={20} />
              <span>Yeni Şirket Kaydet</span>
            </Button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
            <QuickStat label="Toplam Şirket" value="12" icon={<Building2 className="text-indigo-600" />} />
            <QuickStat label="Aktif Kullanıcı" value="142" icon={<Users className="text-blue-600" />} />
            <QuickStat label="Bu Ayki Gelir" value="₺42,500" icon={<ArrowUpRight className="text-green-600" />} />
            <QuickStat label="Bekleyen Talepler" value="3" icon={<ShieldAlert className="text-orange-600" />} />
          </div>

          <Card className="rounded-3xl border-slate-200 shadow-sm overflow-hidden bg-white">
            <CardHeader className="p-8 border-b border-slate-100 flex-row justify-between items-center space-y-0">
              <CardTitle className="text-xl font-bold text-slate-900 flex items-center gap-2">
                Kayıtlı Şirketler
                <span className="ml-2 px-2 py-0.5 bg-slate-100 text-slate-500 rounded text-xs font-bold">{orgs.length}</span>
              </CardTitle>
              <div className="flex items-center gap-4">
                <div className="relative w-64 group">
                  <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 group-focus-within:text-indigo-600 transition-colors" />
                  <Input
                    type="text"
                    placeholder="Şirket ara..."
                    className="pl-9 h-10 bg-slate-100 border-none rounded-lg focus-visible:ring-indigo-600/20 transition-all"
                  />
                </div>
              </div>
            </CardHeader>
            <CardContent className="p-0">
              <div className="overflow-x-auto">
                <table className="w-full text-left border-collapse">
                  <thead>
                    <tr className="bg-slate-50/50 text-slate-500 text-xs font-bold uppercase tracking-wider">
                      <th className="px-8 py-4">Şirket Adı</th>
                      <th className="px-8 py-4">Ekip</th>
                      <th className="px-8 py-4">Kayıt Tarihi</th>
                      <th className="px-8 py-4">Durum</th>
                      <th className="px-8 py-4 text-right">İşlemler</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100">
                    {orgs.map((org) => (
                      <tr key={org.id} className="hover:bg-slate-50/50 transition-colors group">
                        <td className="px-8 py-5">
                          <div className="flex items-center gap-3">
                            <div className="w-10 h-10 rounded-xl bg-slate-100 flex items-center justify-center text-slate-600 font-bold group-hover:bg-indigo-50 group-hover:text-indigo-600 transition-colors">
                              {org.name.substring(0, 2).toUpperCase()}
                            </div>
                            <span className="font-bold text-slate-900">{org.name}</span>
                          </div>
                        </td>
                        <td className="px-8 py-5">
                          <div className="flex flex-col">
                            <span className="text-sm font-bold text-slate-700">{org.lawyerCount} Avukat</span>
                            <span className="text-xs text-slate-400 font-medium">{org.adminCount} Yönetici</span>
                          </div>
                        </td>
                        <td className="px-8 py-5 text-sm text-slate-500 font-medium">{org.createdAt}</td>
                        <td className="px-8 py-5">
                          <span
                            className={`
                              px-3 py-1 rounded-full text-[10px] font-extrabold uppercase tracking-widest border
                              ${org.status === 'ACTIVE'
                                ? 'bg-green-50 text-green-700 border-green-100'
                                : 'bg-orange-50 text-orange-700 border-orange-100'}
                            `}
                          >
                            {org.status === 'ACTIVE' ? 'Aktif' : 'Beklemede'}
                          </span>
                        </td>
                        <td className="px-8 py-5 text-right">
                          <Button variant="ghost" size="icon" className="rounded-xl text-slate-400 hover:text-indigo-600 hover:bg-indigo-50 transition-all" disabled title="Önizleme">
                            <MoreVertical size={20} />
                          </Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </CardContent>
          </Card>
        </div>
      </main>
    </div>
  );
}

function NavItem({ icon, label, active = false, disabled = false }) {
  return (
    <button
      type="button"
      disabled={disabled}
      className={`
        flex items-center gap-3 px-4 py-3.5 rounded-xl transition-all group w-full text-left
        ${active
          ? "bg-indigo-600 text-white shadow-lg shadow-indigo-600/20 font-bold"
          : "text-slate-400 hover:bg-slate-800 hover:text-white"}
        ${disabled ? "opacity-75 cursor-not-allowed" : ""}
      `}
    >
      <span className={active ? "text-white" : "text-slate-500 group-hover:text-indigo-400 transition-colors"}>
        {icon}
      </span>
      <span className="font-medium text-md">{label}</span>
      {disabled && (
        <span className="ml-auto text-[10px] uppercase tracking-wider text-amber-300">Önizleme</span>
      )}
    </button>
  );
}

function QuickStat({ label, value, icon }) {
  return (
    <Card className="p-6 rounded-3xl border-slate-200 shadow-sm hover:shadow-md transition-all">
      <div className="flex items-start justify-between mb-4">
        <div className="p-2 bg-slate-50 rounded-xl">
          {icon}
        </div>
      </div>
      <div>
        <h4 className="text-2xl font-extrabold text-slate-900">{value}</h4>
        <p className="text-slate-500 text-xs font-bold uppercase tracking-wider">{label}</p>
      </div>
    </Card>
  );
}
