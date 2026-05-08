import axios from "axios";

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api",
});

// Response interceptor to unwrap ApiResponse
api.interceptors.response.use(
  (response) => {
    // If the response has our standard wrapper { status, data, meta }, return only data
    if (response.data && response.data.status === "ok" && Object.prototype.hasOwnProperty.call(response.data, "data")) {
      return {
        ...response,
        data: response.data.data,
        meta: response.data.meta
      };
    }
    return response;
  },
  (error) => {
    // Standardize error message extraction from ApiResponse wrapper
    if (error.response && error.response.data && error.response.data.status === "error") {
      error.message = error.response.data.meta || error.message;
    }
    return Promise.reject(error);
  }
);
