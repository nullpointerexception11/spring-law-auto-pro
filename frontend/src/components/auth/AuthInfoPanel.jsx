import { Scale, ShieldCheck, Building2 } from "lucide-react";

export function AuthInfoPanel() {
  return (
    <div className="relative hidden md:flex flex-col justify-between p-12 bg-primary text-primary-foreground overflow-hidden">
      <div className="absolute inset-0 z-0">
        <img 
          src="/bg-office.png" 
          alt="Office" 
          className="w-full h-full object-cover"
        />
        <div className="absolute inset-0 bg-black/30" />
      </div>
      
      <div className="relative z-10">
        <div className="flex items-center gap-2 mb-8">
          <div className="p-2 bg-white/20 rounded-lg backdrop-blur-sm">
              <Scale className="w-8 h-8 text-primary-foreground" />
          </div>
          <span className="text-2xl font-bold tracking-tight">LawAuto Pro</span>
        </div>
        
        <h1 className="text-4xl font-extrabold leading-tight mb-6">
          Hukuk Süreçlerinizi <br />
          <span className="text-white/80">Dijitalleştirin.</span>
        </h1>
        <p className="text-lg text-white/70 max-w-md">
          Dava takibi, dilekçe yönetimi ve müvekkil ilişkilerini tek bir modern platformdan yönetin.
        </p>
      </div>

      <div className="relative z-10 flex gap-6 text-sm text-white/60">
        <div className="flex items-center gap-2 text-xs font-medium uppercase tracking-wider">
          <ShieldCheck className="w-4 h-4" />
          <span>Güvenli Altyapı</span>
        </div>
        <div className="flex items-center gap-2 text-xs font-medium uppercase tracking-wider">
          <Building2 className="w-4 h-4" />
          <span>Kurumsal Çözüm</span>
        </div>
      </div>
    </div>
  );
}
