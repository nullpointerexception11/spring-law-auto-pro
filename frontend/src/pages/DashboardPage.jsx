import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Briefcase, Users, FileText, Sparkles, 
  ArrowRight, Plus, Clock, Gavel, AlertCircle,
  Calendar, TrendingUp, Loader2
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { useAuthStore } from '@/store/useAuthStore';
import { useMatters } from '@/hooks/useMatters';
import { CreateMatterModal } from '@/components/matters/CreateMatterModal';
import { cn } from '@/lib/utils';

export default function DashboardPage() {
  const { user, role } = useAuthStore();
  const navigate = useNavigate();
  const [modalOpen, setModalOpen] = useState(false);
  const { data, isLoading } = useMatters();

  const matters = data || [];
  
  const activeCount = matters.filter(m => m.status === 'OPEN').length;
  const pendingCount = matters.filter(m => m.status === 'PENDING').length;
  const closedCount = matters.filter(m => m.status === 'CLOSED').length;
  const totalCount = matters.length;

  const recentMatters = [...matters]
    .sort((a, b) => new Date(b.openedAt) - new Date(a.openedAt))
    .slice(0, 5);

  return (
    <div className="space-y-8">
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
        <Button size="sm" onClick={() => setModalOpen(true)}>
          <Plus className="h-4 w-4 mr-1.5" />
          Yeni Dava
        </Button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {isLoading ? (
          [...Array(4)].map((_, i) => (
            <Card key={i}>
              <CardContent className="p-5">
                <Skeleton className="h-9 w-9 rounded-lg mb-4" />
                <Skeleton className="h-3 w-20 mb-2" />
                <Skeleton className="h-7 w-12" />
              </CardContent>
            </Card>
          ))
        ) : (
          <>
            <StatCard icon={Briefcase} label="Aktif Dava" value={activeCount} subtitle={`${totalCount} toplam`} />
            <StatCard icon={AlertCircle} label="Bekleyen" value={pendingCount} subtitle="İşlem bekliyor" variant="warning" />
            <StatCard icon={Gavel} label="Kapanan" value={closedCount} subtitle="Tamamlandı" variant="success" />
            <StatCard icon={Sparkles} label="AI Destek" value="Hazır" subtitle="Chat + RAG" variant="info" />
          </>
        )}
      </div>

      {/* Content */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Recent Matters */}
        <Card className="lg:col-span-2">
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle className="text-sm font-medium">Son Davalar</CardTitle>
            <Button variant="ghost" size="sm" className="text-xs gap-1" onClick={() => navigate('/matters')}>
              Tümü <ArrowRight className="h-3 w-3" />
            </Button>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <div className="space-y-2">
                {[...Array(3)].map((_, i) => (
                  <Skeleton key={i} className="h-16 w-full rounded-lg" />
                ))}
              </div>
            ) : recentMatters.length > 0 ? (
              <div className="space-y-1">
                {recentMatters.map((matter) => (
                  <div
                    key={matter.id}
                    onClick={() => navigate(`/matters/${matter.id}`)}
                    className="flex items-center justify-between py-3 px-3 rounded-lg hover:bg-accent transition-colors cursor-pointer group"
                  >
                    <div className="flex items-center gap-3 min-w-0">
                      <div className={cn(
                        'h-9 w-9 rounded-lg flex items-center justify-center shrink-0',
                        matter.status === 'OPEN' && 'bg-emerald-100 dark:bg-emerald-900/30 text-emerald-600 dark:text-emerald-400',
                        matter.status === 'PENDING' && 'bg-amber-100 dark:bg-amber-900/30 text-amber-600 dark:text-amber-400',
                        matter.status === 'CLOSED' && 'bg-muted text-muted-foreground',
                      )}>
                        <Briefcase className="h-4 w-4" />
                      </div>
                      <div className="min-w-0">
                        <p className="text-sm font-medium text-foreground truncate">{matter.title}</p>
                        <p className="text-xs text-muted-foreground">
                          {matter.clientName || matter.referenceNumber || '---'}
                        </p>
                      </div>
                    </div>
                    <div className="flex items-center gap-3 shrink-0">
                      <Badge variant={matter.status === 'OPEN' ? 'success' : matter.status === 'PENDING' ? 'warning' : 'secondary'} className="text-[10px]">
                        {matter.status === 'OPEN' ? 'Aktif' : matter.status === 'PENDING' ? 'Beklemede' : 'Kapalı'}
                      </Badge>
                      <span className="text-xs text-muted-foreground hidden md:block">
                        {new Date(matter.openedAt).toLocaleDateString('tr-TR', { day: 'numeric', month: 'short' })}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="text-center py-10 space-y-3">
                <div className="h-12 w-12 rounded-full bg-muted flex items-center justify-center mx-auto text-muted-foreground">
                  <Briefcase className="h-6 w-6" />
                </div>
                <div>
                  <p className="text-sm font-medium text-foreground">Henüz dava bulunmuyor</p>
                  <p className="text-xs text-muted-foreground mt-0.5">İlk davanızı oluşturarak başlayın.</p>
                </div>
                <Button size="sm" onClick={() => setModalOpen(true)}>
                  <Plus className="h-4 w-4 mr-1.5" /> İlk Davayı Oluştur
                </Button>
              </div>
            )}
          </CardContent>
        </Card>

        {/* Quick Actions & Info */}
        <div className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle className="text-sm font-medium">Hızlı İşlemler</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              {[
                { icon: Briefcase, label: 'Yeni Dava Oluştur', onClick: () => setModalOpen(true) },
                { icon: Calendar, label: 'Takvimi Görüntüle', onClick: () => navigate('/calendar') },
                { icon: FileText, label: 'Belgeleri Yönet', onClick: () => navigate('/documents') },
                { icon: Sparkles, label: 'AI Asistan', onClick: () => navigate('/ai') },
              ].map((action, i) => {
                const Icon = action.icon;
                return (
                  <button
                    key={i}
                    onClick={action.onClick}
                    className="flex items-center gap-3 w-full px-3 py-2.5 rounded-lg text-sm text-muted-foreground hover:text-foreground hover:bg-accent transition-colors"
                  >
                    <Icon className="h-4 w-4" />
                    {action.label}
                  </button>
                );
              })}
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="text-sm font-medium">AI Önerisi</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-sm text-muted-foreground leading-relaxed">
                Yargıtay&apos;ın son işçilik alacakları kararını incelediniz mi? 
                Mevcut dosyalarınız bu karardan etkilenebilir.
              </p>
              <Button size="sm" variant="outline" className="w-full mt-4 text-xs" onClick={() => navigate('/ai')}>
                AI ile İncele
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>

      <CreateMatterModal isOpen={modalOpen} onClose={() => setModalOpen(false)} />
    </div>
  );
}

function StatCard({ icon: Icon, label, value, subtitle, variant = 'default' }) {
  const variants = {
    default: 'bg-primary/10 text-primary',
    warning: 'bg-amber-100 dark:bg-amber-900/30 text-amber-600 dark:text-amber-400',
    success: 'bg-emerald-100 dark:bg-emerald-900/30 text-emerald-600 dark:text-emerald-400',
    info: 'bg-violet-100 dark:bg-violet-900/30 text-violet-600 dark:text-violet-400',
  };

  return (
    <Card>
      <CardContent className="p-5">
        <div className={cn('h-9 w-9 rounded-lg flex items-center justify-center mb-4', variants[variant])}>
          <Icon className="h-4 w-4" />
        </div>
        <p className="text-xs text-muted-foreground mb-1">{label}</p>
        <div className="flex items-baseline gap-2">
          <span className="text-2xl font-semibold text-foreground">{value}</span>
          <span className="text-[11px] text-muted-foreground">{subtitle}</span>
        </div>
      </CardContent>
    </Card>
  );
}
