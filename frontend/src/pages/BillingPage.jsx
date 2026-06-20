import React, { memo, useDeferredValue, useMemo, useState } from 'react';
import {
  Receipt,
  Download,
  Eye,
  Search,
  CheckCircle2,
  Clock,
  AlertCircle,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { cn } from '@/lib/utils';

const MOCK_INVOICES = [
  { id: 1, number: 'INV-2026-0042', matter: 'İşçi Alacakları Davası', amount: 12500, date: '2026-05-25', dueDate: '2026-06-25', status: 'paid' },
  { id: 2, number: 'INV-2026-0041', matter: 'Tazminat Davası', amount: 8750, date: '2026-05-15', dueDate: '2026-06-15', status: 'pending' },
  { id: 3, number: 'INV-2026-0040', matter: 'Sözleşme İhtilafı', amount: 15200, date: '2026-05-01', dueDate: '2026-06-01', status: 'overdue' },
  { id: 4, number: 'INV-2026-0039', matter: 'İşçi Alacakları Davası', amount: 3400, date: '2026-04-20', dueDate: '2026-05-20', status: 'paid' },
  { id: 5, number: 'INV-2026-0038', matter: 'Tazminat Davası', amount: 6000, date: '2026-04-10', dueDate: '2026-05-10', status: 'paid' },
  { id: 6, number: 'INV-2026-0037', matter: 'Sözleşme İhtilafı', amount: 9300, date: '2026-03-28', dueDate: '2026-04-28', status: 'paid' },
];

const STATUS_CONFIG = {
  paid: { label: 'Ödendi', variant: 'success', icon: CheckCircle2 },
  pending: { label: 'Bekliyor', variant: 'warning', icon: Clock },
  overdue: { label: 'Gecikmiş', variant: 'destructive', icon: AlertCircle },
};

const CURRENCY_FORMATTER = new Intl.NumberFormat('tr-TR', { style: 'currency', currency: 'TRY' });
const DATE_FORMATTER = new Intl.DateTimeFormat('tr-TR');

const SummaryCard = memo(function SummaryCard({ icon: Icon, label, value, toneClassName }) {
  return (
    <Card>
      <CardContent className="p-5">
        <div className="flex items-center gap-3 mb-3">
          <div className={cn('h-9 w-9 rounded-lg flex items-center justify-center', toneClassName)}>
            <Icon className="h-4 w-4" />
          </div>
          <span className="text-[11px] font-medium text-muted-foreground uppercase tracking-wider">{label}</span>
        </div>
        <p className="text-2xl font-semibold text-foreground">{value}</p>
      </CardContent>
    </Card>
  );
});

const InvoiceRow = memo(function InvoiceRow({ invoice }) {
  const status = STATUS_CONFIG[invoice.status];
  const StatusIcon = status.icon;
  const isOverdue = invoice.status === 'overdue';

  return (
    <tr
      className={cn(
        'border-b border-border hover:bg-muted/30 transition-colors',
        isOverdue && 'bg-destructive/5'
      )}
    >
      <td className="px-4 py-3">
        <span className="text-sm font-mono text-foreground">{invoice.number}</span>
      </td>
      <td className="px-4 py-3">
        <span className="text-sm text-foreground">{invoice.matter}</span>
      </td>
      <td className="px-4 py-3 text-right">
        <span className="text-sm font-medium text-foreground">{CURRENCY_FORMATTER.format(invoice.amount)}</span>
      </td>
      <td className="px-4 py-3">
        <span className="text-sm text-muted-foreground">{DATE_FORMATTER.format(new Date(invoice.date))}</span>
      </td>
      <td className="px-4 py-3">
        <span className={cn('text-sm', isOverdue ? 'text-destructive font-medium' : 'text-muted-foreground')}>
          {DATE_FORMATTER.format(new Date(invoice.dueDate))}
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
          <Button variant="ghost" size="icon" className="h-7 w-7" disabled title="Önizleme">
            <Eye className="h-3.5 w-3.5" />
          </Button>
          <Button variant="ghost" size="icon" className="h-7 w-7" disabled title="Önizleme">
            <Download className="h-3.5 w-3.5" />
          </Button>
        </div>
      </td>
    </tr>
  );
});

function BillingPageComponent() {
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const deferredSearch = useDeferredValue(search);

  const filteredInvoices = useMemo(() => {
    const term = deferredSearch.trim().toLowerCase();

    return MOCK_INVOICES.filter((invoice) => {
      const matchesSearch =
        !term ||
        invoice.number.toLowerCase().includes(term) ||
        invoice.matter.toLowerCase().includes(term);
      const matchesStatus = statusFilter === 'all' || invoice.status === statusFilter;
      return matchesSearch && matchesStatus;
    });
  }, [deferredSearch, statusFilter]);

  const { totalRevenue, totalPending, overdueCount } = useMemo(() => {
    let revenue = 0;
    let pending = 0;
    let overdue = 0;

    for (const invoice of MOCK_INVOICES) {
      if (invoice.status === 'paid') {
        revenue += invoice.amount;
      } else if (invoice.status === 'pending' || invoice.status === 'overdue') {
        pending += invoice.amount;
      }

      if (invoice.status === 'overdue') {
        overdue += 1;
      }
    }

    return {
      totalRevenue: revenue,
      totalPending: pending,
      overdueCount: overdue,
    };
  }, []);

  const filterOptions = useMemo(
    () => [
      { key: 'all', label: 'Tümü' },
      { key: 'paid', label: 'Ödenen' },
      { key: 'pending', label: 'Bekleyen' },
      { key: 'overdue', label: 'Geciken' },
    ],
    []
  );

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-xl font-semibold text-foreground">Finans</h1>
            <Badge variant="outline" className="text-[10px] border-amber-200 bg-amber-50 text-amber-700 dark:border-amber-900 dark:bg-amber-950/40 dark:text-amber-300">
              Önizleme
            </Badge>
          </div>
          <p className="text-sm text-muted-foreground mt-0.5">Fatura ve ödemelerinizi takip edin.</p>
        </div>
        <Button size="sm" variant="outline" disabled title="Bu ekran şu anda backend'e bağlı değil">
          <Receipt className="h-4 w-4 mr-1.5" /> Yeni Fatura
        </Button>
      </div>

      <Card className="border-amber-200 bg-amber-50/60 dark:border-amber-900/60 dark:bg-amber-950/20">
        <CardContent className="flex items-start gap-3 p-4">
          <Badge variant="outline" className="shrink-0 text-[10px] border-amber-200 bg-background text-amber-700 dark:border-amber-900 dark:text-amber-300">
            Önizleme
          </Badge>
          <p className="text-sm text-amber-900/90 dark:text-amber-100/90">
            Faturalama bölümü henüz gerçek muhasebe servisine bağlı değil. Rapor ve aksiyonlar demo amaçlı tutuluyor.
          </p>
        </CardContent>
      </Card>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <SummaryCard icon={CheckCircle2} label="Tahsil Edilen" value={CURRENCY_FORMATTER.format(totalRevenue)} toneClassName="bg-emerald-100 dark:bg-emerald-900 text-emerald-600 dark:text-emerald-400" />
        <SummaryCard icon={Clock} label="Bekleyen" value={CURRENCY_FORMATTER.format(totalPending)} toneClassName="bg-amber-100 dark:bg-amber-900 text-amber-600 dark:text-amber-400" />
        <SummaryCard icon={Receipt} label="Toplam Fatura" value={MOCK_INVOICES.length} toneClassName="bg-blue-100 dark:bg-blue-900 text-blue-600 dark:text-blue-400" />
        <SummaryCard icon={AlertCircle} label="Gecikmiş" value={overdueCount} toneClassName="bg-red-100 dark:bg-red-900 text-red-600 dark:text-red-400" />
      </div>

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
          {filterOptions.map((filter) => (
            <button
              key={filter.key}
              type="button"
              onClick={() => setStatusFilter(filter.key)}
              className={cn(
                'px-3 py-1 text-xs font-medium rounded-sm transition-colors',
                statusFilter === filter.key ? 'bg-background text-foreground shadow-sm' : 'text-muted-foreground hover:text-foreground'
              )}
            >
              {filter.label}
            </button>
          ))}
        </div>
      </div>

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
                {filteredInvoices.map((invoice) => (
                  <InvoiceRow key={invoice.id} invoice={invoice} />
                ))}
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

export default memo(BillingPageComponent);
