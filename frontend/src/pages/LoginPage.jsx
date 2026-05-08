import { useState } from "react";
import { api } from "@/api/client";
import { useNavigate } from "react-router-dom";
import { Card, CardContent } from "@/components/ui/card";
import { AuthInfoPanel } from "@/components/auth/AuthInfoPanel";
import { LoginForm } from "@/components/auth/LoginForm";

export default function LoginPage() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleAuth = async (values) => {
    setLoading(true);
    setError("");
    try {
      const response = await api.post("/auth/login", values);
      const data = response.data.data || response.data; 
      
      if (!data || !data.token) {
        setError("Sunucudan geçersiz yanıt alındı.");
        return;
      }

      localStorage.setItem("token", data.token);
      localStorage.setItem("role", data.role);
      localStorage.setItem("orgId", data.orgId);
      
      const userRole = (data.role || "").toString().trim().toUpperCase();

      if (userRole === "SUPER_ADMIN") {
        navigate("/super-admin");
      } else {
        navigate("/dashboard");
      }
    } catch (err) {
      setError(err?.response?.data?.error || "Giriş başarısız. Lütfen bilgilerinizi kontrol edin.");
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
            <h2 className="text-3xl font-bold text-slate-900 mb-2">Hoş Geldiniz</h2>
            <p className="text-slate-500 font-medium">Devam etmek için giriş yapın.</p>
          </div>

          {error && (
            <div className="mb-6 p-4 bg-red-50 border border-red-100 text-red-600 text-sm rounded-xl animate-in font-medium">
              {error}
            </div>
          )}

          <LoginForm onSubmit={handleAuth} isLoading={loading} />

          <div className="mt-8 text-center">
            <p className="text-slate-400 text-sm">
              Giriş yapamıyorsanız lütfen sistem yöneticinizle iletişime geçin.
            </p>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

