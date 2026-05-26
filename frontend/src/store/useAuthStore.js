import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

export const useAuthStore = create(
  persist(
    (set, get) => ({
      user: null,
      token: null,
      role: null,
      orgId: null,
      isAuthenticated: false,

      setAuth: (data) => {
        set({
          token: data.token,
          role: data.role,
          orgId: data.orgId,
          isAuthenticated: true,
          user: data.user || null,
        });
      },

      logout: () => {
        set({
          user: null,
          token: null,
          role: null,
          orgId: null,
          isAuthenticated: false,
        });
      },

      hasPermission: (_permission) => {
        const { role } = get();
        if (role === 'PLATFORM_ADMIN') return true;
        return false;
      },
    }),
    {
      name: 'law-auto-auth',
      storage: createJSONStorage(() => localStorage),
    }
  )
);
