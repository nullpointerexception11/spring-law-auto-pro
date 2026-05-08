import { useState } from "react";
import { api } from "@/api/client";
import { useNavigate } from "react-router-dom";
import { Card, CardContent } from "@/components/ui/card";
import { AuthInfoPanel } from "@/components/auth/AuthInfoPanel";
import { LoginForm } from "@/components/auth/LoginForm";
import { RegisterForm } from "@/components/auth/RegisterForm";

export default function LoginPage() {
  const [isLogin, setIsLogin] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleAuth = async (values) => {
    setLoading(true);
    setError("");
    try {
      const endpoint = isLogin ? "/auth/login" : "/auth/register";
      const { data } = await api.post(endpoint, values);
      localStorage.setItem("token", data.token);
      navigate("/dashboard");
    } catch (err) {
      setError(err?.response?.data?.error || "Kimlik doğrulama başarısız. Lütfen bilgilerinizi kontrol edin.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50 p-4 font-sans">
      <Card className="max-w-5xl w-full grid md:grid-cols-2 shadow-2xl border-none overflow-hidden animate-in p-0 rounded-3xl">
        
        {/* Left Side: Info Panel */}
        <AuthInfoPanel />

        {/* Right Side: Form Content */}
        <CardContent className="p-8 md:p-16 flex flex-col justify-center bg-white">
          <div className="mb-10 text-center md:text-left">
            <h2 className="text-3xl font-bold text-slate-900 mb-2">
              {isLogin ? "Hoş Geldiniz" : "Hesap Oluşturun"}
            </h2>
            <p className="text-slate-500 font-medium">
              {isLogin 
                ? "Devam etmek için giriş yapın." 
                : "Sisteme dahil olmak için formu doldurun."}
            </p>
          </div>

          {error && (
            <div className="mb-6 p-4 bg-red-50 border border-red-100 text-red-600 text-sm rounded-xl animate-in font-medium">
              {error}
            </div>
          )}

          {isLogin ? (
            <LoginForm onSubmit={handleAuth} isLoading={loading} />
          ) : (
            <RegisterForm onSubmit={handleAuth} isLoading={loading} />
          )}

          <div className="mt-8 text-center">
            <button 
              onClick={() => setIsLogin(!isLogin)}
              className="text-slate-600 hover:text-primary transition-colors text-sm font-semibold"
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
