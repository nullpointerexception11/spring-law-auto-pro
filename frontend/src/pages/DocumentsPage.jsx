import React, { useEffect, useMemo, useState } from 'react';
import { FileText, FolderOpen, Upload, Sparkles, FileCheck } from 'lucide-react';
import { useMatters } from '@/hooks/useMatters';
import { DocumentManager } from '@/components/matters/DocumentManager';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent } from '@/components/ui/card';

export default function DocumentsPage() {
  const { data: pageData, isLoading } = useMatters({ page: 0, size: 100 });
  const matters = pageData?.content || [];
  const [selectedMatterId, setSelectedMatterId] = useState('');

  useEffect(() => {
    if (!selectedMatterId && matters.length > 0) {
      setSelectedMatterId(String(matters[0].id));
    }
  }, [matters, selectedMatterId]);

  const selectedMatter = useMemo(
    () => matters.find((matter) => String(matter.id) === String(selectedMatterId)),
    [matters, selectedMatterId]
  );

  const matterCount = pageData?.totalElements ?? matters.length;

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-16">
        <div className="h-6 w-6 animate-spin rounded-full border-2 border-primary border-t-transparent" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <h1 className="text-xl font-semibold text-foreground">Belgeler</h1>
          <p className="text-sm text-muted-foreground mt-0.5">
            Seçili dava için yükleme, OCR ve AI özet akışını yönetin.
          </p>
        </div>

        <div className="w-full lg:w-96">
          <label className="mb-1 block text-[11px] font-medium uppercase tracking-wider text-muted-foreground">
            Dava seç
          </label>
          <select
            value={selectedMatterId}
            onChange={(e) => setSelectedMatterId(e.target.value)}
            className="h-10 w-full rounded-lg border border-input bg-background px-3 text-sm"
          >
            {matters.map((matter) => (
              <option key={matter.id} value={matter.id}>
                {matter.title}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card>
          <CardContent className="p-5">
            <div className="flex items-center gap-3 mb-3">
              <div className="h-9 w-9 rounded-lg bg-primary/10 text-primary flex items-center justify-center">
                <FolderOpen className="h-4 w-4" />
              </div>
              <span className="text-[11px] font-medium text-muted-foreground uppercase tracking-wider">Toplam dava</span>
            </div>
            <p className="text-2xl font-semibold text-foreground">{matterCount}</p>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-5">
            <div className="flex items-center gap-3 mb-3">
              <div className="h-9 w-9 rounded-lg bg-emerald-100 dark:bg-emerald-900 text-emerald-600 dark:text-emerald-400 flex items-center justify-center">
                <Upload className="h-4 w-4" />
              </div>
              <span className="text-[11px] font-medium text-muted-foreground uppercase tracking-wider">Yükleme</span>
            </div>
            <p className="text-sm text-foreground">Dosyalar doğrudan seçili davaya eklenir.</p>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-5">
            <div className="flex items-center gap-3 mb-3">
              <div className="h-9 w-9 rounded-lg bg-violet-100 dark:bg-violet-900 text-violet-600 dark:text-violet-400 flex items-center justify-center">
                <Sparkles className="h-4 w-4" />
              </div>
              <span className="text-[11px] font-medium text-muted-foreground uppercase tracking-wider">AI akışı</span>
            </div>
            <p className="text-sm text-foreground">OCR ve AI özeti otomatik olarak takip edilir.</p>
          </CardContent>
        </Card>
      </div>

      {selectedMatter ? (
        <div className="space-y-4">
          <div className="rounded-xl border border-border bg-card p-5">
            <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
              <div>
                <div className="flex items-center gap-2">
                  <h2 className="text-base font-semibold text-foreground">{selectedMatter.title}</h2>
                  <Badge variant="outline" className="text-[10px] font-mono">
                    {selectedMatter.displayId || selectedMatter.referenceNumber || 'N/A'}
                  </Badge>
                </div>
                <p className="text-sm text-muted-foreground mt-1">
                  {selectedMatter.summary || 'Bu dava için henüz bir özet girilmemiş.'}
                </p>
              </div>

              <div className="text-sm text-muted-foreground">
                <span className="font-medium text-foreground">Müvekkil:</span>{' '}
                {selectedMatter.clientName || 'Belirtilmemiş'}
              </div>
            </div>
          </div>

          <DocumentManager matterId={selectedMatterId} />
        </div>
      ) : (
        <Card className="border-dashed">
          <CardContent className="flex flex-col items-center justify-center py-12 gap-3">
            <FileText className="h-10 w-10 text-muted-foreground/40" />
            <p className="text-sm text-muted-foreground">Belgeleri görmek için bir dava seçin.</p>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
