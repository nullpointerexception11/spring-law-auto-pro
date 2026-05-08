import { useState } from "react";
import { api } from "../api/client";
import { useNavigate } from "react-router-dom";
import { Scale, ShieldCheck, Mail, Lock, Building2, User } from "lucide-react";

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
      localStorage.setItem("user", JSON.minifiy ? JSON.stringify(data.user) : "{}"); // Placeholder if data.user exists
      navigate("/dashboard");
    } catch (err) {
      setError(err?.response?.data?.error || "Bir hata oluştu. Lütfen bilgilerinizi kontrol edin.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#f8fafc] p-4 font-sans">
      <div className="max-w-5xl w-full grid md:grid-cols-2 bg-white rounded-3xl shadow-2xl overflow-hidden animate-in">
        
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
        <div className="p-8 md:p-16 flex flex-col justify-center">
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

          <form onSubmit={handleSubmit} className="space-y-5">
            <div className="space-y-4">
              <div className="relative group">
                <Building2 className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400 group-focus-within:text-primary transition-colors" />
                <input
                  type="text"
                  placeholder="Organizasyon ID (UUID)"
                  className="w-full pl-12 pr-4 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all"
                  value={formData.orgId}
                  onChange={(e) => setFormData({ ...formData, orgId: e.target.value })}
                  required
                />
              </div>

              {!isLogin && (
                <div className="relative group animate-in">
                  <User className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400 group-focus-within:text-primary transition-colors" />
                  <input
                    type="text"
                    placeholder="Ad Soyad"
                    className="w-full pl-12 pr-4 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all"
                    value={formData.fullName}
                    onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                    required
                  />
                </div>
              )}

              <div className="relative group">
                <Mail className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400 group-focus-within:text-primary transition-colors" />
                <input
                  type="email"
                  placeholder="E-posta Adresi"
                  className="w-full pl-12 pr-4 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  required
                />
              </div>

              <div className="relative group">
                <Lock className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-slate-400 group-focus-within:text-primary transition-colors" />
                <input
                  type="password"
                  placeholder="Şifre"
                  className="w-full pl-12 pr-4 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all"
                  value={formData.password}
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                  required
                />
              </div>

              {!isLogin && (
                <div className="relative animate-in">
                  <select
                    className="w-full px-4 py-4 bg-slate-50 border border-slate-200 rounded-2xl focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all appearance-none"
                    value={formData.role}
                    onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                  >
                    <option value="LAWYER">Avukat</option>
                    <option value="ADMIN">Yönetici</option>
                    <option value="SECRETARY">Sekreter</option>
                  </select>
                </div>
              )}
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full py-4 bg-primary text-white font-bold rounded-2xl shadow-lg shadow-primary/20 hover:bg-primary/90 hover:scale-[1.01] active:scale-[0.98] transition-all disabled:opacity-50 disabled:hover:scale-100"
            >
              {loading ? "İşleniyor..." : (isLogin ? "Giriş Yap" : "Kayıt Ol")}
            </button>
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
        </div>
      </div>
    </div>
  );
}
