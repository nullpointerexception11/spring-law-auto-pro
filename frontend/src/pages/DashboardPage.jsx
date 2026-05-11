import React from 'react';
import { 
  Briefcase, 
  Users, 
  FileText, 
  Calendar, 
  TrendingUp, 
  ArrowRight,
  Plus,
  Sparkles,
  Gavel
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { useAuthStore } from "@/store/useAuthStore";
import { motion } from "framer-motion";

export default function DashboardPage() {
  const { user, role } = useAuthStore();

  return (
    <div className="space-y-10 fade-enter-active">
      
      {/* Welcome Section */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-6">
        <div>
          <h1 className="text-4xl font-black text-slate-900 mb-2 tracking-tight">
            Hoş Geldin, <span className="text-indigo-600">{user?.fullName || (role === 'PLATFORM_ADMIN' ? 'Sistem Yöneticisi' : 'Avukat')}</span>
          </h1>
          <p className="text-slate-500 text-lg font-medium">
            {role === 'PLATFORM_ADMIN' 
              ? 'Tüm sistem ve organizasyonlar genelinde tam yetkiye sahipsiniz.' 
              : 'Hukuk otomasyon sisteminizde her şey kontrol altında.'}
          </p>
        </div>
        <div className="flex items-center gap-3">
          <Button className="h-12 px-6 rounded-2xl bg-indigo-600 hover:bg-indigo-700 text-white font-bold shadow-lg shadow-indigo-100 transition-all hover:scale-[1.02] active:scale-95">
            <Plus className="h-5 w-5 mr-2" /> Yeni Dava Kaydı
          </Button>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard 
          icon={<Briefcase className="h-6 w-6" />} 
          label="Aktif Davalar" 
          value="42" 
          trend="+5 bu ay" 
          color="indigo" 
        />
        <StatCard 
          icon={<Gavel className="h-6 w-6" />} 
          label="Beklenen Kararlar" 
          value="12" 
          trend="3 kritik" 
          color="amber" 
        />
        <StatCard 
          icon={<Users className="h-6 w-6" />} 
          label="Müvekkil Sayısı" 
          value="86" 
          trend="+8 yeni" 
          color="emerald" 
        />
        <StatCard 
          icon={<Sparkles className="h-6 w-6" />} 
          label="AI Analizleri" 
          value="156" 
          trend="Bugün: 12" 
          color="violet" 
        />
      </div>

      {/* Main Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Recent Matters */}
        <Card className="lg:col-span-2 rounded-[40px] border-slate-100 shadow-sm bg-white overflow-hidden">
          <CardHeader className="p-8 pb-4 flex flex-row justify-between items-center">
            <CardTitle className="text-xl font-bold text-slate-900">Son İşlemler</CardTitle>
            <Button variant="ghost" className="text-indigo-600 font-bold hover:bg-indigo-50 rounded-xl px-4">
              Tümünü Gör <ArrowRight className="h-4 w-4 ml-2" />
            </Button>
          </CardHeader>
          <CardContent className="p-8 pt-4">
            <div className="space-y-4">
              {[1, 2, 3].map((i) => (
                <div key={i} className="flex items-center justify-between p-5 hover:bg-slate-50/80 rounded-3xl transition-all group cursor-pointer border border-transparent hover:border-slate-100">
                  <div className="flex items-center gap-4">
                    <div className="w-12 h-12 bg-indigo-50 text-indigo-600 rounded-2xl flex items-center justify-center shadow-sm">
                      <FileText className="h-5 w-5" />
                    </div>
                    <div>
                      <p className="font-bold text-slate-900">Dosya #2024/{i * 456}</p>
                      <p className="text-[11px] text-slate-400 font-bold uppercase tracking-wider">Ağır Ceza Mahkemesi</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-6">
                    <div className="hidden md:block text-right">
                      <p className="text-xs font-bold text-slate-700">Duruşma Tarihi</p>
                      <p className="text-[11px] text-slate-400 font-medium">15 Haziran 2024</p>
                    </div>
                    <span className="h-8 px-4 flex items-center bg-emerald-50 text-emerald-700 text-[10px] font-black rounded-xl border border-emerald-100 uppercase tracking-widest">
                      Açık
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* AI Insight Card */}
        <Card className="rounded-[40px] border-none bg-gradient-to-br from-indigo-600 to-violet-700 shadow-xl shadow-indigo-200 p-8 text-white">
          <div className="h-12 w-12 rounded-2xl bg-white/20 backdrop-blur-md flex items-center justify-center mb-6">
            <Sparkles className="h-6 w-6 text-white" />
          </div>
          <h3 className="text-2xl font-bold mb-4">Günün AI Analizi</h3>
          <p className="text-indigo-100 text-sm leading-relaxed mb-8 font-medium">
            "Yargıtay'ın son işçilik alacakları kararını incelediniz mi? Mevcut 5 dosyanız bu karardan doğrudan etkilenebilir."
          </p>
          <Button className="w-full h-12 bg-white text-indigo-600 font-bold rounded-2xl hover:bg-indigo-50 shadow-lg shadow-black/10">
            Analizi İncele
          </Button>
        </Card>
      </div>
    </div>
  );
}

function StatCard({ icon, label, value, trend, color }) {
  const colors = {
    indigo: "bg-indigo-50 text-indigo-600 shadow-indigo-100",
    amber: "bg-amber-50 text-amber-600 shadow-amber-100",
    emerald: "bg-emerald-50 text-emerald-600 shadow-emerald-100",
    violet: "bg-violet-50 text-violet-600 shadow-violet-100",
  };

  return (
    <motion.div whileHover={{ y: -5 }} className="bg-white p-7 rounded-[32px] border border-slate-50 shadow-sm hover:shadow-md transition-all">
      <div className={cn("h-12 w-12 rounded-2xl flex items-center justify-center mb-6", colors[color])}>
        {icon}
      </div>
      <p className="text-slate-400 text-xs font-bold mb-1 uppercase tracking-widest">{label}</p>
      <div className="flex items-baseline justify-between">
        <h4 className="text-3xl font-black text-slate-900">{value}</h4>
        <span className="text-[10px] font-black text-slate-500 bg-slate-50 px-2.5 py-1 rounded-lg border border-slate-100 uppercase tracking-widest">
          {trend}
        </span>
      </div>
    </motion.div>
  );
}

function cn(...classes) {
  return classes.filter(Boolean).join(' ');
}
