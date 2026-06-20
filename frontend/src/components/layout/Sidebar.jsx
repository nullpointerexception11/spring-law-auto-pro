import React, { useCallback, useMemo } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import {
  Scale,
  Briefcase,
  Calendar,
  CreditCard,
  Search,
  Users,
  FileText,
  Sparkles,
  Settings,
  LogOut,
  LayoutDashboard,
  PanelLeft,
  BadgeInfo,
} from 'lucide-react';
import { useAuthStore } from '@/store/useAuthStore';
import { ROUTES } from '@/lib/constants';
import { cn } from '@/lib/utils';
import { Badge } from '@/components/ui/badge';

const LIVE_NAVIGATION = [
  { name: 'Panel', href: ROUTES.DASHBOARD, icon: LayoutDashboard },
  { name: 'Davalar', href: ROUTES.MATTERS, icon: Briefcase },
  { name: 'Belgeler', href: ROUTES.DOCUMENTS, icon: FileText },
  { name: 'AI Asistan', href: ROUTES.AI, icon: Sparkles },
  { name: 'Hukuk Arama', href: ROUTES.LEGAL_SEARCH, icon: Search },
  { name: 'Müvekkiller', href: ROUTES.CLIENTS, icon: Users },
];

const PREVIEW_NAVIGATION = [
  { name: 'Takvim', href: ROUTES.CALENDAR, icon: Calendar, badge: 'Önizleme' },
  { name: 'Finans', href: ROUTES.BILLING, icon: CreditCard, badge: 'Önizleme' },
];

function NavItem({ item, collapsed, activeClassName, inactiveClassName }) {
  return (
    <NavLink
      to={item.href}
      className={({ isActive }) => cn(
        'flex items-center gap-3 h-9 rounded-md text-sm transition-colors',
        collapsed ? 'justify-center w-12 mx-auto' : 'px-3',
        isActive ? activeClassName : inactiveClassName
      )}
    >
      <item.icon className="w-4 h-4 shrink-0" />
      {!collapsed && <span className="truncate">{item.name}</span>}
    </NavLink>
  );
}

function SidebarComponent({ collapsed, onToggle }) {
  const { logout, role } = useAuthStore();
  const navigate = useNavigate();
  const previewNavigation = useMemo(() => {
    if (role === 'PLATFORM_ADMIN') {
      return [...PREVIEW_NAVIGATION, { name: 'Yönetim', href: ROUTES.SUPER_ADMIN, icon: Scale, badge: 'Önizleme' }];
    }
    return PREVIEW_NAVIGATION;
  }, [role]);

  const handleLogout = useCallback(() => {
    logout();
    navigate(ROUTES.LOGIN);
  }, [logout, navigate]);

  return (
    <aside className={cn(
      'flex flex-col h-full border-r border-border bg-card transition-all duration-200',
      collapsed ? 'w-16' : 'w-56'
    )}>
      <div className={cn(
        'flex items-center h-14 border-b border-border px-4',
        collapsed && 'justify-center px-0'
      )}>
        {collapsed ? (
          <div className="w-8 h-8 rounded-md bg-primary flex items-center justify-center">
            <Scale className="w-4 h-4 text-primary-foreground" />
          </div>
        ) : (
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-md bg-primary flex items-center justify-center">
              <Scale className="w-4 h-4 text-primary-foreground" />
            </div>
            <span className="font-semibold text-sm text-foreground tracking-tight">
              LawAuto
            </span>
          </div>
        )}
      </div>

      <nav className="flex-1 py-3 px-2 space-y-4">
        <div className="space-y-1">
          {LIVE_NAVIGATION.map((item) => (
            <NavItem
              key={item.name}
              item={item}
              collapsed={collapsed}
              activeClassName="bg-primary/10 text-primary font-medium"
              inactiveClassName="text-muted-foreground hover:text-foreground hover:bg-accent"
            />
          ))}
        </div>

        {previewNavigation.length > 0 && (
          <div className="pt-2 border-t border-border space-y-1">
            {!collapsed && (
              <div className="flex items-center gap-2 px-3 pb-1 text-[10px] font-semibold uppercase tracking-wider text-muted-foreground">
                <BadgeInfo className="w-3.5 h-3.5" />
                Önizleme
              </div>
            )}
            {previewNavigation.map((item) => (
              <NavLink
                key={item.name}
                to={item.href}
                className={({ isActive }) => cn(
                  'flex items-center gap-3 h-9 rounded-md text-sm transition-colors',
                  collapsed ? 'justify-center w-12 mx-auto' : 'px-3',
                  isActive
                    ? 'bg-amber-100/70 text-amber-700 dark:bg-amber-950/40 dark:text-amber-300 font-medium'
                    : 'text-muted-foreground hover:text-foreground hover:bg-amber-50 dark:hover:bg-amber-950/20'
                )}
              >
                <item.icon className="w-4 h-4 shrink-0" />
                {!collapsed && (
                  <span className="flex items-center gap-2 min-w-0">
                    <span className="truncate">{item.name}</span>
                    <Badge variant="outline" className="text-[9px] px-1.5 py-0 h-4 border-amber-200 text-amber-700 bg-amber-50 dark:border-amber-900 dark:text-amber-300 dark:bg-amber-950/40">
                      {item.badge}
                    </Badge>
                  </span>
                )}
              </NavLink>
            ))}
          </div>
        )}
      </nav>

      <div className="py-3 px-2 border-t border-border space-y-1">
        <NavLink
          to={ROUTES.SETTINGS}
          className={({ isActive }) => cn(
            'flex items-center gap-3 h-9 rounded-md text-sm transition-colors',
            collapsed ? 'justify-center w-12 mx-auto' : 'px-3',
            isActive
              ? 'bg-primary/10 text-primary font-medium'
              : 'text-muted-foreground hover:text-foreground hover:bg-accent'
          )}
        >
          <Settings className="w-4 h-4 shrink-0" />
          {!collapsed && <span>Ayarlar</span>}
        </NavLink>

        <button
          onClick={handleLogout}
          className={cn(
            'flex items-center gap-3 h-9 rounded-md text-sm transition-colors w-full',
            collapsed ? 'justify-center w-12 mx-auto' : 'px-3',
            'text-muted-foreground hover:text-destructive hover:bg-destructive/10'
          )}
        >
          <LogOut className="w-4 h-4 shrink-0" />
          {!collapsed && <span>Çıkış</span>}
        </button>

        {!collapsed && (
          <button
            onClick={onToggle}
            className="flex items-center justify-center h-9 rounded-md text-muted-foreground hover:text-foreground hover:bg-accent w-full text-sm"
          >
            <PanelLeft className="w-4 h-4" />
          </button>
        )}
      </div>
    </aside>
  );
}

export const Sidebar = React.memo(SidebarComponent);
