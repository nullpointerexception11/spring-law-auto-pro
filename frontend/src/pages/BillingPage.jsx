import React, { useState } from 'react';
import { 
  CreditCard, Receipt, Download, Eye, Calendar, Filter, Search, 
  CheckCircle2, Clock, AlertCircle, FileText, ArrowUpRight, IndianRupee
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { cn } from '@/lib/utils';

const MOCK_INVOICES = [
  { id: 1, number: 'INV-2026-0042', matter: 'İşçi Alacakları Davası', amount: '12,500.00', date: '2026-05-25', dueDate: '2026-06-25', status: 'paid' },
  { id: 2, number: 'INV-2026-0041', matter: 'Tazminat Davası', amount: '8,750.00', date: '2026-05-15', dueDate: '2026-06-15', status: 'pending' },
  { id: 3, number: 'INV-2026-0040', matter: 'Sözleşme İhtilafı', amount: '15,200.00', date: '2026-05-01', dueDate: '2026-06-01', status: 'overdue' },
  { id: 4, number: 'INV-2026-0039', matter: 'İşçi Alacakları Davası', amount: '3,400.00', date: '2026-04-20', dueDate: '2026-05-20', status: 'paid' },
  { id: 5, number: 'INV-2026-0038', matter: 'Tazminat Davası', amount: '6,000.00', date: '2026-04-10', dueDate: '2026-05-10', status: 'paid' },
  { id: 6, number: 'INV-2026-0037', matter: 'Sözleşme İhtilafı', amount: '9,300.00', date: '2026-03-28', dueDate: '2026-04-28', status: 'paid' },
];

const STATUS_CONFIG = {
  paid: { label: 'Ödendi', variant: 'success', icon: CheckCircle2 },
  pending: { label: 'Bekliyor', variant: 'warning', icon: Clock },
  overdue: { label: 'Gecikmiş', variant: 'destructive', icon: AlertCircle },
};

export default function BillingPage() {
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');

  const filteredInvoices = MOCK_INVOICES.filter(inv => {
    const matchesSearch = inv.number.toLowerCase().includes(search.toLowerCase()) ||
      inv.matter.toLowerCase().includes(search.toLowerCase());
    const matchesStatus = statusFilter === 'all' || inv.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  const totalRevenue = MOCK_INVOICES
    .filter(i => i.status === 'paid')
    .reduce((sum, i) => sum + parseFloat(i.amount.replace(/,/g, '')), 0);

  const totalPending = MOCK_INVOICES
    .filter(i => i.status === 'pending' || i.status === 'overdue')
    .reduce((sum, i) => sum + parseFloat(i.amount.replace(/,/g, '')), 0);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold text-foreground">Finans</h1>
          <p className="text-sm text-muted-foreground mt-0.5">Fatura ve ödemelerinizi takip edin.</p>
        </div>
        <Button size="sm">
          <Receipt className="h-4 w-4 mr-1.5" /> Yeni Fatura
        </Button>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <Card>
          <CardContent className="p-5">
            <div className="flex items-center gap-3 mb-3">
              <div className="h-9 w-9 rounded-lg bg-emerald-100 dark:bg-emerald-900 text-emerald-600 dark:text-emerald-400 flex items-center justify-center">
                <CheckCircle2 className="h-4 w-4" />
              </div>
              <span className="text-[11px] font-medium text-muted-foreground uppercase tracking-wider">Tahsil Edilen</span>
            </div>
            <p className="text-2xl font-semibold text-foreground">
              {new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(totalRevenue)}
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-5">
            <div className="flex items-center gap-3 mb-3">
              <div className="h-9 w-9 rounded-lg bg-amber-100 dark:bg-amber-900 text-amber-600 dark:text-amber-400 flex items-center justify-center">
                <Clock className="h-4 w-4" />
              </div>
              <span className="text-[11px] font-medium text-muted-foreground uppercase tracking-wider">Bekleyen</span>
            </div>
            <p className="text-2xl font-semibold text-foreground">
              {new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(totalPending)}
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-5">
            <div className="flex items-center gap-3 mb-3">
              <div className="h-9 w-9 rounded-lg bg-blue-100 dark:bg-blue-900 text-blue-600 dark:text-blue-400 flex items-center justify-center">
                <Receipt className="h-4 w-4" />
              </div>
              <span className="text-[11px] font-medium text-muted-foreground uppercase tracking-wider">Toplam Fatura</span>
            </div>
            <p className="text-2xl font-semibold text-foreground">{MOCK_INVOICES.length}</p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-5">
            <div className="flex items-center gap-3 mb-3">
              <div className="h-9 w-9 rounded-lg bg-red-100 dark:bg-red-900 text-red-600 dark:text-red-400 flex items-center justify-center">
                <AlertCircle className="h-4 w-4" />
              </div>
              <span className="text-[11px] font-medium text-muted-foreground uppercase tracking-wider">Gecikmiş</span>
            </div>
            <p className="text-2xl font-semibold text-foreground">
              {MOCK_INVOICES.filter(i => i.status === 'overdue').length}
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Search & Filters */}
      <div className="flex items-center gap-3">
        <div className="relative flex-1 max-w-sm">
          <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Fatura veya dava ara..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-8 h-9 text-sm"
          />
        </div>
        <div className="flex items-center gap-1 bg-muted rounded-md p-0.5">
          {[
            { key: 'all', label: 'Tümü' },
            { key: 'paid', label: 'Ödenen' },
            { key: 'pending', label: 'Bekleyen' },
            { key: 'overdue', label: 'Geciken' },
          ].map(f => (
            <button
              key={f.key}
              onClick={() => setStatusFilter(f.key)}
              className={cn(
                'px-3 py-1 text-xs font-medium rounded-sm transition-colors',
                statusFilter === f.key 
                  ? 'bg-background text-foreground shadow-sm' 
                  : 'text-muted-foreground hover:text-foreground'
              )}
            >
              {f.label}
            </button>
          ))}
        </div>
      </div>

      {/* Invoice Table */}
      <Card>
        <CardContent className="p-0">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-border">
                  <th className="text-left text-[11px] font-medium text-muted-foreground px-4 py-3 uppercase tracking-wider">Fatura No</th>
                  <th className="text-left text-[11px] font-medium text-muted-foreground px-4 py-3 uppercase tracking-wider">Dava</th>
                  <th className="text-right text-[11px] font-medium text-muted-foreground px-4 py-3 uppercase tracking-wider">Tutar</th>
                  <th className="text-left text-[11px] font-medium text-muted-foreground px-4 py-3 uppercase tracking-wider">Tarih</th>
                  <th className="text-left text-[11px] font-medium text-muted-foreground px-4 py-3 uppercase tracking-wider">Son Ödeme</th>
                  <th className="text-center text-[11px] font-medium text-muted-foreground px-4 py-3 uppercase tracking-wider">Durum</th>
                  <th className="text-right text-[11px] font-medium text-muted-foreground px-4 py-3 uppercase tracking-wider">İşlem</th>
                </tr>
              </thead>
              <tbody>
                {filteredInvoices.map(invoice => {
                  const status = STATUS_CONFIG[invoice.status];
                  const StatusIcon = status.icon;
                  const isOverdue = invoice.status === 'overdue';
                  return (
                    <tr key={invoice.id} className={cn(
                      'border-b border-border hover:bg-muted/30 transition-colors',
                      isOverdue && 'bg-destructive/5'
                    )}>
                      <td className="px-4 py-3">
                        <span className="text-sm font-mono text-foreground">{invoice.number}</span>
                      </td>
                      <td className="px-4 py-3">
                        <span className="text-sm text-foreground">{invoice.matter}</span>
                      </td>
                      <td className="px-4 py-3 text-right">
                        <span className="text-sm font-medium text-foreground">
                          {new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' }).format(parseFloat(invoice.amount.replace(/,/g, '')))}
                        </span>
                      </td>
                      <td className="px-4 py-3">
                        <span className="text-sm text-muted-foreground">
                          {new Date(invoice.date).toLocaleDateString('tr-TR')}
                        </span>
                      </td>
                      <td className="px-4 py-3">
                        <span className={cn(
                          'text-sm',
                          isOverdue ? 'text-destructive font-medium' : 'text-muted-foreground'
                        )}>
                          {new Date(invoice.dueDate).toLocaleDateString('tr-TR')}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-center">
                        <Badge variant={status.variant} className="text-[10px] gap-1">
                          <StatusIcon className="h-3 w-3" />
                          {status.label}
                        </Badge>
                      </td>
                      <td className="px-4 py-3 text-right">
                        <div className="flex items-center justify-end gap-1">
                          <Button variant="ghost" size="icon" className="h-7 w-7">
                            <Eye className="h-3.5 w-3.5" />
                          </Button>
                          <Button variant="ghost" size="icon" className="h-7 w-7">
                            <Download className="h-3.5 w-3.5" />
                          </Button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
          {filteredInvoices.length === 0 && (
            <div className="text-center py-12">
              <Receipt className="h-8 w-8 text-muted-foreground mx-auto mb-2" />
              <p className="text-sm text-muted-foreground">Fatura bulunamadı</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
