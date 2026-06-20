import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import { api } from '@/api/client';
import { toast } from 'sonner';

const DEFAULT_PAGE_SIZE = 20;

export const MATTER_QUERY_KEYS = {
  all: ['matters'],
  list: (page, size) => ['matters', page, size],
  detail: (id) => ['matter', id],
};

const normalizePage = (data, fallbackSize = DEFAULT_PAGE_SIZE) => {
  if (Array.isArray(data)) {
    return {
      content: data,
      number: 0,
      size: data.length || fallbackSize,
      totalElements: data.length,
      totalPages: data.length ? 1 : 0,
      first: true,
      last: true,
      empty: data.length === 0,
    };
  }

  if (data && Array.isArray(data.content)) {
    return {
      ...data,
      content: data.content,
      number: data.number ?? 0,
      size: data.size ?? fallbackSize,
      totalElements: data.totalElements ?? data.content.length,
      totalPages: data.totalPages ?? 1,
      first: Boolean(data.first),
      last: Boolean(data.last),
      empty: Boolean(data.empty),
    };
  }

  return {
    content: [],
    number: 0,
    size: fallbackSize,
    totalElements: 0,
    totalPages: 0,
    first: true,
    last: true,
    empty: true,
  };
};

/**
 * Hook for fetching the list of matters
 */
export const useMatters = ({ page = 0, size = DEFAULT_PAGE_SIZE } = {}) => {
  return useQuery({
    queryKey: MATTER_QUERY_KEYS.list(page, size),
    queryFn: async () => {
      const response = await api.get('/matters', {
        params: { page, size },
      });
      return normalizePage(response.data, size);
    },
    placeholderData: keepPreviousData,
    staleTime: 60_000,
    gcTime: 10 * 60_000,
  });
};

/**
 * Hook for fetching a single matter detail
 */
export const useMatter = (id) => {
  return useQuery({
    queryKey: MATTER_QUERY_KEYS.detail(id),
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
      const processed = {
        ...newMatter,
        tags: typeof newMatter.tags === 'string'
          ? newMatter.tags.split(',').map((tag) => tag.trim()).filter(Boolean)
          : newMatter.tags,
      };
      const response = await api.post('/matters', processed);
      return response.data;
    },
    onMutate: async (newMatter) => {
      await queryClient.cancelQueries({ queryKey: MATTER_QUERY_KEYS.all });

      const previousMatters = queryClient.getQueriesData({ queryKey: MATTER_QUERY_KEYS.all });
      const optimisticMatter = {
        id: `temp-${Date.now()}`,
        title: newMatter.title,
        referenceNumber: newMatter.referenceNumber || '',
        summary: newMatter.summary || '',
        tags: typeof newMatter.tags === 'string'
          ? newMatter.tags.split(',').map((tag) => tag.trim()).filter(Boolean)
          : newMatter.tags || [],
        status: 'PENDING',
        displayId: 'NEW',
      };

      queryClient.setQueriesData({ queryKey: MATTER_QUERY_KEYS.all }, (oldData) => {
        const page = normalizePage(oldData);
        if (page.number !== 0) return oldData;

        return {
          ...page,
          content: [optimisticMatter, ...page.content].slice(0, page.size || DEFAULT_PAGE_SIZE),
          totalElements: (page.totalElements || page.content.length) + 1,
        };
      });

      return { previousMatters };
    },
    onError: (err, _newMatter, context) => {
      if (context?.previousMatters) {
        context.previousMatters.forEach(([queryKey, data]) => {
          queryClient.setQueryData(queryKey, data);
        });
      }
      toast.error('Dava oluşturulamadı: ' + (err.message || 'Bilinmeyen hata'));
    },
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: MATTER_QUERY_KEYS.all });
    },
    onSuccess: () => {
      toast.success('Dava başarıyla oluşturuldu.');
    },
  });
};
