import axios from "axios";
import { useAuthStore } from "@/store/useAuthStore";
import { ROUTES } from "@/lib/constants";
import { toast } from "sonner";

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor to attach JWT token from store
api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().getToken();
  if (token) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

// Response interceptor to handle data and global errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const originalRequest = error.config;

    // Handle 401 Unauthorized
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      // If we had a refresh token flow, it would go here.
      // For now, we logout on 401 to ensure consistency.
      useAuthStore.getState().logout();
      window.location.assign(ROUTES.LOGIN);
      
      toast.error("Oturum süresi doldu. Lütfen tekrar giriş yapın.");
      return Promise.reject(error);
    }

    // Handle 403 Forbidden
    if (error.response?.status === 403) {
      toast.error("Bu işlem için yetkiniz bulunmuyor.");
    }

    // Handle 500 Internal Server Error
    if (error.response?.status >= 500) {
      toast.error("Sunucu hatası oluştu. Lütfen daha sonra tekrar deneyin.");
    }

    // Standardize error message extraction
    const message = error.response?.data?.message || error.message || "Bir hata oluştu";
    
    return Promise.reject({
      ...error,
      message,
      originalError: error
    });
  }
);
