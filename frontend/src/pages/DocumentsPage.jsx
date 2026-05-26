import React, { useState } from 'react';
import { FileText, Upload, Search, Filter, Download, Eye, Trash2, FileCheck, FileType } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { cn } from '@/lib/utils';

const MOCK_DOCUMENTS = [
  { id: 1, filename: 'Dava_Dilekcesi_2026.pdf', matter: 'İşçi Alacakları Davası', size: '1.2 MB', type: 'application/pdf', date: '2026-05-20', status: 'processed' },
  { id: 2, filename: 'Bilirkisi_Raporu.docx', matter: 'Tazminat Davası', size: '845 KB', type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', date: '2026-05-18', status: 'processing' },
  { id: 3, filename: 'Tanik_Ifadesi.pdf', matter: 'İşçi Alacakları Davası', size: '320 KB', type: 'application/pdf', date: '2026-05-15', status: 'processed' },
  { id: 4, filename: 'Sozlesme_Metni.docx', matter: 'Sözleşme İhtilafı', size: '1.5 MB', type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', date: '2026-05-12', status: 'processed' },
  { id: 5, filename: 'Istinaf_Dilekcesi.pdf', matter: 'Tazminat Davası', size: '2.1 MB', type: 'application/pdf', date: '2026-05-10', status: 'error' },
  { id: 6, filename: 'Delil_Listesi.xlsx', matter: 'Sözleşme İhtilafı', size: '156 KB', type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', date: '2026-05-08', status: 'processed' },
];

const FILE_ICONS = {
  'application/pdf': { icon: FileCheck, className: 'bg-red-100 text-red-600 dark:bg-red-900 dark:text-red-400' },
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document': { icon: FileText, className: 'bg-blue-100 text-blue-600 dark:bg-blue-900 dark:text-blue-400' },
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': { icon: FileType, className: 'bg-emerald-100 text-emerald-600 dark:bg-emerald-900 dark:text-emerald-400' },
};

const STATUS_CONFIG = {
  processed: { label: 'İşlendi', variant: 'success' },
  processing: { label: 'İşleniyor', variant: 'warning' },
  error: { label: 'Hata', variant: 'destructive' },
};

export default function DocumentsPage() {
  const [search, setSearch] = useState('');
  const [dragActive, setDragActive] = useState(false);

  const filteredDocs = MOCK_DOCUMENTS.filter(doc =>
    doc.filename.toLowerCase().includes(search.toLowerCase()) ||
    doc.matter.toLowerCase().includes(search.toLowerCase())
  );

  const handleDrag = (e) => {
    e.preventDefault(); e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') setDragActive(true);
    else if (e.type === 'dragleave') setDragActive(false);
  };

  const handleDrop = (e) => {
    e.preventDefault(); e.stopPropagation();
    setDragActive(false);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold text-foreground">Belgeler</h1>
          <p className="text-sm text-muted-foreground mt-0.5">Tüm dava dosyalarınızı yönetin ve takip edin.</p>
        </div>
      </div>

      {/* Upload Zone */}
      <div
        onDragEnter={handleDrag}
        onDragLeave={handleDrag}
        onDragOver={handleDrag}
        onDrop={handleDrop}
        className={cn(
          'relative rounded-xl border-2 border-dashed p-8 text-center transition-all',
          dragActive 
            ? 'border-primary bg-primary/5' 
            : 'border-border hover:border-muted-foreground/30 hover:bg-muted/30'
        )}
      >
        <div className="flex flex-col items-center gap-3">
          <div className={cn(
            'h-12 w-12 rounded-full flex items-center justify-center transition-colors',
            dragActive ? 'bg-primary/10 text-primary' : 'bg-muted text-muted-foreground'
          )}>
            <Upload className="h-5 w-5" />
          </div>
          <div>
            <p className="text-sm font-medium text-foreground">
              Dosyaları buraya sürükleyin
            </p>
            <p className="text-xs text-muted-foreground mt-1">
              PDF, Word, Excel (max 25 MB)
            </p>
          </div>
          <Button variant="outline" size="sm" className="mt-2">
            Dosya Seç
          </Button>
        </div>
      </div>

      {/* Search & Filters */}
      <div className="flex items-center gap-3">
        <div className="relative flex-1 max-w-sm">
          <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Dosya ara..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-8 h-9 text-sm"
          />
        </div>
        <Button variant="outline" size="sm" className="h-9">
          <Filter className="h-4 w-4 mr-1.5" /> Filtrele
        </Button>
        <Button variant="outline" size="sm" className="h-9">
          <Download className="h-4 w-4 mr-1.5" /> Dışa Aktar
        </Button>
      </div>

      {/* Document Grid */}
      {filteredDocs.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
          {filteredDocs.map(doc => {
            const fileConfig = FILE_ICONS[doc.type] || { icon: FileText, className: 'bg-muted text-muted-foreground' };
            const FileIcon = fileConfig.icon;
            const status = STATUS_CONFIG[doc.status];
            return (
              <Card key={doc.id} className="hover:border-primary/30 transition-colors group">
                <CardContent className="p-4">
                  <div className="flex items-start gap-3">
                    <div className={cn('h-10 w-10 rounded-lg flex items-center justify-center shrink-0', fileConfig.className)}>
                      <FileIcon className="h-5 w-5" />
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-foreground truncate">{doc.filename}</p>
                      <p className="text-[11px] text-muted-foreground mt-0.5">{doc.matter}</p>
                      <div className="flex items-center gap-2 mt-2">
                        <span className="text-[10px] text-muted-foreground">{doc.size}</span>
                        <span className="text-[10px] text-muted-foreground">
                          {new Date(doc.date).toLocaleDateString('tr-TR')}
                        </span>
                        <Badge variant={status.variant} className="text-[9px] py-0 h-4">
                          {status.label}
                        </Badge>
                      </div>
                    </div>
                    <div className="flex items-center gap-0.5 opacity-0 group-hover:opacity-100 transition-opacity">
                      <Button variant="ghost" size="icon" className="h-7 w-7">
                        <Eye className="h-3.5 w-3.5" />
                      </Button>
                      <Button variant="ghost" size="icon" className="h-7 w-7 text-destructive hover:text-destructive">
                        <Trash2 className="h-3.5 w-3.5" />
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            );
          })}
        </div>
      ) : (
        <Card className="border-dashed">
          <CardContent className="flex flex-col items-center justify-center py-12 gap-3">
            <div className="h-12 w-12 rounded-full bg-muted flex items-center justify-center text-muted-foreground">
              <FileText className="h-6 w-6" />
            </div>
            <p className="text-sm text-muted-foreground">Henüz belge yüklenmemiş</p>
            <Button variant="outline" size="sm">
              <Upload className="h-4 w-4 mr-1.5" /> İlk Belgeyi Yükle
            </Button>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
