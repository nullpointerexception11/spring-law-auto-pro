import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

const resources = {
  tr: {
    translation: {
      // Sidebar
      "sidebar.dashboard": "Gösterge Paneli",
      "sidebar.matters": "Davalar",
      "sidebar.calendar": "Takvim",
      "sidebar.documents": "Belgeler",
      "sidebar.billing": "Faturalandırma",
      "sidebar.ai": "Yapay Zeka Araştırma",
      "sidebar.notifications": "Bildirimler",
      "sidebar.settings": "Ayarlar",
      "sidebar.collapse": "Daralt",
      "sidebar.expand": "Genişlet",
      
      // Topbar
      "topbar.search_placeholder": "Dava, belge, taraf ara...",
      "topbar.quick_add": "Hızlı Ekle",
      "topbar.org_name": "Prestij Hukuk Bürosu",
      
      // Matter List Page
      "matter.title": "Davalar",
      "matter.subtitle": "Hukuki davalarınızı, kurumsal projelerinizi ve ihtilaflarınızı yönetin.",
      "matter.new_matter": "Yeni Dava",
      "matter.filter_placeholder": "Davaları filtrele...",
      "matter.no_results": "Filtrenizle eşleşen dava bulunamadı.",
      
      // Table Headers
      "table.id": "Kayıt No",
      "table.matter_title": "Dava Başlığı",
      "table.client": "Müvekkil",
      "table.lead_counsel": "Sorumlu Avukat",
      "table.status": "Durum",
      "table.next_hearing": "Sonraki Duruşma"
    }
  },
  en: {
    translation: {
      // Sidebar
      "sidebar.dashboard": "Dashboard",
      "sidebar.matters": "Matters",
      "sidebar.calendar": "Calendar",
      "sidebar.documents": "Documents",
      "sidebar.billing": "Billing",
      "sidebar.ai": "AI Research",
      "sidebar.notifications": "Notifications",
      "sidebar.settings": "Settings",
      "sidebar.collapse": "Collapse",
      "sidebar.expand": "Expand",
      
      // Topbar
      "topbar.search_placeholder": "Search matters, documents, parties...",
      "topbar.quick_add": "Quick Add",
      "topbar.org_name": "Prestige Law Firm",
      
      // Matter List Page
      "matter.title": "Matters",
      "matter.subtitle": "Manage your legal cases, corporate projects, and disputes.",
      "matter.new_matter": "New Matter",
      "matter.filter_placeholder": "Filter matters...",
      "matter.no_results": "No matters found matching your filter.",
      
      // Table Headers
      "table.id": "ID",
      "table.matter_title": "Matter Title",
      "table.client": "Client",
      "table.lead_counsel": "Lead Counsel",
      "table.status": "Status",
      "table.next_hearing": "Next Hearing"
    }
  }
};

i18n
  .use(initReactI18next)
  .init({
    resources,
    lng: "tr", // Varsayılan dili Türkçe yapıyoruz
    fallbackLng: "en",
    interpolation: {
      escapeValue: false 
    }
  });

export default i18n;
