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
    title: "Stark Industries Merger Acquisition",
    status: "ACTIVE",
    clientName: "Stark Industries",
    assignedLawyerName: "Harvey Specter",
    nextHearingDate: "2026-05-15",
  },
  {
    id: "MAT-2026-002",
    displayId: "2026/089",
    title: "Wayne Enterprises vs. City of Gotham",
    status: "PENDING",
    clientName: "Wayne Enterprises",
    assignedLawyerName: "Bruce Wayne",
    nextHearingDate: "2026-06-02",
  },
  {
    id: "MAT-2026-003",
    displayId: "2025/442",
    title: "Pied Piper Copyright Infringement",
    status: "CLOSED",
    clientName: "Pied Piper",
    assignedLawyerName: "Jared Dunn",
    nextHearingDate: null,
  },
  {
    id: "MAT-2026-004",
    displayId: "2026/210",
    title: "Los Pollos Hermanos Tax Audit",
    status: "ACTIVE",
    clientName: "Los Pollos Hermanos",
    assignedLawyerName: "Saul Goodman",
    nextHearingDate: "2026-05-10",
  }
];

// Minimalist Status Badge component following the "Calm Enterprise" guidelines
const StatusBadge = ({ status }) => {
  const getStyles = () => {
    switch (status) {
      case 'ACTIVE': return 'bg-success/10 text-success border-success/20';
      case 'PENDING': return 'bg-warning/10 text-warning border-warning/20';
      case 'CLOSED': return 'bg-muted text-muted-foreground border-border';
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
      header: 'ID',
      cell: info => <span className="font-mono text-xs text-muted-foreground">{info.getValue()}</span>,
    },
    {
      accessorKey: 'title',
      header: ({ column }) => (
        <button
          onClick={() => column.toggleSorting(column.getIsSorted() === 'asc')}
          className="flex items-center gap-1 hover:text-foreground transition-colors -ml-2 p-2 rounded-md hover:bg-secondary/50"
        >
          Matter Title
          <ArrowUpDown className="h-3 w-3" />
        </button>
      ),
      cell: info => <span className="font-medium text-foreground">{info.getValue()}</span>,
    },
    {
      accessorKey: 'clientName',
      header: 'Client',
      cell: info => <span className="text-muted-foreground">{info.getValue()}</span>,
    },
    {
      accessorKey: 'assignedLawyerName',
      header: 'Lead Counsel',
      cell: info => <span className="text-muted-foreground">{info.getValue()}</span>,
    },
    {
      accessorKey: 'status',
      header: 'Status',
      cell: info => <StatusBadge status={info.getValue()} />,
    },
    {
      accessorKey: 'nextHearingDate',
      header: 'Next Hearing',
      cell: info => {
        const val = info.getValue();
        return val ? <span className="text-sm">{val}</span> : <span className="text-muted-foreground/50">-</span>;
      },
    },
    {
      id: 'actions',
      cell: () => (
        <button className="p-1.5 rounded-md hover:bg-secondary text-muted-foreground transition-colors">
          <MoreHorizontal className="h-4 w-4" />
        </button>
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
      {/* Table Toolbar / Quick Filter */}
      <div className="flex items-center justify-between">
        <input
          value={globalFilter ?? ''}
          onChange={e => setGlobalFilter(e.target.value)}
          placeholder="Filter matters..."
          className="h-9 w-72 rounded-md border border-input bg-card px-3 py-1 text-sm shadow-sm transition-colors placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
        />
      </div>

      {/* Table Container - Sticky Header & Custom Scrollbar */}
      <div className="table-container shadow-sm border border-border max-h-[600px] relative overflow-auto">
        <table className="w-full text-sm text-left">
          <thead className="text-xs text-muted-foreground bg-secondary/80 uppercase border-b border-border sticky top-0 z-10 backdrop-blur-sm">
            {table.getHeaderGroups().map(headerGroup => (
              <tr key={headerGroup.id}>
                {headerGroup.headers.map(header => (
                  <th key={header.id} className="px-4 py-3 font-medium tracking-wide">
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
                    <td key={cell.id} className="px-4 py-3 align-middle">
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </td>
                  ))}
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={columns.length} className="h-24 text-center text-muted-foreground">
                  No matters found matching your filter.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
