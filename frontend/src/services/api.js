import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
});

api.interceptors.request.use(
  (config) => {
    // Don't add auth header for public drug search endpoint
    if (config.url?.includes('/api/drugs/')) {
      return config;
    }
    
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 429) {
      const retryAfter = error.response.headers['retry-after'] || 60;
      error.message = `Too many requests. Please wait ${retryAfter} seconds and try again.`;
    }
    return Promise.reject(error);
  },
);

export default api;
