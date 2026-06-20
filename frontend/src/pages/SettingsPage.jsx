import React, { memo, useCallback, useMemo, useState } from 'react';
import {
  User,
  Bell,
  Shield,
  Moon,
  Sun,
  Save,
  Key,
  Building2,
  MapPin,
  CreditCard,
  LogOut,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { useAuthStore } from '@/store/useAuthStore';
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '@/lib/constants';
import { cn } from '@/lib/utils';

const SETTINGS_SECTIONS = [
  { id: 'profile', label: 'Profil', icon: User },
  { id: 'notifications', label: 'Bildirimler', icon: Bell },
  { id: 'appearance', label: 'Görünüm', icon: Sun },
  { id: 'security', label: 'Güvenlik', icon: Shield },
  { id: 'organization', label: 'Organizasyon', icon: Building2 },
];

const NOTIFICATIONS = [
  { label: 'Duruşma hatırlatmaları', description: 'Yaklaşan duruşmalar için bildirim', enabled: true },
  { label: 'Son tarih uyarıları', description: 'Dilekçe ve evrak son teslim tarihleri', enabled: true },
  { label: 'Dava güncellemeleri', description: 'Yeni işlem ve durum değişiklikleri', enabled: false },
  { label: 'Fatura bildirimleri', description: 'Yeni fatura ve ödeme hatırlatmaları', enabled: true },
  { label: 'Sistem duyuruları', description: 'Platform güncellemeleri ve bakım bildirimleri', enabled: false },
];

const ORGANIZATION_MEMBERS = [
  { name: 'Ahmet Yılmaz', role: 'Yönetici', email: 'ahmet@lawauto.com' },
  { name: 'Ayşe Demir', role: 'Avukat', email: 'ayse@lawauto.com' },
  { name: 'Mehmet Kaya', role: 'Sekreter', email: 'mehmet@lawauto.com' },
];

const SettingsNavButton = memo(function SettingsNavButton({ section, activeSection, onSelect }) {
  const Icon = section.icon;

  return (
    <button
      type="button"
      onClick={() => onSelect(section.id)}
      className={cn(
        'flex items-center gap-3 w-full px-3 py-2 rounded-md text-sm transition-colors',
        activeSection === section.id ? 'bg-primary/10 text-primary font-medium' : 'text-muted-foreground hover:text-foreground hover:bg-accent'
      )}
    >
      <Icon className="h-4 w-4" />
      {section.label}
    </button>
  );
});

function SettingsPageComponent() {
  const { user, role, orgId, logout } = useAuthStore();
  const navigate = useNavigate();
  const [activeSection, setActiveSection] = useState('profile');
  const [isDark, setIsDark] = useState(() => document.documentElement.classList.contains('dark'));

  const sectionIcon = useMemo(
    () => SETTINGS_SECTIONS.find((section) => section.id === activeSection)?.icon || User,
    [activeSection]
  );
  const roleLabel = useMemo(
    () => (role === 'PLATFORM_ADMIN' ? 'Platform Yöneticisi' : role === 'ORG_ADMIN' ? 'Organizasyon Yöneticisi' : 'Avukat'),
    [role]
  );

  const toggleDark = useCallback(() => {
    const dark = document.documentElement.classList.toggle('dark');
    setIsDark(dark);
  }, []);

  const handleLogout = useCallback(() => {
    logout();
    navigate(ROUTES.LOGIN);
  }, [logout, navigate]);

  const handleSelectSection = useCallback((sectionId) => {
    setActiveSection(sectionId);
  }, []);

  const handleNotificationToggle = useCallback((event) => {
    event.preventDefault();
  }, []);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-foreground">Ayarlar</h1>
        <p className="text-sm text-muted-foreground mt-0.5">Hesap ve uygulama tercihlerinizi yönetin.</p>
      </div>

      <div className="flex flex-col lg:flex-row gap-6">
        <div className="lg:w-56 shrink-0">
          <nav className="space-y-1">
            {SETTINGS_SECTIONS.map((section) => (
              <SettingsNavButton
                key={section.id}
                section={section}
                activeSection={activeSection}
                onSelect={handleSelectSection}
              />
            ))}
          </nav>

          <Separator className="my-4" />

          <button
            type="button"
            onClick={handleLogout}
            className="flex items-center gap-3 w-full px-3 py-2 rounded-md text-sm text-muted-foreground hover:text-destructive hover:bg-destructive/10 transition-colors"
          >
            <LogOut className="h-4 w-4" />
            Çıkış Yap
          </button>
        </div>

        <div className="flex-1 space-y-6">
          {activeSection === 'profile' && (
            <>
              <Card>
                <CardHeader>
                  <CardTitle className="text-base">Profil Bilgileri</CardTitle>
                  <CardDescription className="text-xs">Kişisel bilgilerinizi güncelleyin.</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="flex items-center gap-4 pb-4">
                    <div className="h-16 w-16 rounded-full bg-primary/10 text-primary flex items-center justify-center text-lg font-semibold">
                      {user?.fullName?.charAt(0) || 'A'}
                    </div>
                    <div>
                      <p className="text-sm font-medium text-foreground">{user?.fullName || 'Avukat'}</p>
                      <p className="text-xs text-muted-foreground">{user?.email || 'ornek@lawauto.com'}</p>
                      <Badge variant="outline" className="mt-1 text-[10px]">
                        {roleLabel}
                      </Badge>
                    </div>
                  </div>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="space-y-1.5">
                      <label className="text-[11px] font-medium text-foreground">Ad Soyad</label>
                      <Input defaultValue={user?.fullName || ''} className="h-9 text-sm" />
                    </div>
                    <div className="space-y-1.5">
                      <label className="text-[11px] font-medium text-foreground">E-posta</label>
                      <Input defaultValue={user?.email || ''} className="h-9 text-sm" type="email" />
                    </div>
                    <div className="space-y-1.5">
                      <label className="text-[11px] font-medium text-foreground">Telefon</label>
                      <Input placeholder="+90 5XX XXX XX XX" className="h-9 text-sm" />
                    </div>
                    <div className="space-y-1.5">
                      <label className="text-[11px] font-medium text-foreground">Baro Sicil No</label>
                      <Input placeholder="12345" className="h-9 text-sm" />
                    </div>
                  </div>
                  <div className="flex justify-end">
                    <Button size="sm">
                      <Save className="h-4 w-4 mr-1.5" /> Kaydet
                    </Button>
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle className="text-base">Organizasyon Bilgileri</CardTitle>
                  <CardDescription className="text-xs">Bağlı olduğunuz organizasyon detayları.</CardDescription>
                </CardHeader>
                <CardContent className="space-y-3">
                  <div className="flex items-center gap-3">
                    <Building2 className="h-4 w-4 text-muted-foreground" />
                    <span className="text-sm text-foreground">LawAuto Hukuk Bürosu</span>
                    <Badge variant="secondary" className="text-[9px]">Aktif</Badge>
                  </div>
                  <div className="flex items-center gap-3">
                    <MapPin className="h-4 w-4 text-muted-foreground" />
                    <span className="text-sm text-muted-foreground">İstanbul, Türkiye</span>
                  </div>
                  <div className="flex items-center gap-3">
                    <CreditCard className="h-4 w-4 text-muted-foreground" />
                    <span className="text-sm font-mono text-muted-foreground text-xs">{orgId || '---'}</span>
                  </div>
                </CardContent>
              </Card>
            </>
          )}

          {activeSection === 'notifications' && (
            <Card>
              <CardHeader>
                <CardTitle className="text-base">Bildirim Tercihleri</CardTitle>
                <CardDescription className="text-xs">Hangi bildirimleri almak istediğinizi seçin.</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                {NOTIFICATIONS.map((item) => (
                  <div key={item.label} className="flex items-center justify-between py-1">
                    <div>
                      <p className="text-sm font-medium text-foreground">{item.label}</p>
                      <p className="text-xs text-muted-foreground">{item.description}</p>
                    </div>
                    <button
                      type="button"
                      onClick={handleNotificationToggle}
                      className={cn(
                        'relative inline-flex h-5 w-9 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors',
                        item.enabled ? 'bg-primary' : 'bg-muted'
                      )}
                    >
                      <span
                        className={cn(
                          'pointer-events-none inline-block h-4 w-4 rounded-full bg-background shadow transform ring-0 transition-transform',
                          item.enabled ? 'translate-x-4' : 'translate-x-0'
                        )}
                      />
                    </button>
                  </div>
                ))}
              </CardContent>
            </Card>
          )}

          {activeSection === 'appearance' && (
            <Card>
              <CardHeader>
                <CardTitle className="text-base">Görünüm</CardTitle>
                <CardDescription className="text-xs">Uygulama temasını ve görünüm ayarlarını özelleştirin.</CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                <div className="space-y-3">
                  <p className="text-sm font-medium text-foreground">Tema</p>
                  <div className="flex items-center gap-3">
                    <button
                      type="button"
                      onClick={() => {
                        if (isDark) toggleDark();
                      }}
                      className={cn(
                        'flex items-center gap-2 px-4 py-2.5 rounded-lg border text-sm transition-colors',
                        !isDark ? 'border-primary bg-primary/5 text-primary' : 'border-border text-muted-foreground hover:border-muted-foreground/30'
                      )}
                    >
                      <Sun className="h-4 w-4" />
                      Açık
                    </button>
                    <button
                      type="button"
                      onClick={() => {
                        if (!isDark) toggleDark();
                      }}
                      className={cn(
                        'flex items-center gap-2 px-4 py-2.5 rounded-lg border text-sm transition-colors',
                        isDark ? 'border-primary bg-primary/5 text-primary' : 'border-border text-muted-foreground hover:border-muted-foreground/30'
                      )}
                    >
                      <Moon className="h-4 w-4" />
                      Koyu
                    </button>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}

          {activeSection === 'security' && (
            <Card>
              <CardHeader>
                <CardTitle className="text-base">Güvenlik</CardTitle>
                <CardDescription className="text-xs">Şifre ve güvenlik ayarlarınızı yönetin.</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-1.5">
                  <label className="text-[11px] font-medium text-foreground">Mevcut Şifre</label>
                  <Input type="password" placeholder="••••••••" className="h-9 text-sm" />
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-1.5">
                    <label className="text-[11px] font-medium text-foreground">Yeni Şifre</label>
                    <Input type="password" placeholder="••••••••" className="h-9 text-sm" />
                  </div>
                  <div className="space-y-1.5">
                    <label className="text-[11px] font-medium text-foreground">Yeni Şifre (Tekrar)</label>
                    <Input type="password" placeholder="••••••••" className="h-9 text-sm" />
                  </div>
                </div>
                <div className="flex justify-end">
                  <Button size="sm">
                    <Key className="h-4 w-4 mr-1.5" /> Şifreyi Güncelle
                  </Button>
                </div>

                <Separator />

                <div className="space-y-2">
                  <p className="text-sm font-medium text-foreground">Aktif Oturumlar</p>
                  <div className="p-3 rounded-lg border border-border bg-muted/30 flex items-center justify-between">
                    <div>
                      <p className="text-sm text-foreground">Mevcut Cihaz</p>
                      <p className="text-xs text-muted-foreground">Son aktif: Şimdi</p>
                    </div>
                    <Badge variant="success" className="text-[9px]">Aktif</Badge>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}

          {activeSection === 'organization' && (
            <Card>
              <CardHeader>
                <CardTitle className="text-base">Organizasyon Ayarları</CardTitle>
                <CardDescription className="text-xs">Organizasyon bilgilerini ve üyeleri yönetin.</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-1.5">
                    <label className="text-[11px] font-medium text-foreground">Organizasyon Adı</label>
                    <Input defaultValue="LawAuto Hukuk Bürosu" className="h-9 text-sm" />
                  </div>
                  <div className="space-y-1.5">
                    <label className="text-[11px] font-medium text-foreground">Vergi No</label>
                    <Input placeholder="1234567890" className="h-9 text-sm" />
                  </div>
                  <div className="space-y-1.5">
                    <label className="text-[11px] font-medium text-foreground">Adres</label>
                    <Input placeholder="Levent, Beşiktaş" className="h-9 text-sm" />
                  </div>
                  <div className="space-y-1.5">
                    <label className="text-[11px] font-medium text-foreground">Web Sitesi</label>
                    <Input placeholder="https://lawauto.com" className="h-9 text-sm" />
                  </div>
                </div>

                <Separator />

                <div className="space-y-3">
                  <p className="text-sm font-medium text-foreground">Organizasyon Üyeleri</p>
                  {ORGANIZATION_MEMBERS.map((member) => (
                    <div key={member.email} className="flex items-center justify-between p-3 rounded-lg border border-border">
                      <div className="flex items-center gap-3">
                        <div className="h-8 w-8 rounded-full bg-muted flex items-center justify-center text-xs font-medium text-muted-foreground">
                          {member.name.charAt(0)}
                        </div>
                        <div>
                          <p className="text-sm font-medium text-foreground">{member.name}</p>
                          <p className="text-xs text-muted-foreground">{member.email}</p>
                        </div>
                      </div>
                      <Badge variant="outline" className="text-[10px]">{member.role}</Badge>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
}

export default memo(SettingsPageComponent);
