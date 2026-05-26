import React from 'react';
import { Button } from '@/components/ui/button';
import { AlertCircle, RotateCcw, Home } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    console.error("Uncaught error:", error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen flex items-center justify-center bg-background p-6">
          <div className="max-w-md w-full bg-card rounded-xl p-10 border border-border text-center space-y-6">
            <div className="h-20 w-20 bg-destructive/10 text-destructive rounded-full flex items-center justify-center mx-auto">
              <AlertCircle className="h-10 w-10" />
            </div>
            <div className="space-y-2">
              <h1 className="text-2xl font-semibold text-foreground">Bir Hata Oluştu</h1>
              <p className="text-muted-foreground">Uygulama beklenmedik bir sorunla karşılaştı. Lütfen sayfayı yenileyin veya ana sayfaya dönün.</p>
            </div>
            <div className="flex flex-col gap-3">
              <Button 
                onClick={() => window.location.reload()}
              >
                <RotateCcw className="h-4 w-4 mr-2" /> Sayfayı Yenile
              </Button>
              <Button 
                variant="outline"
                onClick={() => window.location.href = "/"}
              >
                <Home className="h-4 w-4 mr-2" /> Ana Sayfaya Dön
              </Button>
            </div>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

export function NotFoundPage() {
  const navigate = useNavigate();
  return (
    <div className="min-h-screen flex items-center justify-center bg-background p-6">
      <div className="text-center space-y-8">
        <h1 className="text-9xl font-bold text-muted-foreground/20 select-none">404</h1>
        <div className="space-y-2">
          <h2 className="text-3xl font-semibold text-foreground">Aradığınız Sayfa Bulunamadı</h2>
          <p className="text-muted-foreground max-w-sm mx-auto">Görünüşe göre girdiğiniz adres hatalı veya sayfa taşınmış olabilir.</p>
        </div>
        <Button 
          onClick={() => navigate("/")}
        >
          <Home className="h-5 w-5 mr-2" /> ANA SAYFAYA DÖN
        </Button>
      </div>
    </div>
  );
}
