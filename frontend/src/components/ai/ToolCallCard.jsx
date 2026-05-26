import React from "react";
import { motion } from "framer-motion";
import { 
  Search, 
  FileText, 
  ClipboardCheck, 
  Users,
  AlertTriangle,
  CheckCircle2,
  Loader2,
  ExternalLink
} from "lucide-react";
import { cn } from "@/lib/utils";
import { Badge } from "@/components/ui/badge";

/**
 * Tool Call Card
 * 
 * Backend'deki Tool Calling (Agent) sisteminin görsel temsili.
 * AI hangi tool'u çağırdıysa onu kullanıcıya gösterir.
 * 
 * Desteklenen Tool'lar:
 * - searchLawTool: Hukuki kaynak arama
 * - createMatterDraft: Dava taslağı oluşturma (Human-in-the-Loop)
 * - summarizeMatter: Dava özetleme
 * - identifyParties: Tarafları belirleme
 * - createMatter (legacy): Direk kayıt
 * 
 * Vercel Best Practices:
 * - architecture-avoid-boolean-props: Boolean prop yerine variant kullanımı
 * - js-early-exit: Erken çıkış
 * - rerender-memo: Gereksiz re-render'ları engelle
 */
export function ToolCallCard({ toolCall, onResultClick }) {
  if (!toolCall) return null;

  const { name, status, input, output, duration } = toolCall;

  const toolConfig = TOOL_CONFIG[name] || TOOL_CONFIG.UNKNOWN;

  const Icon = toolConfig.icon;
  const isRunning = status === "running";
  const isSuccess = status === "success";
  const isError = status === "error";
  const isHumanLoop = name === "createMatterDraft";

  return (
    <motion.div
      initial={{ opacity: 0, y: -5, scale: 0.98 }}
      animate={{ opacity: 1, y: 0, scale: 1 }}
      className={cn(
        "rounded-2xl border p-4 transition-all",
        isRunning && "border-primary/50 bg-primary/5",
        isSuccess && "border-emerald-200 bg-emerald-50/30",
        isError && "border-destructive/50 bg-destructive/5",
        isHumanLoop && isSuccess && "border-amber-200 bg-amber-50/30"
      )}
    >
      {/* Tool Header */}
      <div className="flex items-start justify-between gap-3">
        <div className="flex items-center gap-3 min-w-0">
          <div className={cn(
            "flex items-center justify-center size-9 rounded-xl shrink-0",
            isRunning && "bg-primary/20 text-primary",
            isSuccess && !isHumanLoop && "bg-emerald-100 text-emerald-600",
            isHumanLoop && isSuccess && "bg-amber-100 text-amber-600",
            isError && "bg-destructive/20 text-destructive"
          )}>
            {isRunning ? (
              <Loader2 className="size-4 animate-spin" />
            ) : isSuccess ? (
              <CheckCircle2 className="size-4" />
            ) : (
              <AlertTriangle className="size-4" />
            )}
          </div>
          <div className="min-w-0">
            <div className="flex items-center gap-2">
              <Icon className="size-4 text-muted-foreground shrink-0" />
              <span className="font-bold text-foreground text-sm truncate">
                {toolConfig.label}
              </span>
              {isHumanLoop && (
                <Badge variant="warning" className="text-[8px] font-bold uppercase tracking-widest px-1.5 py-0.5">
                  Onay Gerekli
                </Badge>
              )}
            </div>
            <div className="flex items-center gap-2 mt-0.5">
              <span className={cn(
                "text-[9px] font-bold uppercase tracking-wider",
                isRunning && "text-primary",
                isSuccess && "text-emerald-600",
                isError && "text-destructive"
              )}>
                {isRunning ? "Çalışıyor..." : isSuccess ? "Başarılı" : "Hata"}
              </span>
              {duration && (
                <>
                  <span className="text-slate-300">·</span>
                  <span className="text-[9px] text-muted-foreground">{duration}ms</span>
                </>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Input/Output Details */}
      {input && (
        <div className="mt-3 bg-white/60 rounded-xl border border-slate-100 p-3">
          {input.query && (
            <p className="text-[11px] text-slate-600">
              <span className="font-bold text-foreground">Sorgu:</span> {input.query}
            </p>
          )}
          {input.title && (
            <p className="text-[11px] text-slate-600">
              <span className="font-bold text-foreground">{isHumanLoop ? "Taslak:" : "Başlık:"}</span> {input.title}
            </p>
          )}
          {input.sourceType && (
            <p className="text-[11px] text-slate-600">
              <span className="font-bold text-foreground">Kaynak Türü:</span> {input.sourceType}
            </p>
          )}
        </div>
      )}

      {/* Human-in-the-Loop Warning */}
      {isHumanLoop && isSuccess && (
        <div className="mt-3 flex items-start gap-2 bg-amber-50 border border-amber-200 rounded-xl p-3">
          <AlertTriangle className="size-4 text-amber-500 shrink-0 mt-0.5" />
          <div>
            <p className="text-[11px] font-bold text-amber-800">İnsan Onayı Gerekli</p>
            <p className="text-[10px] text-amber-700 mt-0.5">
              Bu bir TASLAKTIR. Kaydedilmesi için UI üzerinden onaylayın.
            </p>
          </div>
        </div>
      )}

      {/* Result Button */}
      {output && onResultClick && (
        <button
          onClick={() => onResultClick(output)}
          className="mt-3 w-full flex items-center justify-center gap-1.5 h-8 rounded-xl bg-slate-50 border border-slate-200 text-[10px] font-bold text-slate-600 hover:bg-slate-100 transition-colors"
        >
          <ExternalLink className="size-3" />
          Sonucu Görüntüle
        </button>
      )}
    </motion.div>
  );
}

const TOOL_CONFIG = {
  searchLawTool: {
    label: "Hukuki Kaynak Arama",
    icon: Search,
    description: "Kanun, yönetmelik, içtihatlarda anlamsal arama"
  },
  createMatterDraft: {
    label: "Dava Taslağı Oluşturma",
    icon: FileText,
    description: "AI destekli dava taslağı (onay gerekli)"
  },
  summarizeMatter: {
    label: "Dava Özetleme",
    icon: ClipboardCheck,
    description: "Mevcut davanın özetini çıkar"
  },
  identifyParties: {
    label: "Tarafları Belirleme",
    icon: Users,
    description: "Davacı ve davalı taraflarını belirle"
  },
  createMatter: {
    label: "Dava Oluşturma (Legacy)",
    icon: FileText,
    description: "Sisteme yeni dava kaydı"
  },
  UNKNOWN: {
    label: "Tool Çağrısı",
    icon: ExternalLink,
    description: "Bilinmeyen araç"
  }
};
