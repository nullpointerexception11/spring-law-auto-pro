import { useAuthStore } from "@/store/useAuthStore";
import { ROUTES } from "@/lib/constants";

export default function DebugPage() {
  const { token, role, orgId } = useAuthStore((state) => state.getSession());

  return (
    <div className="p-10 font-mono bg-slate-900 text-green-400 min-h-screen">
      <h1 className="text-3xl font-bold mb-10 text-white underline">LawAuto Sistem Teşhis Paneli</h1>
      
      <div className="space-y-6">
        <section className="p-6 bg-slate-800 rounded-xl border border-slate-700">
          <h2 className="text-xl text-indigo-400 mb-4 font-bold">Auth Store Durumu:</h2>
          <p><strong>Aktif Rol:</strong> <span className="text-yellow-400">{role || "BULUNAMADI"}</span></p>
          <p><strong>Şirket ID:</strong> {orgId || "BULUNAMADI"}</p>
          <p className="break-all"><strong>Token:</strong> {token ? token.substring(0, 50) + "..." : "YOK"}</p>
        </section>

        <section className="p-6 bg-slate-800 rounded-xl border border-slate-700">
          <h2 className="text-xl text-indigo-400 mb-4 font-bold">Yönlendirme Testi:</h2>
          <p>Eğer rolün <code className="bg-slate-700 px-2 py-1 rounded text-white">PLATFORM_ADMIN</code> ise butona bastığında yönlenmelisin.</p>
          <button 
            onClick={() => window.location.href = ROUTES.SUPER_ADMIN}
            className="mt-4 px-6 py-3 bg-indigo-600 text-white font-bold rounded-lg hover:bg-indigo-700 transition-all"
          >
            Süper Admin Paneline Gitmeyi Dene
          </button>
        </section>
      </div>

      <div className="mt-10 text-slate-500 text-sm italic">
        * Bu sayfa sadece teşhis amaçlıdır.
      </div>
    </div>
  );
}
