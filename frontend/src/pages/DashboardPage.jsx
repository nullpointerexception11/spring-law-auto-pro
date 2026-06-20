import React, { memo, useCallback, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Briefcase,
  FileText,
  Sparkles,
  ArrowRight,
  Plus,
  Gavel,
  AlertCircle,
  Calendar,
  TrendingUp,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { useAuthStore } from '@/store/useAuthStore';
import { useMatters } from '@/hooks/useMatters';
import { CreateMatterModal } from '@/components/matters/CreateMatterModal';
import { ROUTES } from '@/lib/constants';
import { cn } from '@/lib/utils';

function DashboardPageComponent() {
  const { user, role } = useAuthStore();
  const navigate = useNavigate();
  const [modalOpen, setModalOpen] = useState(false);
  const { data, isLoading } = useMatters({ page: 0, size: 100 });

  const matters = data?.content || [];

  const openCreateMatterModal = useCallback(() => setModalOpen(true), []);
  const closeCreateMatterModal = useCallback(() => setModalOpen(false), []);
  const goToMatters = useCallback(() => navigate(ROUTES.MATTERS), [navigate]);
  const goToCalendar = useCallback(() => navigate(ROUTES.CALENDAR), [navigate]);
  const goToDocuments = useCallback(() => navigate(ROUTES.DOCUMENTS), [navigate]);
  const goToAi = useCallback(() => navigate(ROUTES.AI), [navigate]);
  const goToMatterDetail = useCallback(
    (matterId) => {
      navigate(ROUTES.MATTER_DETAIL(matterId));
    },
    [navigate]
  );

  const {
    activeCount,
    pendingCount,
    closedCount,
    totalCount,
    recentMatters,
    firstOpenedAt,
  } = useMemo(() => {
    let active = 0;
    let pending = 0;
    let closed = 0;
    let earliest = null;

    for (const matter of matters) {
      if (matter.status === 'OPEN') active += 1;
      else if (matter.status === 'PENDING') pending += 1;
      else if (matter.status === 'CLOSED') closed += 1;

      if (matter.openedAt) {
        const openedAt = new Date(matter.openedAt).getTime();
        if (Number.isFinite(openedAt) && (earliest === null || openedAt < earliest)) {
          earliest = openedAt;
        }
      }
    }

    return {
      activeCount: active,
      pendingCount: pending,
      closedCount: closed,
      totalCount: matters.length,
      recentMatters: [...matters].sort((a, b) => new Date(b.openedAt) - new Date(a.openedAt)).slice(0, 5),
      firstOpenedAt: earliest ? new Date(earliest) : null,
    };
  }, [matters]);

  const quickActions = useMemo(
    () => [
      { icon: Briefcase, label: 'Yeni Dava', onClick: openCreateMatterModal },
      { icon: Calendar, label: 'Takvim', onClick: goToCalendar, preview: true },
      { icon: FileText, label: 'Belgeler', onClick: goToDocuments },
      { icon: Sparkles, label: 'AI Asistan', onClick: goToAi },
    ],
    [goToAi, goToCalendar, goToDocuments, openCreateMatterModal]
  );

  const heroStats = useMemo(
    () => [
      { label: 'Aktif dava', value: activeCount, tone: 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400' },
      { label: 'Bekleyen', value: pendingCount, tone: 'bg-amber-500/10 text-amber-600 dark:text-amber-400' },
      { label: 'Kapanan', value: closedCount, tone: 'bg-slate-500/10 text-slate-600 dark:text-slate-300' },
    ],
    [activeCount, closedCount, pendingCount]
  );

  return (
    <div className="space-y-8">
      <section className="hero-shell overflow-hidden rounded-[1.75rem] border border-border/70 bg-card/90">
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_top_left,_hsl(var(--primary)/0.15),_transparent_35%),radial-gradient(circle_at_top_right,_hsl(var(--warning)/0.10),_transparent_30%)]" />
        <div className="absolute inset-0 opacity-[0.12] [background-image:linear-gradient(to_right,hsl(var(--foreground)/0.07)_1px,transparent_1px),linear-gradient(to_bottom,hsl(var(--foreground)/0.07)_1px,transparent_1px)] [background-size:32px_32px]" />

        <div className="relative grid gap-6 p-6 md:p-8 xl:grid-cols-[1.4fr_0.9fr] xl:items-stretch">
          <div className="space-y-6">
            <div className="flex flex-wrap items-center gap-3">
              <span className="inline-flex items-center rounded-full border border-primary/15 bg-primary/5 px-3 py-1 text-[10px] font-semibold uppercase tracking-[0.24em] text-primary">
                Operasyon paneli
              </span>
              <span className="inline-flex items-center gap-2 rounded-full border border-border bg-background/70 px-3 py-1 text-[10px] font-medium uppercase tracking-[0.22em] text-muted-foreground">
                <TrendingUp className="h-3.5 w-3.5" />
                Canlı özet
              </span>
            </div>

            <div className="space-y-3">
              <h1 className="text-3xl font-semibold tracking-tight text-foreground md:text-4xl">
                Hoş geldin, {user?.fullName || (role === 'PLATFORM_ADMIN' ? 'Yönetici' : 'Avukat')}
              </h1>
              <p className="max-w-2xl text-sm leading-relaxed text-muted-foreground md:text-base">
                Bugün davalarınızı, belgelerinizi ve karar akışınızı tek bakışta izleyebilirsiniz.
                Önce genel fotoğraf, sonra ayrıntı.
              </p>
            </div>

            <div className="grid gap-3 sm:grid-cols-3">
              {heroStats.map((item) => (
                <div
                  key={item.label}
                  className="rounded-2xl border border-border/70 bg-background/80 p-4 backdrop-blur-sm"
                >
                  <p className="text-[10px] font-semibold uppercase tracking-[0.24em] text-muted-foreground">
                    {item.label}
                  </p>
                  <p className={cn('mt-2 text-2xl font-semibold', item.tone)}>{item.value}</p>
                </div>
              ))}
            </div>

            <div className="flex flex-wrap items-center gap-3">
              <Button size="sm" onClick={openCreateMatterModal} className="rounded-full px-4">
                <Plus className="h-4 w-4 mr-1.5" />
                Yeni Dava
              </Button>
              <Button size="sm" variant="outline" onClick={goToMatters} className="rounded-full px-4">
                Davalar
              </Button>
            </div>
          </div>

          <div className="grid gap-4">
            <Card className="border-border/70 bg-background/80 backdrop-blur-sm">
              <CardHeader className="pb-3">
                <CardTitle className="text-sm font-medium">Bugünün durumu</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex items-center justify-between rounded-2xl border border-border bg-card p-4">
                  <div>
                    <p className="text-xs uppercase tracking-[0.22em] text-muted-foreground">Toplam dosya</p>
                    <p className="mt-1 text-2xl font-semibold text-foreground">{totalCount}</p>
                  </div>
                  <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-primary/10 text-primary">
                    <Briefcase className="h-5 w-5" />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div className="rounded-2xl border border-border bg-card p-3">
                    <p className="text-[10px] uppercase tracking-[0.22em] text-muted-foreground">İlk dosya</p>
                    <p className="mt-2 text-sm font-medium text-foreground">
                      {firstOpenedAt ? firstOpenedAt.toLocaleDateString('tr-TR', { day: 'numeric', month: 'short', year: 'numeric' }) : 'Yok'}
                    </p>
                  </div>
                  <div className="rounded-2xl border border-border bg-card p-3">
                    <p className="text-[10px] uppercase tracking-[0.22em] text-muted-foreground">AI destek</p>
                    <p className="mt-2 text-sm font-medium text-foreground">Hazır</p>
                  </div>
                </div>
              </CardContent>
            </Card>

            <Card className="border-border/70 bg-background/80 backdrop-blur-sm">
              <CardHeader className="pb-3">
                <CardTitle className="text-sm font-medium">Hızlı işlemler</CardTitle>
              </CardHeader>
              <CardContent className="space-y-2">
                {quickActions.map((action) => {
                  const Icon = action.icon;

                  return (
                    <button
                      key={action.label}
                      type="button"
                      onClick={action.onClick}
                      disabled={action.preview}
                      className={cn(
                        'flex items-center gap-3 w-full rounded-2xl px-3 py-3 text-sm transition-colors',
                        action.preview
                          ? 'cursor-not-allowed border border-amber-200/70 bg-amber-50/60 text-amber-700/70 dark:border-amber-900/50 dark:bg-amber-950/20 dark:text-amber-300/80'
                          : 'border border-transparent text-muted-foreground hover:border-border hover:bg-accent/70 hover:text-foreground'
                      )}
                      title={action.preview ? 'Önizleme' : undefined}
                    >
                      <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-background/80 text-foreground ring-1 ring-border/70">
                        <Icon className="h-4 w-4" />
                      </span>
                      <span className="flex items-center gap-2">
                        {action.label}
                        {action.preview && (
                          <Badge variant="outline" className="h-5 border-amber-200 bg-background px-1.5 text-[9px] text-amber-700 dark:border-amber-900 dark:text-amber-300">
                            Önizleme
                          </Badge>
                        )}
                      </span>
                    </button>
                  );
                })}
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
        {isLoading ? (
          [...Array(4)].map((_, i) => (
            <Card key={i}>
              <CardContent className="p-5">
                <Skeleton className="mb-4 h-9 w-9 rounded-lg" />
                <Skeleton className="mb-2 h-3 w-20" />
                <Skeleton className="h-7 w-12" />
              </CardContent>
            </Card>
          ))
        ) : (
          <>
            <StatCard icon={Briefcase} label="Aktif dava" value={activeCount} subtitle={`${totalCount} toplam`} />
            <StatCard icon={AlertCircle} label="Bekleyen" value={pendingCount} subtitle="İşlem bekliyor" variant="warning" />
            <StatCard icon={Gavel} label="Kapanan" value={closedCount} subtitle="Tamamlandı" variant="success" />
            <StatCard icon={Sparkles} label="AI destek" value="Hazır" subtitle="Chat + RAG" variant="info" />
          </>
        )}
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-[1.35fr_0.65fr]">
        <Card className="border-border/70 bg-card/90 backdrop-blur-sm">
          <CardHeader className="flex flex-row items-center justify-between">
            <div>
              <CardTitle className="text-sm font-medium">Son davalar</CardTitle>
              <p className="mt-1 text-xs text-muted-foreground">Son açılan dosyalara hızlı erişim.</p>
            </div>
            <Button variant="ghost" size="sm" className="gap-1 text-xs" onClick={goToMatters}>
              Tümü <ArrowRight className="h-3 w-3" />
            </Button>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <div className="space-y-2">
                {[...Array(3)].map((_, i) => (
                  <Skeleton key={i} className="h-16 w-full rounded-2xl" />
                ))}
              </div>
            ) : recentMatters.length > 0 ? (
              <div className="space-y-2">
                {recentMatters.map((matter) => (
                  <button
                    key={matter.id}
                    type="button"
                    onClick={() => goToMatterDetail(matter.id)}
                    className="group flex w-full items-center justify-between rounded-2xl border border-transparent bg-muted/30 px-4 py-3 text-left transition-all hover:border-border hover:bg-background/80"
                  >
                    <div className="flex min-w-0 items-center gap-3">
                      <div
                        className={cn(
                          'flex h-10 w-10 shrink-0 items-center justify-center rounded-2xl',
                          matter.status === 'OPEN' && 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400',
                          matter.status === 'PENDING' && 'bg-amber-500/10 text-amber-600 dark:text-amber-400',
                          matter.status === 'CLOSED' && 'bg-slate-500/10 text-slate-600 dark:text-slate-300'
                        )}
                      >
                        <Briefcase className="h-4 w-4" />
                      </div>
                      <div className="min-w-0">
                        <p className="truncate text-sm font-medium text-foreground">{matter.title}</p>
                        <p className="truncate text-xs text-muted-foreground">
                          {matter.clientName || matter.referenceNumber || '---'}
                        </p>
                      </div>
                    </div>
                    <div className="flex shrink-0 items-center gap-3">
                      <Badge
                        variant={matter.status === 'OPEN' ? 'success' : matter.status === 'PENDING' ? 'warning' : 'secondary'}
                        className="rounded-full px-2.5 text-[10px]"
                      >
                        {matter.status === 'OPEN' ? 'Aktif' : matter.status === 'PENDING' ? 'Beklemede' : 'Kapalı'}
                      </Badge>
                      <span className="hidden text-xs text-muted-foreground md:block">
                        {new Date(matter.openedAt).toLocaleDateString('tr-TR', { day: 'numeric', month: 'short' })}
                      </span>
                    </div>
                  </button>
                ))}
              </div>
            ) : (
              <div className="space-y-3 py-10 text-center">
                <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-muted text-muted-foreground">
                  <Briefcase className="h-6 w-6" />
                </div>
                <div>
                  <p className="text-sm font-medium text-foreground">Henüz dava bulunmuyor</p>
                  <p className="mt-1 text-xs text-muted-foreground">İlk davanızı oluşturarak başlayın.</p>
                </div>
                <Button size="sm" onClick={openCreateMatterModal}>
                  <Plus className="h-4 w-4 mr-1.5" /> İlk Davayı Oluştur
                </Button>
              </div>
            )}
          </CardContent>
        </Card>

        <Card className="border-border/70 bg-card/90 backdrop-blur-sm">
          <CardHeader>
            <CardTitle className="text-sm font-medium">AI önerisi</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="rounded-2xl border border-violet-200/70 bg-gradient-to-br from-violet-50 to-background p-4 dark:border-violet-900/40 dark:from-violet-950/30 dark:to-background">
              <div className="flex items-center gap-2 text-[10px] font-semibold uppercase tracking-[0.24em] text-violet-700 dark:text-violet-300">
                <Sparkles className="h-3.5 w-3.5" />
                Akıllı içgörü
              </div>
              <p className="mt-3 text-sm leading-relaxed text-foreground">
                Yargıtay&apos;ın son işçilik alacakları kararını incelediniz mi?
                Mevcut dosyalarınız için risk veya fırsat oluşturabilir.
              </p>
              <Button size="sm" variant="outline" className="mt-4 w-full rounded-full text-xs" onClick={goToAi}>
                AI ile incele
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>

      <CreateMatterModal isOpen={modalOpen} onClose={closeCreateMatterModal} />
    </div>
  );
}

const StatCard = memo(function StatCard({ icon: Icon, label, value, subtitle, variant = 'default' }) {
  const variants = {
    default: 'bg-primary/10 text-primary',
    warning: 'bg-amber-100 dark:bg-amber-900/30 text-amber-600 dark:text-amber-400',
    success: 'bg-emerald-100 dark:bg-emerald-900/30 text-emerald-600 dark:text-emerald-400',
    info: 'bg-violet-100 dark:bg-violet-900/30 text-violet-600 dark:text-violet-400',
  };

  return (
    <Card className="border-border/70 bg-card/90 backdrop-blur-sm">
      <CardContent className="p-5">
        <div className={cn('mb-4 flex h-9 w-9 items-center justify-center rounded-xl', variants[variant])}>
          <Icon className="h-4 w-4" />
        </div>
        <p className="mb-1 text-xs text-muted-foreground">{label}</p>
        <div className="flex items-baseline gap-2">
          <span className="text-2xl font-semibold text-foreground">{value}</span>
          <span className="text-[11px] text-muted-foreground">{subtitle}</span>
        </div>
      </CardContent>
    </Card>
  );
});

export default memo(DashboardPageComponent);
