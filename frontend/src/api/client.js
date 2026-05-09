import axios from "axios";

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api",
});

// Request interceptor to attach JWT token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor to handle raw data and errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Standardize error handling for Spring Boot error responses
    if (error.response && error.response.data) {
      // Spring typically returns { message, status, ... } for exceptions
      const message = error.response.data.message || error.message;
      return Promise.reject({ ...error, message });
    }
    return Promise.reject(error);
  }
);
