import React from "react";
import { motion } from "framer-motion";
import { 
  BrainCircuit, 
  Zap, 
  Cpu, 
  Sparkles,
  ChevronDown
} from "lucide-react";
import { cn } from "@/lib/utils";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";

/**
 * Model Routing Indicator
 * 
 * Backend'deki ModelRouter'ın hangi modeli kullandığını görselleştirir.
 * GPT-4o (büyük) vs GPT-4o-mini (küçük) ayrımını gösterir.
 * 
 * Vercel Best Practices:
 * - rerender-simple-expression-in-memo: Basit primitive'ler için memo gerekmez
 * - js-early-exit: Erken çıkış
 */
export function ModelIndicator({ model, isStreaming }) {
  // Model bilgisi yoksa gösterme
  if (!model) return null;

  const isLargeModel = model.toLowerCase().includes("gpt-4o") && !model.toLowerCase().includes("mini");
  const modelLabel = isLargeModel ? "GPT-4o" : "GPT-4o-mini";
  const modelDescription = isLargeModel 
    ? "Büyük Model - Karmaşık analiz ve dilekçe yazımı için" 
    : "Küçük Model - Hızlı yanıt ve sınıflandırma için";

  return (
    <TooltipProvider delayDuration={200}>
      <Tooltip>
        <TooltipTrigger asChild>
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            className={cn(
              "inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[10px] font-bold transition-all",
              isLargeModel 
                ? "bg-violet-50 text-violet-700 border border-violet-200" 
                : "bg-emerald-50 text-emerald-700 border border-emerald-200",
              isStreaming && "animate-pulse"
            )}
          >
            {isLargeModel ? (
              <BrainCircuit className="size-3" />
            ) : (
              <Zap className="size-3" />
            )}
            <span>{modelLabel}</span>
            {isStreaming && (
              <span className="flex items-center gap-1 ml-0.5">
                <span className="size-1 rounded-full bg-current animate-bounce" />
                <span className="size-1 rounded-full bg-current animate-bounce" style={{ animationDelay: "0.1s" }} />
                <span className="size-1 rounded-full bg-current animate-bounce" style={{ animationDelay: "0.2s" }} />
              </span>
            )}
          </motion.div>
        </TooltipTrigger>
        <TooltipContent side="top" className="text-xs max-w-[200px]">
          <div className="font-bold text-foreground mb-1">Model Routing</div>
          <p className="text-muted-foreground">{modelDescription}</p>
          <div className="mt-1.5 pt-1.5 border-t border-slate-100 text-[10px] text-slate-400">
            {isLargeModel ? "🧠 Dilekçe yazımı, karmaşık analiz" : "⚡ Sınıflandırma, özetleme, basit Q&A"}
          </div>
        </TooltipContent>
      </Tooltip>
    </TooltipProvider>
  );
}

/**
 * Sadece model adını gösteren kompakt versiyon
 */
export function ModelBadge({ model, size = "sm" }) {
  if (!model) return null;

  const isLargeModel = model.toLowerCase().includes("gpt-4o") && !model.toLowerCase().includes("mini");

  return (
    <span className={cn(
      "inline-flex items-center gap-1 rounded-md font-bold border",
      size === "sm" ? "px-1.5 py-0.5 text-[9px]" : "px-2 py-1 text-[10px]",
      isLargeModel 
        ? "bg-violet-50 text-violet-700 border-violet-200" 
        : "bg-emerald-50 text-emerald-700 border-emerald-200"
    )}>
      {isLargeModel ? <BrainCircuit className="size-2.5" /> : <Zap className="size-2.5" />}
      {isLargeModel ? "GPT-4o" : "Mini"}
    </span>
  );
}
