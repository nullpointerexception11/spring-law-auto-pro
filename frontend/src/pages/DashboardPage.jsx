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

export default function DashboardPage() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);

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
    <div className="flex h-screen bg-slate-50 font-sans overflow-hidden">
      
      {/* Sidebar */}
      <aside className="w-72 bg-white border-r border-slate-200 flex flex-col">
        <div className="p-8">
          <div className="flex items-center gap-2 mb-12">
            <div className="p-2 bg-primary rounded-lg text-white">
              <LayoutDashboard size={20} />
            </div>
            <span className="text-xl font-bold text-slate-900 tracking-tight">LawAuto Pro</span>
          </div>

          <nav className="space-y-1">
            <NavItem icon={<LayoutDashboard size={20} />} label="Panel" active />
            <NavItem icon={<Briefcase size={20} />} label="Dosyalar" />
            <NavItem icon={<Users size={20} />} label="Müvekkiller" />
            <NavItem icon={<FileText size={20} />} label="Dilekçeler" />
            <NavItem icon={<Calendar size={20} />} label="Takvim" />
          </nav>
        </div>

        <div className="mt-auto p-8 border-t border-slate-100">
          <nav className="space-y-1">
            <NavItem icon={<Settings size={20} />} label="Ayarlar" />
            <button 
              onClick={handleLogout}
              className="w-full flex items-center gap-3 px-4 py-3 text-slate-500 hover:text-red-600 hover:bg-red-50 rounded-xl transition-all"
            >
              <LogOut size={20} />
              <span className="font-medium">Çıkış Yap</span>
            </button>
          </nav>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col overflow-hidden">
        
        {/* Header */}
        <header className="h-20 bg-white border-b border-slate-200 flex items-center justify-between px-12">
          <div className="flex items-center bg-slate-100 px-4 py-2 rounded-xl w-96 group focus-within:bg-white focus-within:ring-2 focus-within:ring-primary/20 border border-transparent focus-within:border-primary/20 transition-all">
            <Search size={18} className="text-slate-400 group-focus-within:text-primary" />
            <input 
              type="text" 
              placeholder="Dosya, müvekkil veya dilekçe ara..." 
              className="bg-transparent border-none focus:ring-0 text-sm ml-3 w-full"
            />
          </div>

          <div className="flex items-center gap-6">
            <button className="relative p-2 text-slate-500 hover:bg-slate-100 rounded-lg transition-all">
              <Bell size={20} />
              <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full border-2 border-white"></span>
            </button>
            <div className="flex items-center gap-3 pl-6 border-l border-slate-200">
              <div className="text-right">
                <p className="text-sm font-bold text-slate-900">Av. Orhan Yılmaz</p>
                <p className="text-xs text-slate-500">Kıdemli Ortak</p>
              </div>
              <div className="w-10 h-10 bg-gradient-to-br from-primary to-primary/60 rounded-xl flex items-center justify-center text-white font-bold">
                OY
              </div>
            </div>
          </div>
        </header>

        {/* Content Area */}
        <div className="flex-1 overflow-y-auto p-12 space-y-12">
          
          {/* Welcome Section */}
          <div className="flex justify-between items-end">
            <div>
              <h1 className="text-4xl font-extrabold text-slate-900 mb-2">Hoş Geldiniz, Orhan</h1>
              <p className="text-slate-500 text-lg">Bugün için 3 duruşmanız ve 5 bekleyen dilekçeniz var.</p>
            </div>
            <button className="flex items-center gap-2 px-6 py-3 bg-primary text-white rounded-xl font-bold shadow-lg shadow-primary/20 hover:bg-primary/90 hover:scale-[1.02] transition-all">
              <Plus size={20} />
              <span>Yeni Dosya Ekle</span>
            </button>
          </div>

          {/* Stats Grid */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <StatCard label="Aktif Dosyalar" value="24" trend="+3 bu ay" />
            <StatCard label="Tamamlanan Dilekçeler" value="128" trend="+12 bu hafta" />
            <StatCard label="Müvekkil Sayısı" value="86" trend="+5 yeni" />
          </div>

          {/* Table / List Placeholder */}
          <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
            <div className="p-8 border-b border-slate-100 flex justify-between items-center">
              <h3 className="text-xl font-bold text-slate-900">Son Dosyalar</h3>
              <button className="text-primary font-bold text-sm flex items-center gap-1 hover:underline">
                Tümünü Gör <ChevronRight size={16} />
              </button>
            </div>
            <div className="p-8">
              <div className="space-y-4">
                {[1, 2, 3].map((i) => (
                  <div key={i} className="flex items-center justify-between p-4 hover:bg-slate-50 rounded-2xl transition-all group cursor-pointer border border-transparent hover:border-slate-100">
                    <div className="flex items-center gap-4">
                      <div className="w-12 h-12 bg-blue-50 text-blue-600 rounded-xl flex items-center justify-center font-bold">
                        D{i}
                      </div>
                      <div>
                        <p className="font-bold text-slate-900">Dosya #2024/{i * 123}</p>
                        <p className="text-sm text-slate-500">Müvekkil: Ahmet Yılmaz</p>
                      </div>
                    </div>
                    <div className="flex items-center gap-8">
                      <div className="text-right">
                        <p className="text-sm font-medium text-slate-900">Ağır Ceza Mahkemesi</p>
                        <p className="text-xs text-slate-500">Duruşma: 15.05.2024</p>
                      </div>
                      <span className="px-3 py-1 bg-green-50 text-green-600 text-xs font-bold rounded-full">
                        AÇIK
                      </span>
                      <ChevronRight size={20} className="text-slate-300 group-hover:text-primary transition-colors" />
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>

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
        flex items-center gap-3 px-4 py-3 rounded-xl transition-all group
        ${active 
          ? "bg-primary text-white shadow-lg shadow-primary/20" 
          : "text-slate-500 hover:bg-slate-100 hover:text-slate-900"}
      `}
    >
      <span className={active ? "text-white" : "text-slate-400 group-hover:text-primary transition-colors"}>
        {icon}
      </span>
      <span className="font-medium">{label}</span>
    </a>
  );
}

function StatCard({ label, value, trend }) {
  return (
    <div className="bg-white p-8 rounded-3xl border border-slate-200 shadow-sm hover:shadow-md transition-all">
      <p className="text-slate-500 text-sm font-medium mb-1">{label}</p>
      <div className="flex items-baseline gap-3">
        <h4 className="text-3xl font-extrabold text-slate-900">{value}</h4>
        <span className="text-xs font-bold text-green-600 bg-green-50 px-2 py-0.5 rounded-full">{trend}</span>
      </div>
    </div>
  );
}
