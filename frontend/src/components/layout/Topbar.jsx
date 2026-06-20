import React, { memo, useCallback, useEffect, useState } from 'react';
import { Search, Moon, Sun, PanelLeft } from 'lucide-react';

const THEME_STORAGE_KEY = 'lawauto-theme';

const getInitialTheme = () => {
  if (typeof window === 'undefined') return false;

  const storedTheme = window.localStorage.getItem(THEME_STORAGE_KEY);
  if (storedTheme === 'dark') return true;
  if (storedTheme === 'light') return false;

  return document.documentElement.classList.contains('dark');
};

function TopbarComponent({ onToggle }) {
  const [isDark, setIsDark] = useState(getInitialTheme);

  useEffect(() => {
    document.documentElement.classList.toggle('dark', isDark);
    window.localStorage.setItem(THEME_STORAGE_KEY, isDark ? 'dark' : 'light');
  }, [isDark]);

  const toggleDark = useCallback(() => {
    setIsDark((current) => !current);
  }, []);

  return (
    <header className="app-shell-surface flex h-14 items-center rounded-none border-b-0 px-4 md:px-6">
      <div className="flex flex-1 items-center gap-3">
        <button
          onClick={onToggle}
          className="rounded-full border border-border bg-background/60 p-2 text-muted-foreground transition-colors hover:border-primary/20 hover:bg-accent hover:text-foreground md:hidden"
        >
          <PanelLeft className="h-4 w-4" />
        </button>

        <div className="relative hidden w-full max-w-sm md:block">
          <Search className="absolute left-3 top-1/2 h-3.5 w-3.5 -translate-y-1/2 text-muted-foreground" />
          <input
            type="search"
            placeholder="Ara..."
            className="h-9 w-full rounded-full border border-input/80 bg-background/90 pl-9 pr-3 text-xs outline-none transition-all focus:border-primary/40 focus:ring-4 focus:ring-primary/10"
          />
          <kbd className="absolute right-2.5 top-1/2 -translate-y-1/2 rounded-full border border-border bg-muted px-2 py-0.5 font-mono text-[9px] text-muted-foreground">
            ⌘K
          </kbd>
        </div>
      </div>

      <div className="flex items-center gap-2">
        <button
          onClick={toggleDark}
          className="rounded-full border border-border bg-background/60 p-2 text-muted-foreground transition-colors hover:border-primary/20 hover:bg-accent hover:text-foreground"
        >
          {isDark ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
        </button>
        <div className="flex h-8 w-8 items-center justify-center rounded-full border border-border bg-primary/10 text-xs font-semibold text-primary">
          JD
        </div>
      </div>
    </header>
  );
}

export const Topbar = memo(TopbarComponent);
