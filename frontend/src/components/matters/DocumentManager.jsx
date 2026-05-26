import React, { useState } from 'react';
import { useDocuments, useUploadDocument } from '@/hooks/useDocuments';
import { FileText, Upload, Loader2, Trash2, FileCheck, FileSearch, Sparkles } from 'lucide-react';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import { cn } from '@/lib/utils';

export function DocumentManager({ matterId }) {
  const { data: documents = [], isLoading } = useDocuments(matterId);
  const uploadMutation = useUploadDocument();
  const [dragActive, setDragActive] = useState(false);

  const handleFileChange = (e) => {
    const file = e.target.files?.[0];
    if (file) {
      uploadMutation.mutate({ matterId, file });
    }
  };

  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true);
    } else if (e.type === "dragleave") {
      setDragActive(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      uploadMutation.mutate({ matterId, file: e.dataTransfer.files[0] });
    }
  };

  return (
    <div className="space-y-6">
      <Card className="border-border bg-muted/50">
        <CardContent className="p-0">
          <div 
            onDragEnter={handleDrag}
            onDragLeave={handleDrag}
            onDragOver={handleDrag}
            onDrop={handleDrop}
            className={cn(
              "relative border-2 border-dashed rounded-xl p-10 flex flex-col items-center justify-center transition-all duration-300",
              dragActive ? "border-primary bg-primary/5" : "border-border bg-card"
            )}
          >
            <input 
              type="file" 
              className="absolute inset-0 w-full h-full opacity-0 cursor-pointer" 
              onChange={handleFileChange}
              disabled={uploadMutation.isPending}
            />
            <div className="h-16 w-16 rounded-xl bg-primary/10 text-primary flex items-center justify-center mb-4">
              <Upload className={cn("h-8 w-8", uploadMutation.isPending && "animate-bounce")} />
            </div>
            <div className="text-center">
              <p className="text-sm font-medium text-foreground">Dosya Yüklemek İçin Tıklayın veya Sürükleyin</p>
              <p className="text-xs text-muted-foreground mt-1">PDF, Word, Görüntü (Maks. 10MB)</p>
            </div>

            {uploadMutation.isPending && (
              <div className="absolute inset-0 bg-card/80 backdrop-blur-sm rounded-xl flex flex-col items-center justify-center p-10">
                <Loader2 className="h-10 w-10 animate-spin text-primary mb-4" />
                <p className="text-sm font-medium text-foreground">Dosya Yükleniyor...</p>
                <Progress value={45} className="w-48 mt-4" />
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {isLoading ? (
          Array.from({ length: 3 }).map((_, i) => (
            <div key={i} className="h-32 rounded-xl bg-muted animate-pulse" />
          ))
        ) : documents.length === 0 ? (
          <div className="col-span-full h-48 flex flex-col items-center justify-center text-muted-foreground bg-muted/50 rounded-xl border border-dashed border-border">
            <FileText className="h-10 w-10 mb-2 opacity-20" />
            <p className="text-sm">Henüz doküman yüklenmemiş.</p>
          </div>
        ) : (
          documents.map((doc) => (
            <motion.div
              key={doc.id}
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              className="bg-card p-5 rounded-xl border border-border hover:border-primary/20 transition-all group"
            >
              <div className="flex items-start gap-4">
                <div className="h-12 w-12 rounded-xl bg-muted text-muted-foreground flex items-center justify-center group-hover:bg-primary/10 group-hover:text-primary transition-colors">
                  <FileText className="h-6 w-6" />
                </div>
                <div className="flex-1 min-w-0">
                  <h4 className="text-sm font-medium text-foreground truncate" title={doc.filename}>
                    {doc.filename}
                  </h4>
                  <p className="text-[10px] text-muted-foreground font-medium uppercase mt-1">
                    {(doc.size / 1024).toFixed(1)} KB &bull; {doc.contentType?.split('/')[1] || 'Bilinmiyor'}
                  </p>
                  
                  <div className="flex items-center gap-2 mt-3">
                    <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-emerald-100 dark:bg-emerald-900/30 text-[10px] font-medium text-emerald-700 dark:text-emerald-300">
                      <FileCheck className="h-3 w-3" /> OCR TAMAM
                    </span>
                    {doc.aiProcessed && (
                      <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-primary/10 text-[10px] font-medium text-primary">
                        <Sparkles className="h-3 w-3" /> AI ÖZET
                      </span>
                    )}
                  </div>
                </div>
              </div>
              
              <div className="mt-5 flex items-center gap-2 pt-4 border-t border-border">
                <Button variant="ghost" size="sm" className="flex-1 h-9 rounded-lg hover:bg-primary/10 hover:text-primary">
                  <FileSearch className="h-4 w-4 mr-2" /> İncele
                </Button>
                <Button variant="ghost" size="sm" className="h-9 w-9 p-0 rounded-lg hover:bg-destructive/10 hover:text-destructive">
                  <Trash2 className="h-4 w-4" />
                </Button>
              </div>
            </motion.div>
          ))
        )}
      </div>
    </div>
  );
}
