import React, { useState } from 'react';
import { MatterTable } from '../../components/matters/MatterTable';
import { CreateMatterModal } from '../../components/matters/CreateMatterModal';
import { Plus } from 'lucide-react';

export default function MatterList() {
  const [isModalOpen, setIsModalOpen] = useState(false);

  return (
    <div className="space-y-6 fade-enter-active">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-semibold tracking-tight text-foreground">Davalar</h1>
          <p className="text-sm text-muted-foreground mt-1">
            Hukuki davalarınızı, kurumsal projelerinizi ve ihtilaflarınızı yönetin.
          </p>
        </div>
        <button 
          onClick={() => setIsModalOpen(true)}
          className="h-9 px-4 rounded-md bg-primary text-primary-foreground hover:bg-primary/90 text-sm font-medium transition-colors shadow-sm flex items-center gap-2"
        >
          <Plus className="h-4 w-4" /> Yeni Dava
        </button>
      </div>
      
      {/* TanStack Table Integration */}
      <MatterTable />

      <CreateMatterModal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
      />
    </div>
  );
}
