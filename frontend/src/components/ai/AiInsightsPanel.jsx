import React from "react";
import { motion } from "framer-motion";
import { 
  Sparkles, 
  TrendingUp, 
  AlertTriangle, 
  Info,
  X,
  BrainCircuit,
  Lightbulb,
  Gavel,
  FileSearch
} from "lucide-react";
import { cn } from "@/lib/utils";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { ScrollArea } from "@/components/ui/scroll-area";

/**
 * AI Insights Panel
 * 
 * Backend AI'nın sunduğu analiz ve öngörüleri gösterir.
 * Her insight bir hukuki konu, öneri veya uyarı içerir.
 * 
 * Vercel Best Practices:
 * - js-early-exit: Erken çıkış
 * - rerender-simple-expression-in-memo: Basit primitive'ler
 * - patterns-explicit-variants: Açık varyant tipleri
 */
export function AiInsightsPanel({ isOpen, onClose }) {
  if (!isOpen) return null;

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: 10 }}
      className="rounded-2xl border border-border bg-card shadow-sm overflow-hidden"
    >
      {/* Header */}
      <div className="flex items-center justify-between px-5 py-4 border-b border-border">
        <div className="flex items-center gap-3">
          <div className="flex items-center justify-center size-9 rounded-xl bg-violet-50 text-violet-600">
            <BrainCircuit className="size-5" />
          </div>
          <div>
            <h3 className="font-bold text-foreground text-sm">AI Analizleri</h3>
            <p className="text-[10px] text-muted-foreground font-medium uppercase tracking-wider">
              Hukuki Öngörüler ve Öneriler
            </p>
          </div>
        </div>
        <button
          onClick={onClose}
          className="flex items-center justify-center size-8 rounded-lg hover:bg-slate-100 text-muted-foreground transition-colors"
        >
          <X className="size-4" />
        </button>
      </div>

      {/* Insights List */}
      <ScrollArea className="max-h-[400px]">
        <div className="p-5 flex flex-col gap-3">
          {INSIGHTS.map((insight, index) => (
            <InsightCard key={index} insight={insight} index={index} />
          ))}
        </div>
      </ScrollArea>
    </motion.div>
  );
}

/**
 * Bireysel insight kartı
 * patterns-explicit-variants: Varyant tipleri ile farklı görünümler
 */
function InsightCard({ insight, index }) {
  const { type, title, description, action, priority } = insight;

  const typeConfig = {
    analysis: {
      icon: TrendingUp,
      color: "bg-blue-50 text-blue-600 border-blue-200",
      label: "Analiz"
    },
    suggestion: {
      icon: Lightbulb,
      color: "bg-amber-50 text-amber-600 border-amber-200",
      label: "Öneri"
    },
    warning: {
      icon: AlertTriangle,
      color: "bg-red-50 text-red-600 border-red-200",
      label: "Uyarı"
    },
    info: {
      icon: Info,
      color: "bg-slate-50 text-slate-600 border-slate-200",
      label: "Bilgi"
    },
    legal: {
      icon: Gavel,
      color: "bg-indigo-50 text-indigo-600 border-indigo-200",
      label: "Hukuki"
    },
    search: {
      icon: FileSearch,
      color: "bg-emerald-50 text-emerald-600 border-emerald-200",
      label: "Araştırma"
    }
  };

  const config = typeConfig[type] || typeConfig.info;
  const Icon = config.icon;

  return (
    <motion.div
      initial={{ opacity: 0, x: -10 }}
      animate={{ opacity: 1, x: 0 }}
      transition={{ delay: index * 0.05 }}
      className="group rounded-xl border border-slate-100 p-4 hover:border-slate-200 hover:shadow-sm transition-all cursor-pointer bg-white"
    >
      <div className="flex items-start gap-3">
        <div className={cn(
          "flex items-center justify-center size-9 rounded-xl shrink-0 border",
          config.color
        )}>
          <Icon className="size-4" />
        </div>
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2">
            <span className="font-bold text-foreground text-sm">{title}</span>
            {priority === "high" && (
              <Badge variant="destructive" className="text-[8px] font-bold uppercase tracking-widest px-1.5 py-0.5">
                Öncelikli
              </Badge>
            )}
            <Badge variant="secondary" className="text-[8px] font-bold">
              {config.label}
            </Badge>
          </div>
          <p className="text-xs text-slate-500 mt-1 leading-relaxed">
            {description}
          </p>
          {action && (
            <Button
              variant="ghost"
              size="sm"
              className="mt-2 h-7 px-2.5 rounded-lg text-[10px] font-bold text-primary hover:bg-primary/10"
            >
              {action}
            </Button>
          )}
        </div>
      </div>
    </motion.div>
  );
}

/**
 * Mock insights - gerçek backend'den gelecek
 */
const INSIGHTS = [
  {
    type: "legal",
    title: "Yargıtay Kararı Güncellemesi",
    description: "İşçilik alacaklarıyla ilgili son Yargıtay Hukuk Genel Kurulu kararı, kıdem tazminatı hesaplamasında yeni bir içtihat oluşturdu. 5 aktif dosyanız etkilenebilir.",
    priority: "high",
    action: "Etkilenen Dosyaları Gör"
  },
  {
    type: "analysis",
    title: "Dava Trend Analizi",
    description: "Bu ay iş hukuku davalarında %23 artış var. En sık karşılaşılan uyuşmazlık konusu: fazla mesai ücreti alacakları.",
    priority: "medium",
    action: "Detaylı Analiz"
  },
  {
    type: "suggestion",
    title: "Dilekçe İyileştirme Önerisi",
    description: "Dava #2024/456 için hazırlanan dilekçede Yargıtay 9. Hukuk Dairesi'nin 2023/1452 E. sayılı kararına atıf yapılabilir.",
    priority: "normal",
    action: "Öneriyi Uygula"
  },
  {
    type: "search",
    title: "Yeni Mevzuat Taraması",
    description: "Ticaret Kanunu'nda yapılan değişiklikler limited şirket ortaklarının sorumluluğunu etkiliyor. 12 müvekkiliniz bu değişiklikten haberdar edilmeli.",
    priority: "normal",
    action: "Müvekkilleri Bilgilendir"
  },
  {
    type: "info",
    title: "AI Kullanım İstatistiği",
    description: "Bu hafta AI asistanı 47 kez kullanıldı. En çok iş hukuku ve icra hukuku alanlarında sorgulama yapıldı.",
    priority: "low"
  }
];
