import React, { useState } from 'react';
import { Search, Moon, Sun, PanelLeft } from 'lucide-react';

export function Topbar({ collapsed, onToggle }) {
  const [isDark, setIsDark] = useState(() => 
    document.documentElement.classList.contains('dark')
  );

  const toggleDark = () => {
    const dark = document.documentElement.classList.toggle('dark');
    setIsDark(dark);
  };

  return (
    <header className="flex items-center h-14 px-4 md:px-6 border-b border-border bg-card">
      <div className="flex items-center gap-3 flex-1">
        <button
          onClick={onToggle}
          className="md:hidden p-1.5 rounded-md hover:bg-accent text-muted-foreground"
        >
          <PanelLeft className="w-4 h-4" />
        </button>

        <div className="relative max-w-sm w-full hidden md:block">
          <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-muted-foreground" />
          <input
            type="search"
            placeholder="Ara..."
            className="w-full h-8 pl-8 pr-3 rounded-md border border-input bg-background text-xs focus:outline-none focus:ring-1 focus:ring-ring"
          />
          <kbd className="absolute right-2 top-1/2 -translate-y-1/2 text-[9px] text-muted-foreground bg-muted px-1 rounded border border-border font-mono">
            ⌘K
          </kbd>
        </div>
      </div>

      <div className="flex items-center gap-2">
        <button
          onClick={toggleDark}
          className="p-1.5 rounded-md hover:bg-accent text-muted-foreground hover:text-foreground transition-colors"
        >
          {isDark ? <Sun className="w-4 h-4" /> : <Moon className="w-4 h-4" />}
        </button>
        <div className="w-7 h-7 rounded-full bg-muted flex items-center justify-center text-xs font-medium text-muted-foreground border border-border">
          JD
        </div>
      </div>
    </header>
  );
}
