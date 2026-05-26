import React, { useState, useRef, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  flexRender,
  getCoreRowModel,
  useReactTable,
  getSortedRowModel,
  getFilteredRowModel,
} from '@tanstack/react-table';
import { useVirtualizer } from '@tanstack/react-virtual';
import { ArrowUpDown, MoreHorizontal, Loader2, Search } from 'lucide-react';
import { useMatters } from '@/hooks/useMatters';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { cn } from '@/lib/utils';

const StatusBadge = ({ status }) => {
  const s = status?.toUpperCase() || 'UNKNOWN';
  const variants = {
    'OPEN': 'bg-emerald-100 dark:bg-emerald-900/30 text-emerald-700 dark:text-emerald-300 border-emerald-200 dark:border-emerald-800',
    'AKTİF': 'bg-emerald-100 dark:bg-emerald-900/30 text-emerald-700 dark:text-emerald-300 border-emerald-200 dark:border-emerald-800',
    'PENDING': 'bg-amber-100 dark:bg-amber-900/30 text-amber-700 dark:text-amber-300 border-amber-200 dark:border-amber-800',
    'BEKLEMEDE': 'bg-amber-100 dark:bg-amber-900/30 text-amber-700 dark:text-amber-300 border-amber-200 dark:border-amber-800',
    'CLOSED': 'bg-muted text-muted-foreground border-border',
    'KAPALI': 'bg-muted text-muted-foreground border-border',
  };
  
  return (
    <Badge variant="outline" className={cn("rounded-md px-2 py-0.5 font-medium text-[10px] uppercase tracking-wider", variants[s] || "bg-muted text-muted-foreground")}>
      {status || 'Bilinmiyor'}
    </Badge>
  );
};

export function MatterTable() {
  const [sorting, setSorting] = useState([]);
  const [globalFilter, setGlobalFilter] = useState('');
  const navigate = useNavigate();
  const tableContainerRef = useRef(null);
  const dateFormatter = useMemo(() => new Intl.DateTimeFormat('tr-TR'), []);

  const { data: matters = [], isLoading, error } = useMatters();

  const columns = useMemo(() => [
    {
      accessorKey: 'displayId',
      header: 'KAYIT NO',
      cell: info => <span className="font-mono text-[10px] font-medium text-muted-foreground bg-muted px-2 py-1 rounded-md border border-border">{info.getValue() || 'N/A'}</span>,
      size: 100,
    },
    {
      accessorKey: 'title',
      header: ({ column }) => (
        <button
          onClick={() => column.toggleSorting(column.getIsSorted() === 'asc')}
          className="flex items-center gap-2 hover:text-primary transition-colors font-medium"
        >
          DAVA BAŞLIĞI
          <ArrowUpDown className="h-3 w-3" />
        </button>
      ),
      cell: info => (
        <div className="flex flex-col min-w-0">
          <span className="font-medium text-foreground truncate text-sm">{info.getValue()}</span>
          <span className="text-[10px] text-muted-foreground truncate">{info.row.original.summary || 'Özet belirtilmemiş'}</span>
        </div>
      ),
      size: 350,
    },
    {
      accessorKey: 'status',
      header: 'DURUM',
      cell: info => <StatusBadge status={info.getValue()} />,
      size: 120,
    },
    {
      accessorKey: 'openedAt',
      header: 'AÇILIŞ TARİHİ',
      cell: info => (
        <div className="text-[11px] text-muted-foreground">
          <span>{dateFormatter.format(new Date(info.getValue()))}</span>
        </div>
      ),
      size: 140,
    },
    {
      id: 'actions',
      header: '',
      cell: ({ row }) => (
        <div className="flex justify-end">
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" className="h-8 w-8 p-0">
                <MoreHorizontal className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="p-2">
              <DropdownMenuLabel className="text-[10px] font-medium text-muted-foreground uppercase tracking-widest px-2 py-1.5">İşlemler</DropdownMenuLabel>
              <DropdownMenuItem onClick={() => navigate(`/matters/${row.original.id}`)} className="rounded-md focus:bg-primary/10 focus:text-primary cursor-pointer">
                Detayları Görüntüle
              </DropdownMenuItem>
              <DropdownMenuItem className="rounded-md focus:bg-primary/10 focus:text-primary cursor-pointer">
                Dosyayı Düzenle
              </DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem className="rounded-md focus:bg-destructive/10 focus:text-destructive cursor-pointer text-destructive">
                Dosyayı Arşivle
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      ),
      size: 60,
    }
  ], [dateFormatter, navigate]);

  const table = useReactTable({
    data: matters,
    columns,
    state: { sorting, globalFilter },
    onSortingChange: setSorting,
    onGlobalFilterChange: setGlobalFilter,
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
  });

  const { rows } = table.getRowModel();

  const rowVirtualizer = useVirtualizer({
    count: rows.length,
    getScrollElement: () => tableContainerRef.current,
    estimateSize: () => 64,
    overscan: 10,
  });

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center h-96 space-y-4 rounded-xl border border-border bg-card">
        <div className="relative">
          <div className="h-12 w-12 rounded-xl bg-primary/10 border border-primary/20 animate-pulse" />
          <Loader2 className="h-6 w-6 animate-spin text-primary absolute inset-0 m-auto" />
        </div>
        <p className="text-sm text-muted-foreground animate-pulse uppercase tracking-widest">Veriler Yükleniyor...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center h-96 space-y-4 rounded-xl border border-destructive/20 bg-destructive/5">
        <div className="h-12 w-12 rounded-xl bg-destructive/20 flex items-center justify-center text-destructive">
          <Search className="h-6 w-6" />
        </div>
        <div className="text-center">
          <p className="text-destructive font-medium">Veri Bağlantısı Kesildi</p>
          <p className="text-xs text-muted-foreground mt-1">Sunucuya ulaşılamıyor, lütfen sayfayı yenileyin.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex flex-col md:flex-row gap-3 items-center justify-between">
        <div className="relative w-full md:w-96">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <input
            value={globalFilter ?? ''}
            onChange={e => setGlobalFilter(e.target.value)}
            placeholder="Dava başlığı, müvekkil veya kayıt no ile ara..."
            className="h-10 w-full pl-10 pr-4 bg-card border border-input rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-ring focus:border-primary transition-all placeholder:text-muted-foreground"
          />
        </div>
      </div>

      <div 
        ref={tableContainerRef}
        className="bg-card rounded-xl border border-border overflow-auto max-h-[700px]"
      >
        <table className="w-full text-left">
          <thead className="sticky top-0 z-20 bg-card/95 backdrop-blur-sm">
            {table.getHeaderGroups().map(headerGroup => (
              <tr key={headerGroup.id}>
                {headerGroup.headers.map(header => (
                  <th 
                    key={header.id} 
                    className="px-6 py-3.5 text-[10px] font-medium text-muted-foreground uppercase tracking-widest border-b border-border"
                    style={{ width: header.getSize() }}
                  >
                    {flexRender(header.column.columnDef.header, header.getContext())}
                  </th>
                ))}
              </tr>
            ))}
          </thead>
          <tbody 
            className="relative"
            style={{ height: `${rowVirtualizer.getTotalSize()}px` }}
          >
            {rowVirtualizer.getVirtualItems().map(virtualRow => {
              const row = rows[virtualRow.index];
              return (
                <tr 
                  key={row.id}
                  data-index={virtualRow.index}
                  ref={node => rowVirtualizer.measureElement(node)}
                  onClick={() => navigate(`/matters/${row.original.id}`)}
                  className="group absolute w-full bg-card hover:bg-accent/50 transition-colors cursor-pointer"
                  style={{ transform: `translateY(${virtualRow.start}px)` }}
                >
                  {row.getVisibleCells().map(cell => (
                    <td key={cell.id} className="px-6 py-3 border-b border-border align-middle">
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </td>
                  ))}
                </tr>
              );
            })}
            {rows.length === 0 && (
              <tr>
                <td colSpan={columns.length} className="h-48 text-center">
                  <div className="flex flex-col items-center justify-center text-muted-foreground space-y-2">
                    <Search className="h-8 w-8 opacity-20" />
                    <p className="text-sm">Aradığınız kriterlere uygun dava bulunamadı.</p>
                  </div>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
