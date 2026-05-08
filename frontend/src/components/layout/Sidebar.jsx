import React from 'react';
import { NavLink } from 'react-router-dom';
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
  PanelLeft
} from 'lucide-react';

const navigation = [
  { name: 'Dashboard', href: '/', icon: Scale },
  { name: 'Matters', href: '/matters', icon: Briefcase },
  { name: 'Calendar', href: '/calendar', icon: Calendar },
  { name: 'Documents', href: '/documents', icon: FileText },
  { name: 'Billing', href: '/billing', icon: CreditCard },
  { name: 'AI Research', href: '/ai', icon: Bot },
];

const bottomNavigation = [
  { name: 'Notifications', href: '/notifications', icon: Bell },
  { name: 'Settings', href: '/settings', icon: Settings },
];

export function Sidebar({ isCollapsed, setIsCollapsed }) {
  return (
    <div 
      className={`flex h-full flex-col border-r border-border bg-card transition-all duration-300 ease-in-out ${
        isCollapsed ? 'w-16' : 'w-56'
      }`}
    >
      {/* Brand */}
      <div className={`flex items-center h-16 border-b border-border/50 ${isCollapsed ? 'justify-center px-0' : 'px-5'}`}>
        <Scale className={`h-6 w-6 text-primary shrink-0 transition-all ${isCollapsed ? '' : 'mr-3'}`} />
        {!isCollapsed && (
          <span className="text-lg font-semibold tracking-tight text-foreground truncate fade-enter-active">
            Prestige
          </span>
        )}
      </div>

      {/* Main Nav */}
      <nav className="flex-1 space-y-1 p-3">
        {navigation.map((item) => (
          <NavLink
            key={item.name}
            to={item.href}
            title={isCollapsed ? item.name : undefined}
            className={({ isActive }) =>
              `group flex items-center rounded-md py-2 text-sm font-medium transition-colors ${
                isCollapsed ? 'justify-center px-0' : 'px-3'
              } ${
                isActive
                  ? 'bg-secondary text-secondary-foreground'
                  : 'text-muted-foreground hover:bg-secondary/50 hover:text-foreground'
              }`
            }
          >
            <item.icon className={`h-5 w-5 shrink-0 ${isCollapsed ? '' : 'mr-3'}`} />
            {!isCollapsed && <span className="truncate fade-enter-active">{item.name}</span>}
          </NavLink>
        ))}
      </nav>

      {/* Bottom Nav */}
      <div className="mt-auto space-y-1 p-3 border-t border-border/50">
        {bottomNavigation.map((item) => (
          <NavLink
            key={item.name}
            to={item.href}
            title={isCollapsed ? item.name : undefined}
            className={({ isActive }) =>
              `group flex items-center rounded-md py-2 text-sm font-medium transition-colors ${
                isCollapsed ? 'justify-center px-0' : 'px-3'
              } ${
                isActive
                  ? 'bg-secondary text-secondary-foreground'
                  : 'text-muted-foreground hover:bg-secondary/50 hover:text-foreground'
              }`
            }
          >
            <item.icon className={`h-5 w-5 shrink-0 ${isCollapsed ? '' : 'mr-3'}`} />
            {!isCollapsed && <span className="truncate fade-enter-active">{item.name}</span>}
          </NavLink>
        ))}

        {/* Sidebar Toggle Button at bottom */}
        <button
          onClick={() => setIsCollapsed(!isCollapsed)}
          title={isCollapsed ? "Expand Sidebar" : "Collapse Sidebar"}
          className={`group flex w-full items-center rounded-md py-2 mt-2 text-sm font-medium transition-colors text-muted-foreground hover:bg-secondary/50 hover:text-foreground ${
            isCollapsed ? 'justify-center px-0' : 'px-3'
          }`}
        >
          {isCollapsed ? (
            <PanelLeft className="h-5 w-5 shrink-0" />
          ) : (
            <>
              <PanelLeftClose className="h-5 w-5 shrink-0 mr-3" />
              <span className="truncate fade-enter-active">Collapse</span>
            </>
          )}
        </button>
      </div>
    </div>
  );
}
