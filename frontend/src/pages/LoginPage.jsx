import { useState } from "react";
import { api } from "@/api/client";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "@/store/useAuthStore";
import { AuthInfoPanel } from "@/components/auth/AuthInfoPanel";
import { LoginForm } from "@/components/auth/LoginForm";
import { toast } from "sonner";

export default function LoginPage() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const setAuth = useAuthStore((state) => state.setAuth);
  const navigate = useNavigate();

  const handleAuth = async (values) => {
    setLoading(true);
    setError("");
    try {
      const response = await api.post("/auth/login", values);
      const data = response.data; 
      
      if (!data?.token) {
        setError("Sunucudan geçersiz yanıt alındı (Token bulunamadı).");
        toast.error("Giriş işlemi başarısız.");
        return;
      }

      setAuth({
        token: data.token,
        role: data.role,
        orgId: data.orgId,
        user: data.user
      });
      
      const userRole = (data.role || "").toString().trim().toUpperCase();
      toast.success("Giriş başarılı!");

      if (userRole === "PLATFORM_ADMIN") {
        navigate("/super-admin");
      } else {
        navigate("/dashboard");
      }
    } catch (err) {
      const msg = err.response?.data?.message || err.message || "Giriş başarısız. Lütfen bilgilerinizi kontrol edin.";
      setError(msg);
      toast.error("Giriş başarısız.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background p-4">
      <div className="max-w-5xl w-full grid md:grid-cols-2 border border-border rounded-2xl overflow-hidden bg-card">
        <AuthInfoPanel />

        <div className="p-8 md:p-12 flex flex-col justify-center">
          <div className="mb-8">
            <h2 className="text-2xl font-semibold text-foreground mb-1">Hoş Geldiniz</h2>
            <p className="text-sm text-muted-foreground">Devam etmek için giriş yapın.</p>
          </div>

          {error && (
            <div className="mb-6 p-3 rounded-lg bg-destructive/10 border border-destructive/20 text-destructive text-sm font-medium">
              {error}
            </div>
          )}

          <LoginForm onSubmit={handleAuth} isLoading={loading} />

          <div className="mt-8 text-center">
            <p className="text-xs text-muted-foreground">
              Giriş yapamıyorsanız lütfen sistem yöneticinizle iletişime geçin.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
