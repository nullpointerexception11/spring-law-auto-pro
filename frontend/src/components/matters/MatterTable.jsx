import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import {
  flexRender,
  getCoreRowModel,
  useReactTable,
  getSortedRowModel,
  getFilteredRowModel,
} from '@tanstack/react-table';
import { ArrowUpDown, MoreHorizontal, Loader2 } from 'lucide-react';
import api from '../../lib/api';

const StatusBadge = ({ status }) => {
  const getStyles = () => {
    switch (status?.toUpperCase()) {
      case 'ACTIVE': 
      case 'AKTİF': return 'bg-success/10 text-success border-success/20';
      case 'PENDING': 
      case 'BEKLEMEDE': return 'bg-warning/10 text-warning border-warning/20';
      case 'CLOSED': 
      case 'KAPALI': return 'bg-muted text-muted-foreground border-border';
      default: return 'bg-secondary text-secondary-foreground border-border';
    }
  };
  return (
    <span className={`inline-flex items-center px-2 py-0.5 rounded text-[11px] font-medium border uppercase tracking-wider ${getStyles()}`}>
      {status || 'Bilinmiyor'}
    </span>
  );
};

export function MatterTable() {
  const [sorting, setSorting] = useState([]);
  const [globalFilter, setGlobalFilter] = useState('');
  const navigate = useNavigate();

  const { data: matters = [], isLoading, error } = useQuery({
    queryKey: ['matters'],
    queryFn: async () => {
      const response = await api.get('/matters');
      return response.data.content || [];
    }
  });

  const columns = [
    {
      accessorKey: 'displayId',
      header: () => <div className="w-[80px]">KAYIT NO</div>,
      cell: info => <span className="font-mono text-xs text-muted-foreground">{info.getValue()}</span>,
    },
    {
      accessorKey: 'title',
      header: ({ column }) => (
        <button
          onClick={() => column.toggleSorting(column.getIsSorted() === 'asc')}
          className="flex items-center gap-1 hover:text-foreground transition-colors -ml-2 p-2 rounded-md hover:bg-secondary/50 w-[280px]"
        >
          DAVA BAŞLIĞI
          <ArrowUpDown className="h-3 w-3" />
        </button>
      ),
      cell: info => <span className="font-medium text-foreground truncate block max-w-[280px]">{info.getValue()}</span>,
    },
    {
      accessorKey: 'clientName',
      header: () => <div className="w-[150px]">MÜVEKKİL</div>,
      cell: info => <span className="text-muted-foreground truncate block max-w-[150px]">{info.getValue() || '-'}</span>,
    },
    {
      accessorKey: 'assignedLawyerName',
      header: () => <div className="w-[150px]">SORUMLU AVUKAT</div>,
      cell: info => <span className="text-muted-foreground truncate block max-w-[150px]">{info.getValue() || '-'}</span>,
    },
    {
      accessorKey: 'status',
      header: () => <div className="w-[100px]">DURUM</div>,
      cell: info => <StatusBadge status={info.getValue()} />,
    },
    {
      accessorKey: 'nextHearingDate',
      header: () => <div className="w-[140px]">SONRAKİ DURUŞMA</div>,
      cell: info => {
        const val = info.getValue();
        if (!val) return <div className="w-[140px] text-muted-foreground/50">-</div>;
        
        try {
          const date = new Date(val);
          if (isNaN(date.getTime())) return <div className="w-[140px] text-muted-foreground/50">-</div>;
          
          return (
            <div className="w-[140px] flex flex-col">
              <span className="text-sm font-medium">{date.toLocaleDateString('tr-TR')}</span>
              <span className="text-[10px] text-muted-foreground uppercase">{date.toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' })}</span>
            </div>
          );
        } catch (e) {
          return <div className="w-[140px] text-muted-foreground/50">-</div>;
        }
      },
    },
    {
      id: 'actions',
      cell: () => (
        <div className="w-10 flex justify-end">
          <button className="p-1.5 rounded-md hover:bg-secondary text-muted-foreground transition-colors">
            <MoreHorizontal className="h-4 w-4" />
          </button>
        </div>
      ),
    }
  ];

  const table = useReactTable({
    data: matters,
    columns,
    state: {
      sorting,
      globalFilter,
    },
    onSortingChange: setSorting,
    onGlobalFilterChange: setGlobalFilter,
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
  });

  if (isLoading) {
    return (
      <div className="flex flex-col items-center justify-center h-64 space-y-4 text-muted-foreground">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
        <p className="text-sm font-medium animate-pulse">Davalar yükleniyor...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center h-64 space-y-2 border-2 border-dashed border-destructive/20 rounded-md bg-destructive/5">
        <p className="text-destructive font-semibold">Veriler çekilemedi</p>
        <p className="text-xs text-muted-foreground">Lütfen backend sunucusunun çalıştığından emin olun.</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <input
          value={globalFilter ?? ''}
          onChange={e => setGlobalFilter(e.target.value)}
          placeholder="Davaları filtrele..."
          className="h-9 w-72 rounded-md border border-input bg-card px-3 py-1 text-sm shadow-sm transition-colors placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
        />
      </div>

      <div className="table-container shadow-sm border border-border max-h-[600px] relative overflow-auto rounded-md">
        <table className="w-full text-sm text-left table-fixed">
          <thead className="text-xs text-muted-foreground bg-secondary/80 uppercase border-b border-border sticky top-0 z-10 backdrop-blur-sm">
            {table.getHeaderGroups().map(headerGroup => (
              <tr key={headerGroup.id}>
                {headerGroup.headers.map(header => (
                  <th 
                    key={header.id} 
                    className="px-4 py-3 font-medium tracking-wide"
                    style={{ width: header.getSize() !== 150 ? header.getSize() : undefined }}
                  >
                    {header.isPlaceholder
                      ? null
                      : flexRender(
                          header.column.columnDef.header,
                          header.getContext()
                        )}
                  </th>
                ))}
              </tr>
            ))}
          </thead>
          <tbody className="divide-y divide-border">
            {table.getRowModel().rows.length ? (
              table.getRowModel().rows.map(row => (
                <tr 
                  key={row.id} 
                  onClick={() => navigate(`/matters/${row.original.id}`)}
                  className="bg-card hover:bg-muted/80 transition-colors group cursor-pointer"
                >
                  {row.getVisibleCells().map(cell => (
                    <td key={cell.id} className="px-4 py-3 align-middle overflow-hidden">
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </td>
                  ))}
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={columns.length} className="h-24 text-center text-muted-foreground">
                  Filtrenizle eşleşen dava bulunamadı.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
