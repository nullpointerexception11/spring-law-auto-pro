import React from 'react';
import { useTranslation } from 'react-i18next';
import { MatterTable } from '../../components/matters/MatterTable';

export default function MatterList() {
  const { t } = useTranslation();

  return (
    <div className="space-y-6 fade-enter-active">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-semibold tracking-tight text-foreground">{t('matter.title')}</h1>
          <p className="text-sm text-muted-foreground mt-1">
            {t('matter.subtitle')}
          </p>
        </div>
        <button className="h-9 px-4 rounded-md bg-primary text-primary-foreground hover:bg-primary/90 text-sm font-medium transition-colors shadow-sm">
          {t('matter.new_matter')}
        </button>
      </div>
      
      {/* TanStack Table Integration */}
      <MatterTable />
      
    </div>
  );
}
