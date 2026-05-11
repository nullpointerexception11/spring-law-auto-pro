import React from 'react';
import { useForm } from 'react-hook-form';
import { X, Loader2, Plus, Info } from 'lucide-react';
import { useCreateMatter } from '@/hooks/useMatters';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';

export function CreateMatterModal({ isOpen, onClose }) {
  const mutation = useCreateMatter();
  
  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    defaultValues: {
      title: '',
      referenceNumber: '',
      summary: '',
      tags: ''
    }
  });

  const onSubmit = (data) => {
    mutation.mutate(data, {
      onSuccess: () => {
        reset();
        onClose();
      }
    });
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-background/80 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="bg-card w-full max-w-xl rounded-xl border border-border shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
        
        {/* Header */}
        <div className="px-6 py-4 border-b border-border flex items-center justify-between bg-secondary/30">
          <div className="flex items-center gap-2">
            <div className="p-2 bg-primary/10 rounded-lg">
              <Plus className="h-5 w-5 text-primary" />
            </div>
            <div>
              <h2 className="text-lg font-semibold">Yeni Dava Kaydı</h2>
              <p className="text-xs text-muted-foreground">Sistemde yeni bir hukuki dosya oluşturun.</p>
            </div>
          </div>
          <button 
            onClick={onClose}
            className="p-2 hover:bg-secondary rounded-full transition-colors text-muted-foreground hover:text-foreground"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit(onSubmit)} className="flex-1 overflow-y-auto p-6 space-y-5">
          
          <div className="space-y-2">
            <label className="text-sm font-medium leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70">
              Dava Başlığı <span className="text-destructive">*</span>
            </label>
            <input 
              {...register('title', { required: 'Dava başlığı zorunludur' })}
              placeholder="Örn: X Anonim Şirketi - İşçilik Alacakları"
              className={`flex h-10 w-full rounded-md border bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 ${errors.title ? 'border-destructive' : 'border-input'}`}
            />
            {errors.title && <p className="text-xs text-destructive mt-1 font-medium">{errors.title.message}</p>}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-2">
              <label className="text-sm font-medium leading-none">Esas/Referans No</label>
              <input 
                {...register('referenceNumber')}
                placeholder="Örn: 2024/123 E."
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
              />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium leading-none">Etiketler</label>
              <input 
                {...register('tags')}
                placeholder="Virgül ile ayırın (Örn: Tazminat, İş)"
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
              />
            </div>
          </div>

          <div className="space-y-2">
            <label className="text-sm font-medium leading-none">Dava Özeti</label>
            <textarea 
              {...register('summary')}
              placeholder="Davanın kısa özeti veya önemli notları..."
              className="flex min-h-[100px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
            />
          </div>

          <div className="p-3 bg-primary/5 rounded-lg border border-primary/10 flex gap-3">
            <Info className="h-5 w-5 text-primary shrink-0 mt-0.5" />
            <p className="text-xs text-muted-foreground leading-relaxed">
              Dava oluşturulduktan sonra taraflar, duruşmalar ve belgeler gibi detayları davanın kendi sayfasından yönetebilirsiniz.
            </p>
          </div>
        </form>

        {/* Footer */}
        <div className="px-6 py-4 border-t border-border bg-secondary/10 flex items-center justify-end gap-3">
          <button 
            type="button"
            onClick={onClose}
            className="px-4 py-2 text-sm font-medium rounded-md hover:bg-secondary transition-colors"
          >
            İptal
          </button>
          <button 
            onClick={handleSubmit(onSubmit)}
            disabled={mutation.isPending}
            className="px-4 py-2 bg-primary text-primary-foreground rounded-md text-sm font-medium hover:bg-primary/90 transition-all shadow-sm flex items-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {mutation.isPending ? (
              <>
                <Loader2 className="h-4 w-4 animate-spin" />
                Kaydediliyor...
              </>
            ) : (
              'Dava Oluştur'
            )}
          </button>
        </div>
      </div>
    </div>
  );
}
