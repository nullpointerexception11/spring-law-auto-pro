import React, { useState, useCallback } from 'react';
import { Search, X } from 'lucide-react';
import { ragService } from '@/api/ragService';

export function RagSearchPanel({ isOpen, onClose }) {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [isSearching, setIsSearching] = useState(false);
  const [hasSearched, setHasSearched] = useState(false);

  const handleSearch = useCallback(async () => {
    if (!query.trim() || isSearching) return;
    setIsSearching(true);
    setHasSearched(true);
    try {
      const data = await ragService.hybridSearch(query);
      setResults(data || []);
    } catch {
      setResults([]);
    } finally {
      setIsSearching(false);
    }
  }, [query, isSearching]);

  if (!isOpen) return null;

  return (
    <div className="h-full flex flex-col bg-white border-l border-border">
      <div className="px-4 py-3 border-b border-border flex items-center justify-between">
        <h3 className="text-sm font-medium text-foreground">Hukuki Kaynak Taraması</h3>
        <button onClick={onClose} className="p-1 rounded hover:bg-muted transition-colors">
          <X className="w-4 h-4 text-muted-foreground" />
        </button>
      </div>

      <div className="p-4 border-b border-border">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <input
            type="text"
            placeholder="Kanun, içtihat veya hukuki terim ara..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
            disabled={isSearching}
            className="w-full h-9 pl-9 pr-3 rounded-md border border-input bg-background text-sm focus:outline-none focus:ring-1 focus:ring-ring"
          />
        </div>
      </div>

      <div className="flex-1 overflow-y-auto p-4">
        {isSearching && (
          <p className="text-sm text-muted-foreground italic">Aranıyor...</p>
        )}

        {!isSearching && hasSearched && results.length === 0 && (
          <p className="text-sm text-muted-foreground">Sonuç bulunamadı.</p>
        )}

        {results.map((result) => (
          <div key={result.chunkId} className="mb-3 p-3 rounded-md border border-border">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-medium text-foreground">{result.sourceName}</span>
              {result.sourceReference && (
                <span className="text-[10px] text-muted-foreground">{result.sourceReference}</span>
              )}
            </div>
            <p className="text-xs text-muted-foreground line-clamp-3">{result.content}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
