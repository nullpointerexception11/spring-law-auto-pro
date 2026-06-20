import { useCallback, useState } from 'react';
import { api } from '@/api/client';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/useAuthStore';
import { AuthInfoPanel } from '@/components/auth/AuthInfoPanel';
import { LoginForm } from '@/components/auth/LoginForm';
import { ROUTES } from '@/lib/constants';
import { toast } from 'sonner';

export default function LoginPage() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const setAuth = useAuthStore((state) => state.setAuth);
  const navigate = useNavigate();

  const handleAuth = useCallback(
    async (values) => {
      setLoading(true);
      setError('');

      try {
        const response = await api.post('/auth/login', values);
        const data = response.data;

        if (!data?.token) {
          setError('Sunucudan geçersiz yanıt alındı (Token bulunamadı).');
          toast.error('Giriş işlemi başarısız.');
          return;
        }

        setAuth({
          token: data.token,
          role: data.role,
          orgId: data.orgId,
          fullName: data.fullName,
          email: data.email,
        });

        const userRole = (data.role || '').toString().trim().toUpperCase();
        toast.success('Giriş başarılı!');

        navigate(userRole === 'PLATFORM_ADMIN' ? ROUTES.SUPER_ADMIN : ROUTES.DASHBOARD);
      } catch (err) {
        const msg = err.response?.data?.message || err.message || 'Giriş başarısız. Lütfen bilgilerinizi kontrol edin.';
        setError(msg);
        toast.error('Giriş başarısız.');
      } finally {
        setLoading(false);
      }
    },
    [navigate, setAuth]
  );

  return (
    <div className="min-h-screen px-4 py-6 sm:p-6 flex items-center justify-center">
      <div className="app-shell-surface w-full max-w-5xl overflow-hidden rounded-[1.5rem] grid md:grid-cols-2">
        <AuthInfoPanel />

        <div className="relative flex flex-col justify-center p-8 md:p-12">
          <div className="mb-8 space-y-2">
            <span className="inline-flex items-center rounded-full border border-primary/15 bg-primary/5 px-3 py-1 text-[10px] font-semibold uppercase tracking-[0.24em] text-primary">
              Güvenli giriş
            </span>
            <h2 className="text-3xl font-semibold text-foreground">Hoş geldiniz</h2>
            <p className="max-w-md text-sm text-muted-foreground">
              Devam etmek için sisteme giriş yapın. Kurumsal iş akışınız burada başlar.
            </p>
          </div>

          {error && (
            <div className="mb-6 rounded-xl border border-destructive/20 bg-destructive/10 p-3 text-sm font-medium text-destructive">
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
