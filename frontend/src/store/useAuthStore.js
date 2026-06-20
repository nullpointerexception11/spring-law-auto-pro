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
        const token = data.token || null;
        const user = data.user || (data.fullName || data.email ? {
          fullName: data.fullName || null,
          email: data.email || null,
        } : null);
        set({
          token,
          role: data.role || null,
          orgId: data.orgId || null,
          isAuthenticated: Boolean(token),
          user,
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

      getToken: () => get().token,

      getSession: () => {
        const { user, token, role, orgId, isAuthenticated } = get();
        return { user, token, role, orgId, isAuthenticated };
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
