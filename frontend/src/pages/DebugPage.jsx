import { useState, useEffect } from "react";

export default function DebugPage() {
  const [info, setInfo] = useState({
    token: localStorage.getItem("token"),
    role: localStorage.getItem("role"),
    orgId: localStorage.getItem("orgId")
  });

  return (
    <div className="p-10 font-mono bg-slate-900 text-green-400 min-h-screen">
      <h1 className="text-3xl font-bold mb-10 text-white underline">LawAuto Sistem Teşhis Paneli</h1>
      
      <div className="space-y-6">
        <section className="p-6 bg-slate-800 rounded-xl border border-slate-700">
          <h2 className="text-xl text-indigo-400 mb-4 font-bold">Yerel Depolama (LocalStorage) Durumu:</h2>
          <p><strong>Aktif Rol:</strong> <span className="text-yellow-400">{info.role || "BULUNAMADI"}</span></p>
          <p><strong>Şirket ID:</strong> {info.orgId || "BULUNAMADI"}</p>
          <p className="break-all"><strong>Token:</strong> {info.token ? info.token.substring(0, 50) + "..." : "YOK"}</p>
        </section>

        <section className="p-6 bg-slate-800 rounded-xl border border-slate-700">
          <h2 className="text-xl text-indigo-400 mb-4 font-bold">Yönlendirme Testi:</h2>
          <p>Eğer rolün <code className="bg-slate-700 px-2 py-1 rounded text-white">PLATFORM_ADMIN</code> ise butona bastığında yönlenmelisin.</p>
          <button 
            onClick={() => window.location.href = "/super-admin"}
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
