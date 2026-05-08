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
  const [isHovered, setIsHovered] = React.useState(false);

  // Compute actual state: it's open if it's NOT collapsed, OR if it's hovered.
  const isEffectivelyOpen = !isCollapsed || isHovered;

  return (
    <div 
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      className={`flex h-full flex-col border-r border-border bg-card transition-all duration-300 ease-in-out ${
        isEffectivelyOpen ? 'w-56' : 'w-16'
      }`}
    >
      {/* Brand & Toggle */}
      <div className={`flex items-center h-16 border-b border-border/50 px-3 ${!isEffectivelyOpen ? 'justify-center' : 'justify-between'}`}>
        {isEffectivelyOpen && (
          <div className="flex items-center overflow-hidden">
            <Scale className="h-5 w-5 text-primary shrink-0 mr-2" />
            <span className="font-semibold tracking-tight text-foreground truncate fade-enter-active">
              Prestige
            </span>
          </div>
        )}
        
        <button
          onClick={() => setIsCollapsed(!isCollapsed)}
          title={isCollapsed ? "Pin Sidebar" : "Collapse Sidebar"}
          className="p-1.5 rounded-md text-muted-foreground hover:bg-secondary hover:text-foreground transition-colors shrink-0"
        >
          {isCollapsed ? <PanelLeft className="h-5 w-5" /> : <PanelLeftClose className="h-5 w-5" />}
        </button>
      </div>

      {/* Main Nav */}
      <nav className="flex-1 space-y-1 p-3">
        {navigation.map((item) => (
          <NavLink
            key={item.name}
            to={item.href}
            title={!isEffectivelyOpen ? item.name : undefined}
            className={({ isActive }) =>
              `group flex items-center rounded-md py-2 text-sm font-medium transition-colors ${
                !isEffectivelyOpen ? 'justify-center px-0' : 'px-3'
              } ${
                isActive
                  ? 'bg-secondary text-secondary-foreground'
                  : 'text-muted-foreground hover:bg-secondary/50 hover:text-foreground'
              }`
            }
          >
            <item.icon className={`h-5 w-5 shrink-0 transition-all ${!isEffectivelyOpen ? '' : 'mr-3'}`} />
            {isEffectivelyOpen && <span className="truncate fade-enter-active">{item.name}</span>}
          </NavLink>
        ))}
      </nav>

      {/* Bottom Nav */}
      <div className="mt-auto space-y-1 p-3 border-t border-border/50">
        {bottomNavigation.map((item) => (
          <NavLink
            key={item.name}
            to={item.href}
            title={!isEffectivelyOpen ? item.name : undefined}
            className={({ isActive }) =>
              `group flex items-center rounded-md py-2 text-sm font-medium transition-colors ${
                !isEffectivelyOpen ? 'justify-center px-0' : 'px-3'
              } ${
                isActive
                  ? 'bg-secondary text-secondary-foreground'
                  : 'text-muted-foreground hover:bg-secondary/50 hover:text-foreground'
              }`
            }
          >
            <item.icon className={`h-5 w-5 shrink-0 transition-all ${!isEffectivelyOpen ? '' : 'mr-3'}`} />
            {isEffectivelyOpen && <span className="truncate fade-enter-active">{item.name}</span>}
          </NavLink>
        ))}

      </div>
    </div>
  );
}
