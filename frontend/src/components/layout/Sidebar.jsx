import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { 
  Scale, Briefcase, Calendar, CreditCard, Search, Users,
  Settings, LogOut, LayoutDashboard, PanelLeft
} from 'lucide-react';
import { useAuthStore } from '@/store/useAuthStore';
import { cn } from '@/lib/utils';

export function Sidebar({ collapsed, onToggle }) {
  const { logout, role } = useAuthStore();
  const navigate = useNavigate();

  const navigation = [
    { name: 'Panel', href: '/dashboard', icon: LayoutDashboard },
    { name: 'Davalar', href: '/matters', icon: Briefcase },
    { name: 'Takvim', href: '/calendar', icon: Calendar },
    { name: 'Hukuk Arama', href: '/legal-search', icon: Search },
    { name: 'Müvekkiller', href: '/clients', icon: Users },
    { name: 'Finans', href: '/billing', icon: CreditCard },
    ...(role === 'PLATFORM_ADMIN' 
      ? [{ name: 'Yönetim', href: '/super-admin', icon: Scale }] 
      : []),
  ];

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <aside className={cn(
      'flex flex-col h-full border-r border-border bg-card transition-all duration-200',
      collapsed ? 'w-16' : 'w-56'
    )}>
      {/* Brand */}
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

      {/* Navigation */}
      <nav className="flex-1 py-3 px-2 space-y-1">
        {navigation.map((item) => (
          <NavLink
            key={item.name}
            to={item.href}
            className={({ isActive }) => cn(
              'flex items-center gap-3 h-9 rounded-md text-sm transition-colors',
              collapsed ? 'justify-center w-12 mx-auto' : 'px-3',
              isActive
                ? 'bg-primary/10 text-primary font-medium'
                : 'text-muted-foreground hover:text-foreground hover:bg-accent'
            )}
          >
            <item.icon className="w-4 h-4 shrink-0" />
            {!collapsed && <span className="truncate">{item.name}</span>}
          </NavLink>
        ))}
      </nav>

      {/* Bottom */}
      <div className="py-3 px-2 border-t border-border space-y-1">
        <NavLink
          to="/settings"
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


