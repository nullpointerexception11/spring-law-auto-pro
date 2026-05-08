import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { 
  ArrowLeft, 
  Calendar, 
  Gavel, 
  User, 
  FileText, 
  Clock, 
  ExternalLink,
  ShieldAlert,
  Loader2,
  History,
  Files,
  StickyNote,
  ChevronRight
} from 'lucide-react';
import api from '../../lib/api';

const TEST_ORG_ID = '11111111-1111-1111-1111-111111111111';

export default function MatterDetail() {
  const { matterId } = useParams();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('overview');

  // Backend'den dava detaylarını çekiyoruz
  const { data: matter, isLoading: isMatterLoading, error: matterError } = useQuery({
    queryKey: ['matter', matterId],
    queryFn: async () => {
      const response = await api.get(`/matters/${matterId}`, {
        params: { orgId: TEST_ORG_ID }
      });
      return response.data;
    }
  });

  // Zaman çizelgesi verilerini çekiyoruz
  const { data: timelineData, isLoading: isTimelineLoading } = useQuery({
    queryKey: ['matter-timeline', matterId],
    queryFn: async () => {
      const response = await api.get(`/matters/${matterId}/timeline`, {
        params: { orgId: TEST_ORG_ID }
      });
      return response.data;
    },
    enabled: activeTab === 'timeline'
  });

  if (isMatterLoading) {
    return (
      <div className="flex flex-col items-center justify-center h-[calc(100vh-200px)] space-y-4">
        <Loader2 className="h-10 w-10 animate-spin text-primary" />
        <p className="text-muted-foreground animate-pulse font-medium">Dava detayları hazırlanıyor...</p>
      </div>
    );
  }

  if (matterError || !matter) {
    return (
      <div className="flex flex-col items-center justify-center h-[calc(100vh-200px)] space-y-4 text-center">
        <div className="p-4 rounded-full bg-destructive/10">
          <ShieldAlert className="h-10 w-10 text-destructive" />
        </div>
        <div className="space-y-1">
          <h2 className="text-xl font-semibold">Dava bulunamadı</h2>
          <p className="text-muted-foreground">Erişmek istediğiniz dosya mevcut değil veya yetkiniz yok.</p>
        </div>
        <button 
          onClick={() => navigate('/matters')}
          className="mt-4 px-4 py-2 bg-secondary hover:bg-secondary/80 rounded-md transition-colors inline-flex items-center gap-2"
        >
          <ArrowLeft className="h-4 w-4" /> Listeye Dön
        </button>
      </div>
    );
  }

  const tabs = [
    { id: 'overview', label: 'Genel Bakış', icon: FileText },
    { id: 'timeline', label: 'Zaman Çizelgesi', icon: History },
    { id: 'documents', label: 'Evraklar', icon: Files },
    { id: 'notes', label: 'Notlar', icon: StickyNote },
  ];

  return (
    <div className="space-y-6 fade-enter-active">
      {/* Header / Navigation */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <button 
            onClick={() => navigate('/matters')}
            className="p-2 hover:bg-secondary rounded-full transition-colors text-muted-foreground hover:text-foreground"
            title="Geri Dön"
          >
            <ArrowLeft className="h-5 w-5" />
          </button>
          <div className="space-y-1">
            <h1 className="text-2xl font-bold tracking-tight">{matter.title}</h1>
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <span className="font-mono bg-secondary/50 px-2 py-0.5 rounded text-[11px] border border-border">
                {matter.referenceNumber || 'Dosya No Belirtilmedi'}
              </span>
              <span>•</span>
              <span className="flex items-center gap-1">
                <Clock className="h-3 w-3" />
                {new Date(matter.openedAt).toLocaleDateString('tr-TR')} tarihinde açıldı
              </span>
            </div>
          </div>
        </div>
        
        <div className="flex gap-2">
          <button className="px-4 py-2 text-sm font-medium border border-border rounded-lg hover:bg-secondary transition-colors">
            Dosyayı Düzenle
          </button>
          <button className="px-4 py-2 text-sm font-medium bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors shadow-sm">
            Yeni İşlem
          </button>
        </div>
      </div>

      {/* Tabs Navigation */}
      <div className="flex items-center border-b border-border overflow-x-auto no-scrollbar">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`flex items-center gap-2 px-6 py-3 text-sm font-medium transition-all relative whitespace-nowrap ${
                activeTab === tab.id 
                  ? 'text-primary' 
                  : 'text-muted-foreground hover:text-foreground'
              }`}
            >
              <Icon className="h-4 w-4" />
              {tab.label}
              {activeTab === tab.id && (
                <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-primary rounded-t-full" />
              )}
            </button>
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Content Area */}
        <div className="lg:col-span-2">
          {activeTab === 'overview' && (
            <div className="space-y-6">
              {/* Summary Card */}
              <div className="p-6 rounded-xl border border-border bg-card/50 backdrop-blur-sm shadow-sm space-y-4">
                <div className="flex items-center gap-2 font-semibold text-foreground/80 border-b border-border pb-3">
                  <FileText className="h-4 w-4 text-primary" />
                  Dosya Özeti
                </div>
                <p className="text-sm leading-relaxed text-muted-foreground">
                  {matter.summary || 'Bu dosya için henüz bir özet girilmemiş.'}
                </p>
              </div>

              {/* Parties List */}
              <div className="p-6 rounded-xl border border-border bg-card/50 backdrop-blur-sm shadow-sm">
                <div className="flex items-center justify-between mb-4 border-b border-border pb-3">
                  <div className="flex items-center gap-2 font-semibold text-foreground/80">
                    <User className="h-4 w-4 text-primary" />
                    Dava Tarafları
                  </div>
                  <span className="text-[10px] bg-primary/10 text-primary px-2 py-0.5 rounded-full font-bold">
                    {matter.parties?.length || 0} Taraf
                  </span>
                </div>
                
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {matter.parties?.map((party, idx) => (
                    <div key={idx} className="p-4 rounded-lg border border-border bg-secondary/20 flex items-start gap-3">
                      <div className="p-2 rounded-md bg-background shadow-sm">
                        <User className="h-4 w-4 text-muted-foreground" />
                      </div>
                      <div className="space-y-1 min-w-0">
                        <p className="text-sm font-semibold truncate">{party.fullName}</p>
                        <p className="text-[10px] text-muted-foreground uppercase tracking-wider font-bold">
                          {party.roleName}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {activeTab === 'timeline' && (
            <div className="p-6 rounded-xl border border-border bg-card/50 backdrop-blur-sm shadow-sm">
              <div className="flex items-center gap-2 font-semibold text-foreground/80 border-b border-border pb-4 mb-6">
                <History className="h-4 w-4 text-primary" />
                Dosya Geçmişi ve Akış
              </div>

              {isTimelineLoading ? (
                <div className="flex flex-col items-center justify-center py-12 gap-3">
                  <Loader2 className="h-8 w-8 animate-spin text-primary/50" />
                  <p className="text-sm text-muted-foreground italic">Zaman çizelgesi yükleniyor...</p>
                </div>
              ) : timelineData?.content?.length > 0 ? (
                <div className="relative space-y-8 before:absolute before:inset-0 before:ml-5 before:-translate-x-px before:h-full before:w-0.5 before:bg-gradient-to-b before:from-transparent before:via-border before:to-transparent">
                  {timelineData.content.map((item, idx) => (
                    <div key={item.id || idx} className="relative flex items-start gap-6 group">
                      <div className="absolute left-0 mt-1 flex h-10 w-10 items-center justify-center rounded-full border-4 border-background bg-secondary shadow-sm transition-colors group-hover:bg-primary group-hover:text-primary-foreground group-hover:border-primary/20">
                        <ChevronRight className="h-4 w-4" />
                      </div>
                      <div className="flex-1 ml-10 bg-secondary/30 p-4 rounded-lg border border-border/50 hover:border-primary/30 transition-colors">
                        <div className="flex items-center justify-between mb-1">
                          <span className="text-[10px] font-bold uppercase tracking-widest text-primary/70">
                            {item.action}
                          </span>
                          <span className="text-[10px] text-muted-foreground bg-background px-1.5 py-0.5 rounded border border-border">
                            {new Date(item.createdAt).toLocaleString('tr-TR')}
                          </span>
                        </div>
                        <p className="text-sm font-medium text-foreground mb-1">{item.summary}</p>
                        <div className="flex items-center gap-1.5 text-[11px] text-muted-foreground">
                          <User className="h-3 w-3" />
                          <span>{item.userFullName}</span>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-12 space-y-3">
                  <div className="mx-auto h-12 w-12 rounded-full bg-secondary flex items-center justify-center">
                    <History className="h-6 w-6 text-muted-foreground" />
                  </div>
                  <p className="text-sm text-muted-foreground italic">Bu dosya için henüz bir işlem geçmişi bulunmuyor.</p>
                </div>
              )}
            </div>
          )}

          {(activeTab === 'documents' || activeTab === 'notes') && (
            <div className="p-12 rounded-xl border-2 border-dashed border-border bg-secondary/5 flex flex-col items-center justify-center text-center gap-4">
              <div className="p-4 rounded-full bg-secondary/50">
                {activeTab === 'documents' ? <Files className="h-8 w-8 text-muted-foreground" /> : <StickyNote className="h-8 w-8 text-muted-foreground" />}
              </div>
              <div className="space-y-1">
                <h3 className="font-semibold text-foreground">{activeTab === 'documents' ? 'Evraklar' : 'Notlar'}</h3>
                <p className="text-sm text-muted-foreground max-w-xs">Bu bölüm henüz geliştirme aşamasındadır. Yakında burada dosya yönetimini görebileceksiniz.</p>
              </div>
            </div>
          )}
        </div>

        {/* Sidebar Info Area */}
        <div className="space-y-6">
          {/* Court Info Card */}
          <div className="p-6 rounded-xl border border-border bg-card shadow-md space-y-5">
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <div className="text-[10px] font-bold text-muted-foreground uppercase tracking-widest">Dava Durumu</div>
                <span className="px-2 py-0.5 rounded bg-success/10 text-success border border-success/20 text-[10px] font-bold uppercase tracking-wider">
                  {matter.status}
                </span>
              </div>
              
              <div className="space-y-4">
                <div className="flex items-start gap-3">
                  <Gavel className="h-5 w-5 text-muted-foreground shrink-0 mt-0.5" />
                  <div className="space-y-1">
                    <div className="text-[10px] font-bold text-muted-foreground uppercase tracking-widest">Mahkeme</div>
                    <div className="text-sm font-medium">{matter.courtName || 'Bilinmiyor'}</div>
                  </div>
                </div>

                <div className="flex items-start gap-3">
                  <Calendar className="h-5 w-5 text-muted-foreground shrink-0 mt-0.5" />
                  <div className="space-y-1">
                    <div className="text-[10px] font-bold text-muted-foreground uppercase tracking-widest">Esas No</div>
                    <div className="text-sm font-medium">{matter.caseNumber || 'Bilinmiyor'}</div>
                  </div>
                </div>

                <div className="flex items-start gap-3">
                  <User className="h-5 w-5 text-muted-foreground shrink-0 mt-0.5" />
                  <div className="space-y-1">
                    <div className="text-[10px] font-bold text-muted-foreground uppercase tracking-widest">Hakim</div>
                    <div className="text-sm font-medium">{matter.judgeName || 'Atanmamış'}</div>
                  </div>
                </div>
              </div>
            </div>

            <div className="pt-4 border-t border-border">
              <button className="w-full py-2.5 bg-primary text-primary-foreground rounded-lg text-sm font-medium hover:bg-primary/90 transition-colors flex items-center justify-center gap-2 shadow-lg shadow-primary/20">
                UYAP Dosyasına Git <ExternalLink className="h-4 w-4" />
              </button>
            </div>
          </div>

          {/* Quick Stats or Actions could go here */}
          <div className="p-5 rounded-xl border border-dashed border-border bg-secondary/10">
            <p className="text-[11px] text-muted-foreground text-center italic">
              Bu dosya en son {new Date(matter.updatedAt).toLocaleDateString('tr-TR')} tarihinde güncellendi.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
