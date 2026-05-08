import { useState } from "react";
import { api } from "@/api/client";
import { useNavigate } from "react-router-dom";
import { Scale, ShieldCheck, Mail, Lock, Building2, User } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent } from "@/components/ui/card";
import { 
  Select, 
  SelectContent, 
  SelectItem, 
  SelectTrigger, 
  SelectValue 
} from "@/components/ui/select";

export default function LoginPage() {
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({
    orgId: "",
    email: "",
    fullName: "",
    password: "",
    role: "LAWYER",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    
    try {
      const endpoint = isLogin ? "/auth/login" : "/auth/register";
      const { data } = await api.post(endpoint, formData);
      localStorage.setItem("token", data.token);
      navigate("/dashboard");
    } catch (err) {
      setError(err?.response?.data?.error || "Bir hata oluştu. Lütfen bilgilerinizi kontrol edin.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50 p-4 font-sans">
      <Card className="max-w-5xl w-full grid md:grid-cols-2 shadow-2xl border-none overflow-hidden animate-in p-0">
        
        {/* Left Side: Design & Info */}
        <div className="relative hidden md:flex flex-col justify-between p-12 bg-primary text-white overflow-hidden">
          <div className="absolute inset-0 z-0">
            <img 
              src="/law_office_background_1778228742292.png" 
              alt="Office" 
              className="w-full h-full object-cover opacity-20"
            />
            <div className="absolute inset-0 bg-gradient-to-br from-primary via-primary/90 to-transparent" />
          </div>
          
          <div className="relative z-10">
            <div className="flex items-center gap-2 mb-8">
              <div className="p-2 bg-white/20 rounded-lg backdrop-blur-sm">
                <Scale className="w-8 h-8 text-white" />
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
            <div className="flex items-center gap-2">
              <ShieldCheck className="w-4 h-4" />
              <span>Güvenli Altyapı</span>
            </div>
            <div className="flex items-center gap-2">
              <Building2 className="w-4 h-4" />
              <span>Kurumsal Çözüm</span>
            </div>
          </div>
        </div>

        {/* Right Side: Form */}
        <CardContent className="p-8 md:p-16 flex flex-col justify-center">
          <div className="mb-10 text-center md:text-left">
            <h2 className="text-3xl font-bold text-slate-900 mb-2">
              {isLogin ? "Hoş Geldiniz" : "Hesap Oluşturun"}
            </h2>
            <p className="text-slate-500">
              {isLogin 
                ? "Devam etmek için giriş yapın." 
                : "Sisteme dahil olmak için formu doldurun."}
            </p>
          </div>

          {error && (
            <div className="mb-6 p-4 bg-red-50 border border-red-100 text-red-600 text-sm rounded-xl animate-in">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-4">
              <div className="space-y-2">
                <Label>Organizasyon ID</Label>
                <div className="relative group">
                  <Building2 className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 group-focus-within:text-primary transition-colors" />
                  <Input
                    placeholder="Organizasyon UUID"
                    className="pl-10 h-12 rounded-xl"
                    value={formData.orgId}
                    onChange={(e) => setFormData({ ...formData, orgId: e.target.value })}
                    required
                  />
                </div>
              </div>

              {!isLogin && (
                <div className="space-y-2 animate-in">
                  <Label>Ad Soyad</Label>
                  <div className="relative group">
                    <User className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 group-focus-within:text-primary transition-colors" />
                    <Input
                      placeholder="Ad Soyad"
                      className="pl-10 h-12 rounded-xl"
                      value={formData.fullName}
                      onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                      required
                    />
                  </div>
                </div>
              )}

              <div className="space-y-2">
                <Label>E-posta</Label>
                <div className="relative group">
                  <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 group-focus-within:text-primary transition-colors" />
                  <Input
                    type="email"
                    placeholder="ornek@hukuk.com"
                    className="pl-10 h-12 rounded-xl"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="space-y-2">
                <Label>Şifre</Label>
                <div className="relative group">
                  <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400 group-focus-within:text-primary transition-colors" />
                  <Input
                    type="password"
                    placeholder="••••••••"
                    className="pl-10 h-12 rounded-xl"
                    value={formData.password}
                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                    required
                  />
                </div>
              </div>

              {!isLogin && (
                <div className="space-y-2 animate-in">
                  <Label>Rol</Label>
                  <Select
                    value={formData.role}
                    onValueChange={(val) => setFormData({ ...formData, role: val })}
                  >
                    <SelectTrigger className="h-12 rounded-xl">
                      <SelectValue placeholder="Rol Seçin" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="LAWYER">Avukat</SelectItem>
                      <SelectItem value="ADMIN">Yönetici</SelectItem>
                      <SelectItem value="SECRETARY">Sekreter</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              )}
            </div>

            <Button
              type="submit"
              disabled={loading}
              className="w-full h-12 rounded-xl text-md font-bold shadow-lg shadow-primary/20 transition-all hover:scale-[1.01] active:scale-[0.98]"
            >
              {loading ? "İşleniyor..." : (isLogin ? "Giriş Yap" : "Kayıt Ol")}
            </Button>
          </form>

          <div className="mt-8 text-center">
            <button 
              onClick={() => setIsLogin(!isLogin)}
              className="text-slate-600 hover:text-primary transition-colors text-sm font-medium"
            >
              {isLogin 
                ? "Henüz hesabınız yok mu? Kayıt olun" 
                : "Zaten hesabınız var mı? Giriş yapın"}
            </button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
