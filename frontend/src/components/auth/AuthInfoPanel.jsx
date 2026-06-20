import React, { memo } from "react";
import { Scale, ShieldCheck, Building2 } from "lucide-react";

function AuthInfoPanelComponent() {
  return (
    <div className="hero-shell relative hidden md:flex flex-col justify-between p-12 bg-primary text-primary-foreground overflow-hidden">
      <div className="absolute inset-0 z-0">
        <img
          src="/bg-office.png"
          alt="Office"
          className="h-full w-full object-cover"
        />
        <div className="absolute inset-0 bg-slate-950/45" />
      </div>

      <div className="relative z-10">
        <div className="mb-8 flex items-center gap-3">
          <div className="rounded-xl bg-white/15 p-2 backdrop-blur-md ring-1 ring-white/15">
            <Scale className="h-8 w-8 text-primary-foreground" />
          </div>
          <div>
            <span className="block text-[10px] uppercase tracking-[0.28em] text-white/55">
              Corporate legal OS
            </span>
            <span className="block text-2xl font-bold tracking-tight">LawAuto Pro</span>
          </div>
        </div>

        <h1 className="mb-6 text-4xl font-extrabold leading-tight">
          Hukuk Süreçlerinizi <br />
          <span className="text-white/80">Dijitalleştirin.</span>
        </h1>
        <p className="max-w-md text-lg text-white/70">
          Dava takibi, dilekçe yönetimi ve müvekkil ilişkilerini tek bir modern platformdan yönetin.
        </p>
      </div>

      <div className="relative z-10 flex flex-wrap gap-3 text-sm text-white/60">
        <div className="flex items-center gap-2 text-xs font-medium uppercase tracking-wider">
          <ShieldCheck className="h-4 w-4" />
          <span>Güvenli Altyapı</span>
        </div>
        <div className="flex items-center gap-2 text-xs font-medium uppercase tracking-wider">
          <Building2 className="h-4 w-4" />
          <span>Kurumsal Çözüm</span>
        </div>
        <div className="flex items-center gap-2 text-xs font-medium uppercase tracking-wider">
          <span className="h-1.5 w-1.5 rounded-full bg-emerald-400" />
          <span>Gerçek zamanlı akış</span>
        </div>
      </div>
    </div>
  );
}

export const AuthInfoPanel = memo(AuthInfoPanelComponent);
