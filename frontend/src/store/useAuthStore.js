import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

export const useAuthStore = create(
  persist(
    (set, get) => ({
      user: null,
      token: localStorage.getItem('token') || null,
      role: localStorage.getItem('role') || null,
      orgId: localStorage.getItem('orgId') || null,
      isAuthenticated: !!localStorage.getItem('token'),

      setAuth: (data) => {
        localStorage.setItem('token', data.token);
        localStorage.setItem('role', data.role);
        localStorage.setItem('orgId', data.orgId);
        
        set({
          token: data.token,
          role: data.role,
          orgId: data.orgId,
          isAuthenticated: true,
          user: data.user || null,
        });
      },

      logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
        localStorage.removeItem('orgId');
        
        set({
          user: null,
          token: null,
          role: null,
          orgId: null,
          isAuthenticated: false,
        });
      },

      // Helper to check permissions
      hasPermission: (permission) => {
        const { role } = get();
        if (role === 'SUPER_ADMIN') return true;
        // Logic for role-based permissions can be expanded here
        return false;
      },
    }),
    {
      name: 'law-auto-auth',
      storage: createJSONStorage(() => localStorage),
      // We only want to persist certain fields if needed, 
      // but here we sync with localStorage for simplicity and SSR safety
    }
  )
);
