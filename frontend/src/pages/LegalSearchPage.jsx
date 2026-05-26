import React, { useState } from 'react';
import { Search, BookOpen, Scale, FileText, Filter, Loader2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent } from '@/components/ui/card';
import { cn } from '@/lib/utils';
import { api } from '@/api/client';

const SOURCE_TYPES = [
  { key: 'all', label: 'Tümü' },
  { key: 'YARGITAY', label: 'Yargıtay' },
  { key: 'AYM', label: 'AYM' },
  { key: 'AIHM', label: 'AİHM' },
  { key: 'DOKTRIN', label: 'Doktrin' },
  { key: 'KANUN', label: 'Mevzuat' },
  { key: 'YONETMELIK', label: 'Yönetmelik' },
];

export default function LegalSearchPage() {
  const [query, setQuery] = useState('');
  const [sourceFilter, setSourceFilter] = useState('all');
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);

  const handleSearch = async () => {
    if (!query.trim()) return;
    setLoading(true);
    setSearched(true);
    try {
      const res = await api.post('/rag/search', { query: query.trim(), limit: 20 });
      const data = res.data || [];
      if (sourceFilter !== 'all') {
        setResults(data.filter(r => r.sourceType === sourceFilter));
      } else {
        setResults(data);
      }
    } catch (e) {
      setResults([]);
    } finally {
      setLoading(false);
    }
  };

  const filteredResults = sourceFilter === 'all'
    ? results
    : results.filter(r => r.sourceType === sourceFilter);

  const sourceIcon = (type) => {
    switch (type) {
      case 'YARGITAY': return <Scale className="h-4 w-4" />;
      case 'AYM': return <Scale className="h-4 w-4" />;
      case 'AIHM': return <Scale className="h-4 w-4" />;
      case 'DOKTRIN': return <BookOpen className="h-4 w-4" />;
      default: return <FileText className="h-4 w-4" />;
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-xl font-semibold text-foreground">Hukuk Arama</h1>
        <p className="text-sm text-muted-foreground mt-0.5">AI destekli Yargıtay, AYM, AİHM karar ve doktrin araması.</p>
      </div>

      <div className="flex flex-col gap-4">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Anahtar kelime, kanun maddesi veya esas numarası ile arayın..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
            className="pl-10 h-11 text-sm pr-24"
          />
          {query && (
            <Button size="sm" onClick={handleSearch} disabled={loading} className="absolute right-1.5 top-1/2 -translate-y-1/2 h-8">
              {loading ? <Loader2 className="h-3.5 w-3.5 animate-spin" /> : <Search className="h-3.5 w-3.5 mr-1.5" />}
              {!loading && 'Ara'}
            </Button>
          )}
        </div>

        <div className="flex items-center gap-2 flex-wrap">
          {SOURCE_TYPES.map(s => (
            <button
              key={s.key}
              onClick={() => setSourceFilter(s.key)}
              className={cn(
                'px-3 py-1.5 text-xs font-medium rounded-md transition-colors',
                sourceFilter === s.key
                  ? 'bg-primary/10 text-primary'
                  : 'text-muted-foreground hover:text-foreground hover:bg-muted'
              )}
            >
              {s.label}
            </button>
          ))}
        </div>
      </div>

      {loading && (
        <div className="flex items-center justify-center py-16">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      )}

      {!loading && searched && filteredResults.length > 0 && (
        <div className="space-y-3">
          <p className="text-xs text-muted-foreground">{filteredResults.length} sonuç bulundu</p>
          {filteredResults.map((r, i) => (
            <Card key={r.chunkId || i}>
              <CardContent className="p-4">
                <div className="flex items-start gap-3">
                  <div className="mt-0.5 h-8 w-8 rounded-md bg-primary/10 flex items-center justify-center shrink-0">
                    {sourceIcon(r.sourceType)}
                  </div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1">
                      <p className="font-medium text-sm text-foreground">{r.sourceName}</p>
                      <Badge variant="outline" className="text-[10px]">{r.sourceType}</Badge>
                      {r.score > 0 && (
                        <span className="text-[10px] text-muted-foreground/60">
                          %{(r.score * 100).toFixed(0)} uyumlu
                        </span>
                      )}
                    </div>
                    {r.sourceReference && (
                      <p className="text-xs text-muted-foreground mb-1">{r.sourceReference}</p>
                    )}
                    <p className="text-sm text-foreground/80 line-clamp-3">{r.content}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}

      {!loading && searched && filteredResults.length === 0 && (
        <div className="flex items-center justify-center py-16">
          <div className="text-center space-y-3">
            <Search className="h-10 w-10 text-muted-foreground/30 mx-auto" />
            <p className="text-sm text-muted-foreground">Sonuç bulunamadı</p>
          </div>
        </div>
      )}

      {!searched && (
        <div className="flex items-center justify-center py-20">
          <div className="text-center space-y-3">
            <Search className="h-12 w-12 text-muted-foreground/40 mx-auto" />
            <h3 className="text-lg font-medium text-muted-foreground">Henüz bir arama yapmadınız</h3>
            <p className="text-sm text-muted-foreground/70 max-w-md">
              Yargıtay kararları, AYM içtihatları, AİHM kararları ve doktrin kaynaklarında
              AI destekli anlamsal arama yapmak için yukarıdaki alanı kullanın.
            </p>
          </div>
        </div>
      )}
    </div>
  );
}
