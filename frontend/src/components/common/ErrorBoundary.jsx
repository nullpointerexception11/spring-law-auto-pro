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
        <div className="min-h-screen flex items-center justify-center bg-slate-50 p-6">
          <div className="max-w-md w-full bg-white rounded-[40px] p-10 shadow-xl shadow-slate-200 text-center space-y-6">
            <div className="h-20 w-20 bg-red-50 text-red-600 rounded-full flex items-center justify-center mx-auto">
              <AlertCircle className="h-10 w-10" />
            </div>
            <div className="space-y-2">
              <h1 className="text-2xl font-black text-slate-900">Bir Hata Oluştu</h1>
              <p className="text-slate-500 font-medium">Uygulama beklenmedik bir sorunla karşılaştı. Lütfen sayfayı yenileyin veya ana sayfaya dönün.</p>
            </div>
            <div className="flex flex-col gap-3">
              <Button 
                onClick={() => window.location.reload()}
                className="rounded-2xl h-12 bg-indigo-600 hover:bg-indigo-700 font-bold"
              >
                <RotateCcw className="h-4 w-4 mr-2" /> Sayfayı Yenile
              </Button>
              <Button 
                variant="outline"
                onClick={() => window.location.href = "/"}
                className="rounded-2xl h-12 font-bold"
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
    <div className="min-h-screen flex items-center justify-center bg-slate-50 p-6">
      <div className="text-center space-y-8">
        <h1 className="text-9xl font-black text-slate-200 select-none">404</h1>
        <div className="space-y-2">
          <h2 className="text-3xl font-bold text-slate-900">Aradığınız Sayfa Bulunamadı</h2>
          <p className="text-slate-500 font-medium max-w-sm mx-auto">Görünüşe göre girdiğiniz adres hatalı veya sayfa taşınmış olabilir.</p>
        </div>
        <Button 
          onClick={() => navigate("/")}
          className="rounded-2xl h-14 px-8 bg-indigo-600 hover:bg-indigo-700 font-black shadow-lg shadow-indigo-100"
        >
          <Home className="h-5 w-5 mr-2" /> ANA SAYFAYA DÖN
        </Button>
      </div>
    </div>
  );
}
