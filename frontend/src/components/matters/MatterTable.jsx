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
import { ArrowUpDown, MoreHorizontal, Loader2, Search, Filter, ChevronRight } from 'lucide-react';
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

const StatusBadge = ({ status }) => {
  const s = status?.toUpperCase() || 'UNKNOWN';
  const variants = {
    'ACTIVE': 'bg-emerald-50 text-emerald-700 border-emerald-100',
    'AKTİF': 'bg-emerald-50 text-emerald-700 border-emerald-100',
    'PENDING': 'bg-amber-50 text-amber-700 border-amber-100',
    'BEKLEMEDE': 'bg-amber-50 text-amber-700 border-amber-100',
    'CLOSED': 'bg-slate-50 text-slate-600 border-slate-200',
    'KAPALI': 'bg-slate-50 text-slate-600 border-slate-200',
  };
  
  return (
    <Badge variant="outline" className={cn("rounded-lg px-2 py-0.5 font-bold text-[10px] uppercase tracking-wider", variants[s] || "bg-slate-50 text-slate-500")}>
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
      cell: info => <span className="font-mono text-[10px] font-bold text-slate-400 bg-slate-50 px-2 py-1 rounded-md border border-slate-100">{info.getValue() || 'N/A'}</span>,
      size: 100,
    },
    {
      accessorKey: 'title',
      header: ({ column }) => (
        <button
          onClick={() => column.toggleSorting(column.getIsSorted() === 'asc')}
          className="flex items-center gap-2 hover:text-indigo-600 transition-colors font-bold"
        >
          DAVA BAŞLIĞI
          <ArrowUpDown className="h-3 w-3" />
        </button>
      ),
      cell: info => (
        <div className="flex flex-col min-w-0">
          <span className="font-bold text-slate-900 truncate text-sm">{info.getValue()}</span>
          <span className="text-[10px] text-slate-400 font-medium truncate">{info.row.original.summary || 'Özet belirtilmemiş'}</span>
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
        <div className="flex flex-col text-[11px] font-medium text-slate-500">
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
              <Button variant="ghost" className="h-8 w-8 p-0 rounded-xl">
                <MoreHorizontal className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="rounded-2xl p-2 border-slate-100 shadow-xl">
              <DropdownMenuLabel className="text-[10px] font-bold text-slate-400 uppercase tracking-widest px-2 py-1.5">İşlemler</DropdownMenuLabel>
              <DropdownMenuItem onClick={() => navigate(`/matters/${row.original.id}`)} className="rounded-xl focus:bg-indigo-50 focus:text-indigo-600 cursor-pointer">
                Detayları Görüntüle
              </DropdownMenuItem>
              <DropdownMenuItem className="rounded-xl focus:bg-indigo-50 focus:text-indigo-600 cursor-pointer">
                Dosyayı Düzenle
              </DropdownMenuItem>
              <DropdownMenuSeparator className="bg-slate-50" />
              <DropdownMenuItem className="rounded-xl focus:bg-red-50 focus:text-red-600 cursor-pointer text-red-500">
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

  // Virtualization logic
  const rowVirtualizer = useVirtualizer({
    count: rows.length,
    getScrollElement: () => tableContainerRef.current,
    estimateSize: () => 72, // Estimated height of each row
    overscan: 10,
  });

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center h-96 space-y-4 bg-white rounded-[32px] border border-slate-100">
        <div className="relative">
          <div className="h-12 w-12 rounded-2xl bg-indigo-50 border border-indigo-100 animate-pulse" />
          <Loader2 className="h-6 w-6 animate-spin text-indigo-600 absolute inset-0 m-auto" />
        </div>
        <p className="text-sm font-bold text-slate-400 animate-pulse uppercase tracking-widest">Veriler Senkronize Ediliyor...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center h-96 space-y-4 bg-red-50/50 rounded-[32px] border border-red-100">
        <div className="h-12 w-12 rounded-2xl bg-red-100 flex items-center justify-center text-red-600">
          <Filter className="h-6 w-6" />
        </div>
        <div className="text-center">
          <p className="text-red-600 font-bold">Veri Bağlantısı Kesildi</p>
          <p className="text-xs text-red-400 mt-1">Sunucuya ulaşılamıyor, lütfen sayfayı yenileyin.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Search and Filters Bar */}
      <div className="flex flex-col md:flex-row gap-4 items-center justify-between">
        <div className="relative w-full md:w-96 group">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400 group-focus-within:text-indigo-600 transition-colors" />
          <input
            value={globalFilter ?? ''}
            onChange={e => setGlobalFilter(e.target.value)}
            placeholder="Dava başlığı, müvekkil veya kayıt no ile ara..."
            className="h-12 w-full pl-12 pr-4 bg-white border border-slate-200 rounded-2xl text-sm focus:outline-none focus:ring-4 focus:ring-indigo-500/10 focus:border-indigo-300 transition-all placeholder:text-slate-400 font-medium"
          />
        </div>
        <div className="flex items-center gap-2">
           <Button variant="outline" className="h-12 rounded-2xl px-6 border-slate-200 hover:bg-slate-50 font-bold text-slate-600 shadow-sm">
             <Filter className="h-4 w-4 mr-2" /> Filtreler
           </Button>
        </div>
      </div>

      {/* Virtualized Table Container */}
      <div 
        ref={tableContainerRef}
        className="bg-white rounded-[32px] border border-slate-100 shadow-sm overflow-auto max-h-[700px] scroll-smooth no-scrollbar"
      >
        <table className="w-full text-left border-separate border-spacing-0">
          <thead className="sticky top-0 z-20 bg-white/80 backdrop-blur-md">
            {table.getHeaderGroups().map(headerGroup => (
              <tr key={headerGroup.id}>
                {headerGroup.headers.map(header => (
                  <th 
                    key={header.id} 
                    className="px-6 py-5 text-[10px] font-bold text-slate-400 uppercase tracking-widest border-b border-slate-50"
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
                  className="group absolute w-full bg-white hover:bg-slate-50/80 transition-all cursor-pointer"
                  style={{ transform: `translateY(${virtualRow.start}px)` }}
                >
                  {row.getVisibleCells().map(cell => (
                    <td key={cell.id} className="px-6 py-4 border-b border-slate-50 align-middle">
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </td>
                  ))}
                </tr>
              );
            })}
            {rows.length === 0 && (
              <tr>
                <td colSpan={columns.length} className="h-48 text-center">
                  <div className="flex flex-col items-center justify-center text-slate-400 space-y-2">
                    <Search className="h-8 w-8 opacity-20" />
                    <p className="text-sm font-medium">Aradığınız kriterlere uygun dava bulunamadı.</p>
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

function cn(...classes) {
  return classes.filter(Boolean).join(' ');
}
