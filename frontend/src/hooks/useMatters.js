import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '@/api/client';
import { toast } from 'sonner';

/**
 * Hook for fetching the list of matters
 */
export const useMatters = () => {
  return useQuery({
    queryKey: ['matters'],
    queryFn: async () => {
      const response = await api.get('/matters');
      // Standardizing response: handle both paged and raw list responses
      return response.data.content || response.data || [];
    },
    staleTime: 60_000,
    gcTime: 10 * 60_000,
  });
};

/**
 * Hook for fetching a single matter detail
 */
export const useMatter = (id) => {
  return useQuery({
    queryKey: ['matters', id],
    queryFn: async () => {
      const response = await api.get(`/matters/${id}`);
      return response.data;
    },
    enabled: !!id,
    staleTime: 30_000,
    gcTime: 10 * 60_000,
  });
};

/**
 * Hook for creating a new matter with Optimistic Updates
 */
export const useCreateMatter = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (newMatter) => {
      // Logic for tag processing (moved from component to hook)
      const processed = {
        ...newMatter,
        tags: typeof newMatter.tags === 'string' 
          ? newMatter.tags.split(',').map(t => t.trim()).filter(Boolean) 
          : newMatter.tags
      };
      const response = await api.post('/matters', processed);
      return response.data;
    },
    
    // Step 2: Optimistic Update
    onMutate: async (newMatter) => {
      // Cancel any outgoing refetches
      await queryClient.cancelQueries({ queryKey: ['matters'] });

      // Snapshot the previous value
      const previousMatters = queryClient.getQueryData(['matters']);

      // Optimistically update to the new value
      queryClient.setQueryData(['matters'], (old = []) => [
        { 
          id: 'temp-' + Date.now(), 
          ...newMatter, 
          status: 'PENDING',
          displayId: 'NEW' 
        },
        ...old,
      ]);

      return { previousMatters };
    },

    // If the mutation fails, use the context returned from onMutate to roll back
    onError: (err, newMatter, context) => {
      queryClient.setQueryData(['matters'], context.previousMatters);
      toast.error('Dava oluşturulamadı: ' + (err.message || 'Bilinmeyen hata'));
    },

    // Always refetch after error or success:
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ['matters'] });
    },
    
    onSuccess: () => {
      toast.success('Dava başarıyla oluşturuldu.');
    }
  });
};
