import React, { useEffect } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { 
  Scale, 
  Briefcase, 
  Calendar, 
  FileText, 
  CreditCard, 
  Bot, 
  Bell, 
  Settings,
  PanelLeftClose,
  PanelLeft,
  LogOut,
  BrainCircuit,
  LayoutDashboard
} from 'lucide-react';
import { useAuthStore } from '@/store/useAuthStore';

export function Sidebar({ isCollapsed, setIsCollapsed }) {
  const [isHovered, setIsHovered] = React.useState(false);
  const { logout, role } = useAuthStore();
  const navigate = useNavigate();

  const isEffectivelyOpen = !isCollapsed || isHovered;

  const navigation = [
    { name: 'Gösterge Paneli', href: '/dashboard', icon: LayoutDashboard },
    { name: 'Davalar', href: '/matters', icon: Briefcase },
    { name: 'Takvim', href: '/calendar', icon: Calendar },
    { name: 'Belgeler', href: '/documents', icon: FileText },
    { name: 'Faturalandırma', href: '/billing', icon: CreditCard },
    { name: 'AI Asistan', href: '/ai', icon: BrainCircuit },
    ...(role === 'PLATFORM_ADMIN' ? [{ name: 'Sistem Paneli', href: '/super-admin', icon: Scale }] : []),
  ];

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div 
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      className={cn(
        "flex h-full flex-col border-r border-slate-100 bg-white transition-all duration-500 ease-in-out shadow-sm z-30",
        isEffectivelyOpen ? 'w-64' : 'w-20'
      )}
    >
      {/* Brand & Toggle */}
      <div className={cn(
        "flex items-center h-20 px-5 transition-all duration-300",
        !isEffectivelyOpen ? 'justify-center' : 'justify-between'
      )}>
        {isEffectivelyOpen ? (
          <div className="flex items-center gap-3 overflow-hidden">
            <div className="h-9 w-9 rounded-xl bg-indigo-600 flex items-center justify-center shadow-lg shadow-indigo-100">
              <Scale className="h-5 w-5 text-white" />
            </div>
            <span className="font-black text-lg tracking-tight text-slate-900 truncate">
              LAW<span className="text-indigo-600">AUTO</span>
            </span>
          </div>
        ) : (
          <div className="h-10 w-10 rounded-xl bg-indigo-600 flex items-center justify-center shadow-md">
            <Scale className="h-5 w-5 text-white" />
          </div>
        )}
      </div>

      {/* Main Nav */}
      <nav className="flex-1 space-y-2 p-4">
        {navigation.map((item) => (
          <NavLink
            key={item.name}
            to={item.href}
            className={({ isActive }) =>
              cn(
                "group flex items-center rounded-2xl py-3 text-sm font-bold transition-all duration-300",
                !isEffectivelyOpen ? 'justify-center px-0' : 'px-4',
                isActive
                  ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-100'
                  : 'text-slate-500 hover:bg-slate-50 hover:text-indigo-600'
              )
            }
          >
            <item.icon className={cn("h-5 w-5 shrink-0 transition-all", isEffectivelyOpen && "mr-3")} />
            {isEffectivelyOpen && <span className="truncate">{item.name}</span>}
          </NavLink>
        ))}
      </nav>

      {/* Bottom Nav */}
      <div className="mt-auto p-4 space-y-2 border-t border-slate-50">
        <NavLink
          to="/settings"
          className={({ isActive }) =>
            cn(
              "group flex items-center rounded-2xl py-3 text-sm font-bold transition-all duration-300",
              !isEffectivelyOpen ? 'justify-center px-0' : 'px-4',
              isActive
                ? 'bg-slate-100 text-slate-900'
                : 'text-slate-500 hover:bg-slate-50 hover:text-indigo-600'
            )
          }
        >
          <Settings className={cn("h-5 w-5 shrink-0", isEffectivelyOpen && "mr-3")} />
          {isEffectivelyOpen && <span className="truncate">Ayarlar</span>}
        </NavLink>

        <button
          onClick={handleLogout}
          className={cn(
            "w-full group flex items-center rounded-2xl py-3 text-sm font-bold transition-all duration-300 text-red-500 hover:bg-red-50",
            !isEffectivelyOpen ? 'justify-center px-0' : 'px-4'
          )}
        >
          <LogOut className={cn("h-5 w-5 shrink-0", isEffectivelyOpen && "mr-3")} />
          {isEffectivelyOpen && <span className="truncate">Çıkış Yap</span>}
        </button>

        {/* Collapse Toggle at Bottom */}
        <button
          onClick={() => setIsCollapsed(!isCollapsed)}
          className={cn(
            "w-full mt-4 flex items-center justify-center h-10 rounded-xl text-slate-400 hover:bg-slate-50 hover:text-slate-600 transition-all",
            !isEffectivelyOpen && "hidden"
          )}
        >
          {isCollapsed ? <PanelLeft className="h-5 w-5" /> : <PanelLeftClose className="h-5 w-5" />}
        </button>
      </div>
    </div>
  );
}

function cn(...classes) {
  return classes.filter(Boolean).join(' ');
}
