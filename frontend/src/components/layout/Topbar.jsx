import React, { useState } from 'react';
import { Search, Plus, Building2, PanelLeft, Moon, Sun } from 'lucide-react';

export function Topbar({ isSidebarCollapsed, setIsSidebarCollapsed }) {
  // Initialize dark mode state
  const [isDarkMode, setIsDarkMode] = useState(() => {
    return document.documentElement.classList.contains('dark');
  });

  const toggleDarkMode = () => {
    const isDark = document.documentElement.classList.toggle('dark');
    setIsDarkMode(isDark);
  };

  return (
    <header className="flex h-16 w-full items-center justify-between border-b border-border bg-background px-4 md:px-6 transition-colors duration-300">
      
      <div className="flex flex-1 items-center gap-4">
        {/* Toggle Sidebar Button for Mobile */}
        <button 
          onClick={() => setIsSidebarCollapsed(!isSidebarCollapsed)}
          className="md:hidden p-2 -ml-2 rounded-md hover:bg-secondary text-muted-foreground hover:text-foreground transition-colors"
          title="Menüyü Aç/Kapa"
        >
          <PanelLeft className="h-5 w-5" />
        </button>

        {/* Global Search - Front and Center */}
        <div className="relative w-full max-w-xl hidden md:block group">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground group-focus-within:text-primary transition-colors" />
          <input
            type="search"
            placeholder="Dava, belge, taraf ara..."
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
      <div className="flex items-center gap-3">
        {/* Dark Mode Toggle */}
        <button 
          onClick={toggleDarkMode}
          className="p-2 rounded-md hover:bg-secondary text-muted-foreground hover:text-foreground transition-colors"
          title="Karanlık Mod Değiştir"
        >
          {isDarkMode ? <Sun className="h-5 w-5" /> : <Moon className="h-5 w-5" />}
        </button>

        {/* Org Switcher */}
        <button className="hidden sm:flex items-center gap-2 h-9 px-3 rounded-md border border-input bg-background hover:bg-accent hover:text-accent-foreground text-sm font-medium transition-colors">
          <Building2 className="h-4 w-4 text-muted-foreground" />
          <span>Prestij Hukuk Bürosu</span>
        </button>

        {/* Quick Add */}
        <button className="flex items-center gap-2 h-9 px-3 rounded-md bg-primary text-primary-foreground hover:bg-primary/90 text-sm font-medium transition-colors shadow-sm ml-2">
          <Plus className="h-4 w-4" />
          <span className="hidden sm:block">Hızlı Ekle</span>
        </button>

        {/* User Avatar */}
        <div className="h-8 w-8 ml-1 rounded-full bg-secondary border border-border flex items-center justify-center text-sm font-medium text-secondary-foreground cursor-pointer hover:opacity-80 transition-opacity">
          JD
        </div>
      </div>
    </header>
  );
}
