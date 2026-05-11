import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '@/api/client';
import { toast } from 'sonner';

/**
 * Hook to fetch documents for a specific matter
 */
export const useDocuments = (matterId) => {
  return useQuery({
    queryKey: ['documents', matterId],
    queryFn: async () => {
      const response = await api.get(`/documents/matter/${matterId}`);
      return response.data || [];
    },
    enabled: !!matterId,
  });
};

/**
 * Hook to upload a new document
 */
export const useUploadDocument = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ matterId, file }) => {
      const formData = new FormData();
      formData.append('matterId', matterId);
      formData.append('file', file);

      const response = await api.post('/documents/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data;
    },
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['documents', variables.matterId] });
      toast.success('Dosya başarıyla yüklendi.');
    },
    onError: (err) => {
      toast.error('Yükleme başarısız: ' + (err.message || 'Bilinmeyen hata'));
    },
  });
};
