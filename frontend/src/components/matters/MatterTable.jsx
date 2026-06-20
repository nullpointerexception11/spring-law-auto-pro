import React, { memo, useCallback, useDeferredValue, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, ArrowRight, Loader2, Search, MoreHorizontal } from 'lucide-react';
import { useMatters } from '@/hooks/useMatters';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Card } from '@/components/ui/card';
import { ROUTES } from '@/lib/constants';
import { cn } from '@/lib/utils';

const STATUS_BADGE_CLASSES = {
  OPEN: 'bg-emerald-100 dark:bg-emerald-900/30 text-emerald-700 dark:text-emerald-300 border-emerald-200 dark:border-emerald-800',
  AKTIF: 'bg-emerald-100 dark:bg-emerald-900/30 text-emerald-700 dark:text-emerald-300 border-emerald-200 dark:border-emerald-800',
  PENDING: 'bg-amber-100 dark:bg-amber-900/30 text-amber-700 dark:text-amber-300 border-amber-200 dark:border-amber-800',
  BEKLEMEDE: 'bg-amber-100 dark:bg-amber-900/30 text-amber-700 dark:text-amber-300 border-amber-200 dark:border-amber-800',
  CLOSED: 'bg-muted text-muted-foreground border-border',
  KAPALI: 'bg-muted text-muted-foreground border-border',
};

const DATE_FORMATTER = new Intl.DateTimeFormat('tr-TR');

const formatDate = (value) => {
  if (!value) return '-';
  return DATE_FORMATTER.format(new Date(value));
};

const normalizeStatus = (status) => (status || 'UNKNOWN').toString().toUpperCase();

const StatusBadge = memo(function StatusBadge({ status }) {
  const normalized = normalizeStatus(status);

  return (
    <Badge
      variant="outline"
      className={cn(
        'rounded-md px-2 py-0.5 font-medium text-[10px] uppercase tracking-wider',
        STATUS_BADGE_CLASSES[normalized] || 'bg-muted text-muted-foreground border-border'
      )}
    >
      {status || 'Bilinmiyor'}
    </Badge>
  );
});

const MatterRow = memo(function MatterRow({ matter, onOpen }) {
  const handleOpen = useCallback(() => onOpen(matter.id), [matter.id, onOpen]);

  return (
    <tr
      onClick={handleOpen}
      className="cursor-pointer border-t border-border hover:bg-accent/40 transition-colors"
    >
      <td className="px-6 py-4 align-top">
        <span className="font-mono text-[10px] font-medium text-muted-foreground bg-muted px-2 py-1 rounded-md border border-border">
          {matter.displayId || matter.referenceNumber || 'N/A'}
        </span>
      </td>
      <td className="px-6 py-4 align-top">
        <div className="flex flex-col min-w-0">
          <span className="font-medium text-foreground truncate text-sm">{matter.title}</span>
          <span className="text-[10px] text-muted-foreground truncate">{matter.summary || 'Özet belirtilmemiş'}</span>
        </div>
      </td>
      <td className="px-6 py-4 align-top">
        <StatusBadge status={matter.status} />
      </td>
      <td className="px-6 py-4 align-top text-sm text-muted-foreground">
        {formatDate(matter.openedAt)}
      </td>
      <td className="px-6 py-4 align-top text-sm text-foreground">
        {matter.clientName || '-'}
      </td>
      <td className="px-6 py-4 align-top text-sm text-foreground">
        {matter.assignedLawyerName || '-'}
      </td>
      <td className="px-6 py-4 align-top text-right">
        <Button variant="ghost" className="h-8 w-8 p-0" onClick={(event) => event.stopPropagation()}>
          <MoreHorizontal className="h-4 w-4" />
        </Button>
      </td>
    </tr>
  );
});

export function MatterTable() {
  const navigate = useNavigate();
  const [pageIndex, setPageIndex] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [search, setSearch] = useState('');
  const deferredSearch = useDeferredValue(search);

  const { data: pageData, isLoading, error } = useMatters({ page: pageIndex, size: pageSize });
  const matters = pageData?.content || [];
  const totalPages = pageData?.totalPages || 0;
  const totalElements = pageData?.totalElements || 0;

  const filteredMatters = useMemo(() => {
    const term = deferredSearch.trim().toLowerCase();
    if (!term) return matters;

    return matters.filter((matter) => {
      return [
        matter.title,
        matter.summary,
        matter.clientName,
        matter.assignedLawyerName,
        matter.referenceNumber,
        matter.displayId,
      ]
        .filter(Boolean)
        .some((value) => String(value).toLowerCase().includes(term));
    });
  }, [deferredSearch, matters]);

  const handleMatterOpen = useCallback(
    (matterId) => {
      navigate(ROUTES.MATTER_DETAIL(matterId));
    },
    [navigate]
  );
  const goPrevious = useCallback(() => setPageIndex((current) => Math.max(current - 1, 0)), []);
  const goNext = useCallback(
    () => setPageIndex((current) => Math.min(current + 1, Math.max(totalPages - 1, 0))),
    [totalPages]
  );

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center h-96 space-y-4 rounded-xl border border-border bg-card">
        <div className="relative">
          <div className="h-12 w-12 rounded-xl bg-primary/10 border border-primary/20 animate-pulse" />
          <Loader2 className="h-6 w-6 animate-spin text-primary absolute inset-0 m-auto" />
        </div>
        <p className="text-sm text-muted-foreground animate-pulse uppercase tracking-widest">Veriler yükleniyor...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center h-96 space-y-4 rounded-xl border border-destructive/20 bg-destructive/5">
        <div className="h-12 w-12 rounded-xl bg-destructive/20 flex items-center justify-center text-destructive">
          <Search className="h-6 w-6" />
        </div>
        <div className="text-center">
          <p className="text-destructive font-medium">Veri bağlantısı kesildi</p>
          <p className="text-xs text-muted-foreground mt-1">Sunucuya ulaşılamıyor, lütfen sayfayı yenileyin.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
        <div className="relative w-full md:max-w-sm">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Dava başlığı, müvekkil veya kayıt no ile ara..."
            className="h-10 w-full rounded-lg border border-input bg-background pl-10 pr-4 text-sm focus:outline-none focus:ring-2 focus:ring-ring"
          />
        </div>

        <div className="flex items-center gap-3">
          <label className="text-xs font-medium text-muted-foreground uppercase tracking-wider">
            Sayfa boyutu
          </label>
          <select
            value={pageSize}
            onChange={(e) => {
              setPageIndex(0);
              setPageSize(Number(e.target.value));
            }}
            className="h-10 rounded-lg border border-input bg-background px-3 text-sm"
          >
            {[10, 20, 50].map((size) => (
              <option key={size} value={size}>
                {size}
              </option>
            ))}
          </select>
        </div>
      </div>

      <Card className="overflow-hidden border-border bg-card">
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead className="bg-muted/30">
              <tr>
                <th className="px-6 py-3 text-[10px] font-medium text-muted-foreground uppercase tracking-widest">Kayıt No</th>
                <th className="px-6 py-3 text-[10px] font-medium text-muted-foreground uppercase tracking-widest">Dava Başlığı</th>
                <th className="px-6 py-3 text-[10px] font-medium text-muted-foreground uppercase tracking-widest">Durum</th>
                <th className="px-6 py-3 text-[10px] font-medium text-muted-foreground uppercase tracking-widest">Açılış Tarihi</th>
                <th className="px-6 py-3 text-[10px] font-medium text-muted-foreground uppercase tracking-widest">Müvekkil</th>
                <th className="px-6 py-3 text-[10px] font-medium text-muted-foreground uppercase tracking-widest">Atanan Avukat</th>
                <th className="px-6 py-3 text-[10px] font-medium text-muted-foreground uppercase tracking-widest text-right">İşlemler</th>
              </tr>
            </thead>
            <tbody>
              {filteredMatters.map((matter) => (
                <MatterRow key={matter.id} matter={matter} onOpen={handleMatterOpen} />
              ))}

              {filteredMatters.length === 0 && (
                <tr>
                  <td colSpan={7} className="h-48 text-center">
                    <div className="flex flex-col items-center justify-center text-muted-foreground space-y-2">
                      <Search className="h-8 w-8 opacity-20" />
                      <p className="text-sm">Aradığınız kriterlere uygun dava bulunamadı.</p>
                    </div>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </Card>

      <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
        <p className="text-xs text-muted-foreground">
          {totalElements} toplam kayıt, sayfa {pageIndex + 1} / {Math.max(totalPages, 1)}
        </p>

        <div className="flex items-center gap-2">
          <Button variant="outline" size="sm" onClick={goPrevious} disabled={pageIndex === 0}>
            <ArrowLeft className="h-4 w-4 mr-1.5" />
            Önceki
          </Button>
          <Button variant="outline" size="sm" onClick={goNext} disabled={totalPages === 0 || pageIndex >= totalPages - 1}>
            Sonraki
            <ArrowRight className="h-4 w-4 ml-1.5" />
          </Button>
        </div>
      </div>
    </div>
  );
}
