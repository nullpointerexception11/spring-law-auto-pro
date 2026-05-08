import React, { useState } from 'react';
import {
  flexRender,
  getCoreRowModel,
  useReactTable,
  getSortedRowModel,
  getFilteredRowModel,
} from '@tanstack/react-table';
import { ArrowUpDown, MoreHorizontal } from 'lucide-react';

const mockData = [
  {
    id: "MAT-2026-001",
    displayId: "2026/114",
    title: "Stark Industries Birleşme ve Devralma",
    status: "AKTİF",
    clientName: "Stark Industries",
    assignedLawyerName: "Harvey Specter",
    nextHearingDate: "2026-05-15",
  },
  {
    id: "MAT-2026-002",
    displayId: "2026/089",
    title: "Wayne Enterprises vs. Gotham City",
    status: "BEKLEMEDE",
    clientName: "Wayne Enterprises",
    assignedLawyerName: "Bruce Wayne",
    nextHearingDate: "2026-06-02",
  },
  {
    id: "MAT-2026-003",
    displayId: "2025/442",
    title: "Pied Piper Telif Hakkı İhlali Davası",
    status: "KAPALI",
    clientName: "Pied Piper",
    assignedLawyerName: "Jared Dunn",
    nextHearingDate: null,
  },
  {
    id: "MAT-2026-004",
    displayId: "2026/210",
    title: "Los Pollos Hermanos Vergi Denetimi",
    status: "AKTİF",
    clientName: "Los Pollos Hermanos",
    assignedLawyerName: "Saul Goodman",
    nextHearingDate: "2026-05-10",
  }
];

const StatusBadge = ({ status }) => {
  const getStyles = () => {
    switch (status) {
      case 'AKTİF': return 'bg-success/10 text-success border-success/20';
      case 'BEKLEMEDE': return 'bg-warning/10 text-warning border-warning/20';
      case 'KAPALI': return 'bg-muted text-muted-foreground border-border';
      default: return 'bg-secondary text-secondary-foreground border-border';
    }
  };
  return (
    <span className={`inline-flex items-center px-2 py-0.5 rounded text-[11px] font-medium border uppercase tracking-wider ${getStyles()}`}>
      {status}
    </span>
  );
};

export function MatterTable() {
  const [sorting, setSorting] = useState([]);
  const [globalFilter, setGlobalFilter] = useState('');

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
      cell: info => <span className="text-muted-foreground truncate block max-w-[150px]">{info.getValue()}</span>,
    },
    {
      accessorKey: 'assignedLawyerName',
      header: () => <div className="w-[150px]">SORUMLU AVUKAT</div>,
      cell: info => <span className="text-muted-foreground truncate block max-w-[150px]">{info.getValue()}</span>,
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
        return <div className="w-[140px]">{val ? <span className="text-sm">{val}</span> : <span className="text-muted-foreground/50">-</span>}</div>;
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
    data: mockData,
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
                  className="bg-card hover:bg-muted/50 transition-colors group cursor-pointer"
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
