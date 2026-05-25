import React from 'react';
import { Briefcase, Users, FileText, TrendingUp, ArrowRight, Plus, Sparkles } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';
import { useAuthStore } from '@/store/useAuthStore';
import { cn } from '@/lib/utils';

export default function DashboardPage() {
  const { user, role } = useAuthStore();

  return (
    <div className="space-y-8 fade-enter">
      {/* Welcome */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold text-foreground">
            Hoş geldin, {user?.fullName || (role === 'PLATFORM_ADMIN' ? 'Yönetici' : 'Avukat')}
          </h1>
          <p className="text-sm text-muted-foreground mt-0.5">
            Hukuk otomasyon sisteminize hoş geldiniz.
          </p>
        </div>
        <Button size="sm">
          <Plus className="w-4 h-4 mr-1.5" />
          Yeni Dava
        </Button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard icon={Briefcase} label="Aktif Dava" value="42" trend="+5" />
        <StatCard icon={FileText} label="Bekleyen" value="12" trend="3 kritik" />
        <StatCard icon={Users} label="Müvekkil" value="86" trend="+8" />
        <StatCard icon={Sparkles} label="AI Analiz" value="156" trend="12 bugün" />
      </div>

      {/* Content */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <Card className="lg:col-span-2">
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle className="text-sm font-medium">Son İşlemler</CardTitle>
            <Button variant="ghost" size="sm" className="text-xs gap-1">
              Tümü <ArrowRight className="w-3 h-3" />
            </Button>
          </CardHeader>
          <CardContent className="space-y-1">
            {[1, 2, 3].map((i) => (
              <div key={i} className="flex items-center justify-between py-3 px-3 rounded-md hover:bg-accent transition-colors cursor-pointer">
                <div className="flex items-center gap-3">
                  <div className="w-9 h-9 rounded-md bg-primary/10 flex items-center justify-center">
                    <FileText className="w-4 h-4 text-primary" />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-foreground">Dosya #2024/{i * 456}</p>
                    <p className="text-xs text-muted-foreground">Ağır Ceza Mahkemesi</p>
                  </div>
                </div>
                <span className="text-xs text-muted-foreground">15 Haz 2024</span>
              </div>
            ))}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-sm font-medium">AI Analizi</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-sm text-muted-foreground leading-relaxed">
              &ldquo;Yargıtay&apos;ın son işçilik alacakları kararını incelediniz mi? 
              Mevcut 5 dosyanız bu karardan etkilenebilir.&rdquo;
            </p>
            <Button size="sm" variant="outline" className="w-full mt-4 text-xs">
              İncele
            </Button>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}

function StatCard({ icon: Icon, label, value, trend }) {
  return (
    <div className="rounded-lg border border-border bg-card p-5">
      <div className="w-9 h-9 rounded-md bg-primary/10 flex items-center justify-center mb-4">
        <Icon className="w-4 h-4 text-primary" />
      </div>
      <p className="text-xs text-muted-foreground mb-1">{label}</p>
      <div className="flex items-baseline gap-2.5">
        <span className="text-2xl font-semibold text-foreground">{value}</span>
        <span className="text-[11px] text-muted-foreground">{trend}</span>
      </div>
    </div>
  );
}


