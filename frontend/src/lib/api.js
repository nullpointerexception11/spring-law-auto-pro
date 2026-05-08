import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Backend tenant izolasyonu (Org ID) beklediği için geçici olarak sabit bir ID ekliyoruz.
// Gerçek uygulamada bu değer giriş yapmış kullanıcının organizasyonundan gelecektir.
api.interceptors.request.use((config) => {
  config.headers['X-Org-Id'] = '1';
  return config;
});

export default api;
