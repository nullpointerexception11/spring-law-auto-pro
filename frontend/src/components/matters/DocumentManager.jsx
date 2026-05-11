import React, { useState } from 'react';
import { useDocuments, useUploadDocument } from '@/hooks/useDocuments';
import { FileText, Upload, Loader2, Download, Trash2, FileCheck, FileSearch } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';

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
      <Card className="border-none shadow-sm bg-slate-50/50">
        <CardContent className="p-0">
          <div 
            onDragEnter={handleDrag}
            onDragLeave={handleDrag}
            onDragOver={handleDrag}
            onDrop={handleDrop}
            className={cn(
              "relative border-2 border-dashed rounded-3xl p-10 flex flex-col items-center justify-center transition-all duration-300",
              dragActive ? "border-indigo-500 bg-indigo-50/50" : "border-slate-200 bg-white"
            )}
          >
            <input 
              type="file" 
              className="absolute inset-0 w-full h-full opacity-0 cursor-pointer" 
              onChange={handleFileChange}
              disabled={uploadMutation.isPending}
            />
            <div className="h-16 w-16 rounded-2xl bg-indigo-50 text-indigo-600 flex items-center justify-center mb-4">
              <Upload className={cn("h-8 w-8", uploadMutation.isPending && "animate-bounce")} />
            </div>
            <div className="text-center">
              <p className="text-sm font-semibold text-slate-900">Dosya Yüklemek İçin Tıklayın veya Sürükleyin</p>
              <p className="text-xs text-slate-500 mt-1">PDF, Word, Görüntü (Maks. 10MB)</p>
            </div>

            {uploadMutation.isPending && (
              <div className="absolute inset-0 bg-white/80 backdrop-blur-sm rounded-3xl flex flex-col items-center justify-center p-10">
                <Loader2 className="h-10 w-10 animate-spin text-indigo-600 mb-4" />
                <p className="text-sm font-bold text-slate-700">Dosya Yükleniyor...</p>
                <Progress value={45} className="w-48 h-2 mt-4" />
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {isLoading ? (
          Array.from({ length: 3 }).map((_, i) => (
            <div key={i} className="h-32 rounded-3xl bg-slate-100 animate-pulse" />
          ))
        ) : documents.length === 0 ? (
          <div className="col-span-full h-48 flex flex-col items-center justify-center text-slate-400 bg-slate-50 rounded-3xl border border-dashed border-slate-200">
            <FileText className="h-10 w-10 mb-2 opacity-20" />
            <p className="text-sm">Henüz doküman yüklenmemiş.</p>
          </div>
        ) : (
          documents.map((doc) => (
            <motion.div
              key={doc.id}
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              className="bg-white p-5 rounded-3xl border border-slate-100 shadow-sm hover:shadow-md transition-all group"
            >
              <div className="flex items-start gap-4">
                <div className="h-12 w-12 rounded-2xl bg-slate-50 text-slate-400 flex items-center justify-center group-hover:bg-indigo-50 group-hover:text-indigo-600 transition-colors">
                  <FileText className="h-6 w-6" />
                </div>
                <div className="flex-1 min-w-0">
                  <h4 className="text-sm font-bold text-slate-900 truncate" title={doc.filename}>
                    {doc.filename}
                  </h4>
                  <p className="text-[10px] text-slate-500 font-medium uppercase mt-1">
                    {(doc.size / 1024).toFixed(1)} KB • {doc.contentType.split('/')[1]}
                  </p>
                  
                  {/* OCR & Status Indicators */}
                  <div className="flex items-center gap-2 mt-3">
                    <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-emerald-50 text-[10px] font-bold text-emerald-600">
                      <FileCheck className="h-3 w-3" /> OCR TAMAM
                    </span>
                    {doc.aiProcessed && (
                      <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded-full bg-indigo-50 text-[10px] font-bold text-indigo-600">
                        <Sparkles className="h-3 w-3" /> AI ÖZET
                      </span>
                    )}
                  </div>
                </div>
              </div>
              
              <div className="mt-5 flex items-center gap-2 pt-4 border-t border-slate-50">
                <Button variant="ghost" size="sm" className="flex-1 h-9 rounded-xl hover:bg-indigo-50 hover:text-indigo-600">
                  <FileSearch className="h-4 w-4 mr-2" /> İncele
                </Button>
                <Button variant="ghost" size="sm" className="h-9 w-9 p-0 rounded-xl hover:bg-red-50 hover:text-red-600">
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

// Helper for class names
function cn(...classes) {
  return classes.filter(Boolean).join(' ');
}
