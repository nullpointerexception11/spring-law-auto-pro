import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import { api } from '@/api/client';
import { toast } from 'sonner';

const DEFAULT_PAGE_SIZE = 20;

export const MATTER_QUERY_KEYS = {
  all: ['matters'],
  list: (page, size) => ['matters', page, size],
  detail: (id) => ['matter', id],
  stats: ['matters', 'stats'],
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
 * Hook for the dashboard summary widget (active/pending/closed counts +
 * top-5 recent matters).
 *
 * PERFORMANCE NOTE: this replaces the previous pattern of calling
 * `useMatters({ page: 0, size: 100 })` from the dashboard just to compute
 * 3 counters and slice the 5 most recent rows in the browser. That meant:
 *   - shipping ~100 full matter DTOs over the wire on every dashboard load
 *   - the backend running its (lateral-join-heavy) list query for 100 rows
 *     instead of the 5 actually displayed
 *   - re-deriving the same aggregate in JS on every render
 * `/matters/stats` does the counting in SQL (single aggregate query) and
 * returns a handful of numbers plus 5 rows — orders of magnitude less data
 * and DB work for the same UI.
 */
export const useDashboardStats = () => {
  return useQuery({
    queryKey: MATTER_QUERY_KEYS.stats,
    queryFn: async () => {
      const response = await api.get('/matters/stats');
      return response.data;
    },
    staleTime: 60_000,
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
      // Scope to paginated LIST queries only (['matters', page, size]).
      // MATTER_QUERY_KEYS.all (['matters']) as a predicate would also match
      // ['matters', 'stats'], whose shape ({activeCount, recentMatters, ...})
      // is not a paginated page — running it through normalizePage() would
      // silently replace it with an empty/garbled page object until the
      // onSettled invalidation refetches it.
      const isListQuery = (query) =>
        query.queryKey[0] === 'matters' && typeof query.queryKey[1] === 'number';

      await queryClient.cancelQueries({ predicate: isListQuery });

      const previousMatters = queryClient.getQueriesData({ predicate: isListQuery });
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

      queryClient.setQueriesData({ predicate: isListQuery }, (oldData) => {
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
