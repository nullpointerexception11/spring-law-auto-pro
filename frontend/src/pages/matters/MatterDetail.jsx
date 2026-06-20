import React, { memo, useMemo, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useMatter } from '@/hooks/useMatters';
import { DocumentManager } from '@/components/matters/DocumentManager';
import {
  ArrowLeft,
  Calendar,
  Gavel,
  User,
  FileText,
  Clock,
  ShieldAlert,
  History,
  Files,
  StickyNote,
  Edit3,
  Hash,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';
import { ROUTES } from '@/lib/constants';
import { cn } from '@/lib/utils';

const TABS = [
  { id: 'overview', label: 'Genel Bakış', icon: FileText },
  { id: 'documents', label: 'Evraklar', icon: Files },
  { id: 'timeline', label: 'Zaman Çizelgesi', icon: History },
  { id: 'notes', label: 'Notlar', icon: StickyNote },
];

const STATUS_VARIANTS = {
  OPEN: 'success',
  PENDING: 'warning',
  CLOSED: 'secondary',
};

const formatOpenedDate = (value) => {
  if (!value) return '';
  return new Intl.DateTimeFormat('tr-TR').format(new Date(value));
};

function MatterDetailComponent() {
  const { matterId } = useParams();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('overview');

  const { data: matter, isLoading, error } = useMatter(matterId);

  const statusVariant = STATUS_VARIANTS[matter?.status] || 'default';
  const openedDate = useMemo(() => formatOpenedDate(matter?.openedAt), [matter?.openedAt]);

  if (isLoading) {
    return (
      <div className="space-y-8">
        <div className="flex items-center gap-4">
          <Skeleton className="h-10 w-10 rounded-lg" />
          <div className="space-y-2">
            <Skeleton className="h-7 w-64" />
            <Skeleton className="h-4 w-48" />
          </div>
        </div>
        <div className="flex gap-2">
          {[...Array(4)].map((_, i) => (
            <Skeleton key={i} className="h-10 w-28" />
          ))}
        </div>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 space-y-6">
            <Skeleton className="h-48 rounded-xl" />
            <Skeleton className="h-64 rounded-xl" />
          </div>
          <Skeleton className="h-64 rounded-xl" />
        </div>
      </div>
    );
  }

  if (error || !matter) {
    return (
      <div className="flex flex-col items-center justify-center h-[calc(100vh-200px)] space-y-4 text-center">
        <div className="p-4 rounded-2xl bg-destructive/10">
          <ShieldAlert className="h-10 w-10 text-destructive" />
        </div>
        <div className="space-y-1">
          <h2 className="text-xl font-semibold text-foreground">Dava bulunamadı</h2>
          <p className="text-sm text-muted-foreground">Erişmek istediğiniz dosya mevcut değil veya yetkiniz yok.</p>
        </div>
        <Button variant="outline" onClick={() => navigate(ROUTES.MATTERS)} className="mt-4">
          <ArrowLeft className="h-4 w-4 mr-2" /> Listeye Dön
        </Button>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="flex items-center gap-4">
          <button
            type="button"
            onClick={() => navigate(ROUTES.MATTERS)}
            className="h-10 w-10 flex items-center justify-center rounded-lg border border-border bg-card hover:bg-accent transition-colors text-muted-foreground"
          >
            <ArrowLeft className="h-4 w-4" />
          </button>
          <div>
            <div className="flex items-center gap-3 mb-1">
              <h1 className="text-xl font-semibold text-foreground">{matter.title}</h1>
              <Badge variant="outline" className="font-mono text-[10px]">
                {matter.referenceNumber || 'YENİ DOSYA'}
              </Badge>
            </div>
            <div className="flex items-center gap-3 text-xs text-muted-foreground">
              <span className="flex items-center gap-1.5">
                <Clock className="h-3.5 w-3.5" />
                {openedDate} tarihinde açıldı
              </span>
              <span className="h-1 w-1 rounded-full bg-muted-foreground/30" />
              <Badge variant={statusVariant} className="text-[10px] font-medium uppercase tracking-wider">
                {matter.status === 'OPEN'
                  ? 'Aktif'
                  : matter.status === 'PENDING'
                    ? 'Beklemede'
                    : matter.status === 'CLOSED'
                      ? 'Kapalı'
                      : matter.status}
              </Badge>
            </div>
          </div>
        </div>

        <div className="flex items-center gap-3">
          <Button variant="outline" size="sm">
            <Edit3 className="h-4 w-4 mr-2" /> Düzenle
          </Button>
        </div>
      </div>

      <div className="flex items-center border-b border-border">
        {TABS.map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;

          return (
            <button
              key={tab.id}
              type="button"
              onClick={() => setActiveTab(tab.id)}
              className={cn(
                'flex items-center gap-2 px-4 py-3 text-sm font-medium transition-colors relative border-b-2 -mb-[1px]',
                isActive ? 'text-primary border-primary' : 'text-muted-foreground border-transparent hover:text-foreground'
              )}
            >
              <Icon className="h-4 w-4" />
              {tab.label}
            </button>
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className={cn('space-y-6', activeTab === 'overview' ? 'lg:col-span-2' : 'lg:col-span-3')}>
          {activeTab === 'overview' && (
            <div className="space-y-6">
              <div className="rounded-xl border border-border bg-card p-6 space-y-4">
                <div className="flex items-center gap-3 font-medium text-foreground">
                  <div className="h-8 w-8 rounded-lg bg-primary/10 text-primary flex items-center justify-center">
                    <FileText className="h-4 w-4" />
                  </div>
                  Dosya Özeti
                </div>
                <p className="text-sm leading-relaxed text-muted-foreground bg-muted/50 p-4 rounded-lg">
                  {matter.summary || 'Bu dosya için henüz bir özet girilmemiş.'}
                </p>
                {matter.tags?.length > 0 && (
                  <div className="flex flex-wrap gap-2">
                    {matter.tags.map((tag) => (
                      <Badge key={tag} variant="secondary" className="text-[11px]">
                        {tag}
                      </Badge>
                    ))}
                  </div>
                )}
              </div>

              {matter.parties?.length > 0 && (
                <div className="rounded-xl border border-border bg-card p-6">
                  <div className="flex items-center gap-3 font-medium text-foreground mb-6">
                    <div className="h-8 w-8 rounded-lg bg-primary/10 text-primary flex items-center justify-center">
                      <User className="h-4 w-4" />
                    </div>
                    Dava Tarafları
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                    {matter.parties.map((party) => (
                      <div
                        key={party.id || `${party.fullName}-${party.roleName}`}
                        className="p-4 rounded-lg bg-muted/50 border border-border flex items-start gap-3 hover:border-primary/20 transition-colors"
                      >
                        <div className="h-9 w-9 rounded-lg bg-background border border-border flex items-center justify-center text-muted-foreground shrink-0">
                          <User className="h-4 w-4" />
                        </div>
                        <div>
                          <p className="text-sm font-medium text-foreground">{party.fullName}</p>
                          <p className="text-[11px] text-muted-foreground font-medium uppercase tracking-wider mt-0.5">
                            {party.roleName}
                          </p>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {matter.description && (
                <div className="rounded-xl border border-border bg-card p-6">
                  <div className="flex items-center gap-3 font-medium text-foreground mb-4">
                    <div className="h-8 w-8 rounded-lg bg-primary/10 text-primary flex items-center justify-center">
                      <Hash className="h-4 w-4" />
                    </div>
                    Açıklama
                  </div>
                  <p className="text-sm leading-relaxed text-muted-foreground">{matter.description}</p>
                </div>
              )}
            </div>
          )}

          {activeTab === 'documents' && <DocumentManager matterId={matterId} />}

          {activeTab === 'timeline' && (
            <div className="rounded-xl border border-border bg-card p-6">
              <div className="flex items-center gap-3 font-medium text-foreground mb-8">
                <div className="h-8 w-8 rounded-lg bg-primary/10 text-primary flex items-center justify-center">
                  <History className="h-4 w-4" />
                </div>
                Dosya Geçmişi
              </div>

              <div className="text-center py-12 space-y-3">
                <div className="h-16 w-16 mx-auto rounded-full bg-muted flex items-center justify-center text-muted-foreground">
                  <History className="h-8 w-8" />
                </div>
                <p className="text-sm text-muted-foreground">Geçmiş verileri hazırlanıyor...</p>
              </div>
            </div>
          )}

          {activeTab === 'notes' && (
            <div className="rounded-xl border border-dashed border-border bg-muted/30 p-12 flex flex-col items-center justify-center text-center gap-4">
              <div className="h-16 w-16 rounded-full bg-muted flex items-center justify-center text-muted-foreground">
                <StickyNote className="h-8 w-8" />
              </div>
              <div className="space-y-1">
                <h3 className="text-lg font-medium text-foreground">Dosya Notları</h3>
                <p className="text-sm text-muted-foreground max-w-xs">
                  Bu bölüm yakında aktif edilecektir. Notlarınızı buradan takip edebileceksiniz.
                </p>
              </div>
            </div>
          )}
        </div>

        {activeTab === 'overview' && (
          <div className="space-y-4">
            <div className="rounded-xl border border-border bg-card p-6 space-y-5">
              <div className="flex items-center justify-between">
                <span className="text-[10px] font-medium text-muted-foreground uppercase tracking-widest">
                  Duruşma / Karar
                </span>
                <Badge variant="success" className="text-[10px] font-medium rounded-md">
                  AKTİF DOSYA
                </Badge>
              </div>

              <div className="space-y-4">
                <div className="flex items-start gap-3">
                  <div className="h-9 w-9 rounded-lg bg-muted text-muted-foreground flex items-center justify-center shrink-0">
                    <Gavel className="h-4 w-4" />
                  </div>
                  <div className="space-y-0.5">
                    <p className="text-[10px] font-medium text-muted-foreground uppercase tracking-widest">Mahkeme</p>
                    <p className="text-sm font-medium text-foreground">{matter.courtName || 'Henüz Girilmedi'}</p>
                  </div>
                </div>

                <div className="flex items-start gap-3">
                  <div className="h-9 w-9 rounded-lg bg-muted text-muted-foreground flex items-center justify-center shrink-0">
                    <Calendar className="h-4 w-4" />
                  </div>
                  <div className="space-y-0.5">
                    <p className="text-[10px] font-medium text-muted-foreground uppercase tracking-widest">Dava No</p>
                    <p className="text-sm font-medium text-foreground">{matter.caseNumber || 'Atanmadı'}</p>
                  </div>
                </div>

                {matter.judgeName && (
                  <div className="flex items-start gap-3">
                    <div className="h-9 w-9 rounded-lg bg-muted text-muted-foreground flex items-center justify-center shrink-0">
                      <User className="h-4 w-4" />
                    </div>
                    <div className="space-y-0.5">
                      <p className="text-[10px] font-medium text-muted-foreground uppercase tracking-widest">Hakim</p>
                      <p className="text-sm font-medium text-foreground">{matter.judgeName}</p>
                    </div>
                  </div>
                )}

                {matter.decisionDate && (
                  <div className="flex items-start gap-3">
                    <div className="h-9 w-9 rounded-lg bg-muted text-muted-foreground flex items-center justify-center shrink-0">
                      <Calendar className="h-4 w-4" />
                    </div>
                    <div className="space-y-0.5">
                      <p className="text-[10px] font-medium text-muted-foreground uppercase tracking-widest">Karar Tarihi</p>
                      <p className="text-sm font-medium text-foreground">
                        {new Intl.DateTimeFormat('tr-TR').format(new Date(matter.decisionDate))}
                      </p>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default memo(MatterDetailComponent);
