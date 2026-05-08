import React from 'react';
import { Search, Plus, Building2, PanelLeft } from 'lucide-react';

export function Topbar({ isSidebarCollapsed, setIsSidebarCollapsed }) {
  return (
    <header className="flex h-16 w-full items-center justify-between border-b border-border bg-background px-4 md:px-6">
      
      <div className="flex flex-1 items-center gap-4">
        {/* Toggle Sidebar Button for Mobile (Hidden on MD since Sidebar handles its own collapse) */}
        <button 
          onClick={() => setIsSidebarCollapsed(!isSidebarCollapsed)}
          className="md:hidden p-2 -ml-2 rounded-md hover:bg-secondary text-muted-foreground hover:text-foreground transition-colors"
          title="Toggle Sidebar"
        >
          <PanelLeft className="h-5 w-5" />
        </button>

        {/* Global Search - Front and Center */}
        <div className="relative w-full max-w-xl hidden md:block group">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground group-focus-within:text-primary transition-colors" />
          <input
            type="search"
            placeholder="Search matters, documents, parties..."
            className="flex h-9 w-full rounded-md border border-input bg-card px-3 py-1 text-sm shadow-sm transition-colors placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring pl-9"
          />
          <div className="absolute right-2 top-1/2 -translate-y-1/2 flex items-center gap-1 pointer-events-none">
            <kbd className="inline-flex h-5 items-center gap-1 rounded border border-border bg-muted px-1.5 font-mono text-[10px] font-medium text-muted-foreground">
              <span className="text-xs">⌘</span>K
            </kbd>
          </div>
        </div>
      </div>

      {/* Right Side Actions */}
      <div className="flex items-center gap-4">
        {/* Org Switcher */}
        <button className="hidden sm:flex items-center gap-2 h-9 px-3 rounded-md border border-input bg-background hover:bg-accent hover:text-accent-foreground text-sm font-medium transition-colors">
          <Building2 className="h-4 w-4 text-muted-foreground" />
          <span>Prestige Law Firm</span>
        </button>

        {/* Quick Add */}
        <button className="flex items-center gap-2 h-9 px-3 rounded-md bg-primary text-primary-foreground hover:bg-primary/90 text-sm font-medium transition-colors shadow-sm">
          <Plus className="h-4 w-4" />
          <span className="hidden sm:block">Quick Add</span>
        </button>

        {/* User Avatar */}
        <div className="h-8 w-8 rounded-full bg-secondary border border-border flex items-center justify-center text-sm font-medium text-secondary-foreground cursor-pointer hover:opacity-80 transition-opacity">
          JD
        </div>
      </div>
    </header>
  );
}
