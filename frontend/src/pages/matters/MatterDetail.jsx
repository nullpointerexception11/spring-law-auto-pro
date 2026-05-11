import React, { useState } from 'react';
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
  ExternalLink,
  ShieldAlert,
  Loader2,
  History,
  Files,
  StickyNote,
  ChevronRight,
  Edit3
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { motion } from 'framer-motion';

export default function MatterDetail() {
  const { matterId } = useParams();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('overview');

  const { data: matter, isLoading, error } = useMatter(matterId);

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center h-[calc(100vh-200px)] space-y-4">
        <Loader2 className="h-10 w-10 animate-spin text-indigo-600" />
        <p className="text-slate-500 animate-pulse font-medium">Dava detayları hazırlanıyor...</p>
      </div>
    );
  }

  if (error || !matter) {
    return (
      <div className="flex flex-col items-center justify-center h-[calc(100vh-200px)] space-y-4 text-center">
        <div className="p-4 rounded-3xl bg-red-50">
          <ShieldAlert className="h-10 w-10 text-red-600" />
        </div>
        <div className="space-y-1">
          <h2 className="text-xl font-bold text-slate-900">Dava bulunamadı</h2>
          <p className="text-sm text-slate-500">Erişmek istediğiniz dosya mevcut değil veya yetkiniz yok.</p>
        </div>
        <Button 
          variant="outline"
          onClick={() => navigate('/matters')}
          className="mt-4 rounded-xl"
        >
          <ArrowLeft className="h-4 w-4 mr-2" /> Listeye Dön
        </Button>
      </div>
    );
  }

  const tabs = [
    { id: 'overview', label: 'Genel Bakış', icon: FileText },
    { id: 'documents', label: 'Evraklar', icon: Files },
    { id: 'timeline', label: 'Zaman Çizelgesi', icon: History },
    { id: 'notes', label: 'Notlar', icon: StickyNote },
  ];

  return (
    <div className="space-y-8 fade-enter-active">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div className="flex items-center gap-5">
          <button 
            onClick={() => navigate('/matters')}
            className="h-12 w-12 flex items-center justify-center bg-white border border-slate-200 rounded-2xl hover:bg-slate-50 transition-all shadow-sm text-slate-500"
          >
            <ArrowLeft className="h-5 w-5" />
          </button>
          <div>
            <div className="flex items-center gap-3 mb-1">
              <h1 className="text-2xl font-bold text-slate-900">{matter.title}</h1>
              <Badge variant="outline" className="rounded-lg bg-indigo-50/50 text-indigo-700 border-indigo-100 font-mono text-[10px]">
                {matter.referenceNumber || 'YENİ DOSYA'}
              </Badge>
            </div>
            <div className="flex items-center gap-4 text-xs text-slate-500 font-medium">
              <span className="flex items-center gap-1.5">
                <Clock className="h-3.5 w-3.5 text-slate-400" />
                {new Date(matter.openedAt).toLocaleDateString('tr-TR')} tarihinde açıldı
              </span>
              <span className="h-1 w-1 rounded-full bg-slate-300" />
              <span className="text-indigo-600 font-bold uppercase tracking-wider">{matter.status}</span>
            </div>
          </div>
        </div>
        
        <div className="flex items-center gap-3">
          <Button variant="outline" className="rounded-2xl h-11 px-6 shadow-sm">
            <Edit3 className="h-4 w-4 mr-2" /> Düzenle
          </Button>
          <Button className="rounded-2xl h-11 px-6 bg-indigo-600 hover:bg-indigo-700 shadow-lg shadow-indigo-100">
            Yeni İşlem
          </Button>
        </div>
      </div>

      {/* Navigation Tabs */}
      <div className="flex items-center border-b border-slate-200 gap-2">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={cn(
                "flex items-center gap-2 px-6 py-4 text-sm font-bold transition-all relative",
                isActive ? "text-indigo-600" : "text-slate-500 hover:text-slate-900"
              )}
            >
              <Icon className={cn("h-4 w-4", isActive ? "text-indigo-600" : "text-slate-400")} />
              {tab.label}
              {isActive && (
                <motion.div 
                  layoutId="activeTab"
                  className="absolute bottom-0 left-0 right-0 h-1 bg-indigo-600 rounded-t-full" 
                />
              )}
            </button>
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 space-y-8">
          {activeTab === 'overview' && (
            <div className="space-y-8">
              <div className="bg-white p-8 rounded-[32px] border border-slate-100 shadow-sm space-y-6">
                <div className="flex items-center gap-3 text-slate-900 font-bold">
                  <div className="h-8 w-8 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center">
                    <FileText className="h-4 w-4" />
                  </div>
                  Dosya Özeti
                </div>
                <p className="text-sm leading-relaxed text-slate-600 font-medium bg-slate-50/50 p-6 rounded-2xl border border-slate-50">
                  {matter.summary || 'Bu dosya için henüz bir özet girilmemiş.'}
                </p>
              </div>

              <div className="bg-white p-8 rounded-[32px] border border-slate-100 shadow-sm">
                <div className="flex items-center justify-between mb-8">
                  <div className="flex items-center gap-3 text-slate-900 font-bold">
                    <div className="h-8 w-8 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center">
                      <User className="h-4 w-4" />
                    </div>
                    Dava Tarafları
                  </div>
                </div>
                
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {matter.parties?.map((party, idx) => (
                    <div key={idx} className="p-5 rounded-2xl bg-slate-50 border border-slate-100 flex items-start gap-4 hover:border-indigo-200 transition-all group">
                      <div className="h-10 w-10 rounded-xl bg-white shadow-sm flex items-center justify-center text-slate-400 group-hover:text-indigo-600 transition-colors">
                        <User className="h-5 w-5" />
                      </div>
                      <div>
                        <p className="text-sm font-bold text-slate-900">{party.fullName}</p>
                        <p className="text-[10px] text-slate-500 font-bold uppercase tracking-wider mt-1">
                          {party.roleName}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {activeTab === 'documents' && (
            <DocumentManager matterId={matterId} />
          )}

          {activeTab === 'timeline' && (
            <div className="bg-white p-8 rounded-[32px] border border-slate-100 shadow-sm">
              <div className="flex items-center gap-3 text-slate-900 font-bold mb-10">
                <div className="h-8 w-8 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center">
                  <History className="h-4 w-4" />
                </div>
                Dosya Geçmişi
              </div>
              
              <div className="text-center py-12 space-y-4">
                <div className="h-16 w-16 mx-auto rounded-full bg-slate-50 flex items-center justify-center text-slate-300">
                  <History className="h-8 w-8" />
                </div>
                <p className="text-sm text-slate-500 font-medium italic">Geçmiş verileri hazırlanıyor...</p>
              </div>
            </div>
          )}

          {activeTab === 'notes' && (
            <div className="p-16 rounded-[40px] border border-dashed border-slate-200 bg-slate-50/50 flex flex-col items-center justify-center text-center gap-6">
              <div className="h-20 w-20 rounded-full bg-white shadow-sm flex items-center justify-center text-slate-300">
                <StickyNote className="h-10 w-10" />
              </div>
              <div className="space-y-2">
                <h3 className="text-lg font-bold text-slate-900">Dosya Notları</h3>
                <p className="text-sm text-slate-500 max-w-xs font-medium">Bu bölüm yakında aktif edilecektir. Notlarınızı buradan takip edebileceksiniz.</p>
              </div>
            </div>
          )}
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          <div className="bg-white p-8 rounded-[32px] border border-slate-100 shadow-sm space-y-8">
            <div className="flex items-center justify-between">
              <span className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Duruşma / Karar</span>
              <Badge className="bg-emerald-50 text-emerald-700 border-emerald-100 hover:bg-emerald-50 rounded-lg px-3 py-1 font-bold text-[10px]">
                AKTİF DOSYA
              </Badge>
            </div>
            
            <div className="space-y-6">
              <div className="flex items-start gap-4">
                <div className="h-10 w-10 rounded-xl bg-slate-50 text-slate-400 flex items-center justify-center shrink-0">
                  <Gavel className="h-5 w-5" />
                </div>
                <div className="space-y-1">
                  <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Mahkeme</p>
                  <p className="text-sm font-bold text-slate-900">{matter.courtName || 'Henüz Girilmedi'}</p>
                </div>
              </div>

              <div className="flex items-start gap-4">
                <div className="h-10 w-10 rounded-xl bg-slate-50 text-slate-400 flex items-center justify-center shrink-0">
                  <Calendar className="h-5 w-5" />
                </div>
                <div className="space-y-1">
                  <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Dava No</p>
                  <p className="text-sm font-bold text-slate-900">{matter.caseNumber || 'Atanmadı'}</p>
                </div>
              </div>
            </div>

            <Button className="w-full h-12 bg-slate-900 hover:bg-slate-800 text-white rounded-2xl font-bold shadow-lg shadow-slate-100">
              UYAP Entegrasyonu <ExternalLink className="h-4 w-4 ml-2" />
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}

function cn(...classes) {
  return classes.filter(Boolean).join(' ');
}
