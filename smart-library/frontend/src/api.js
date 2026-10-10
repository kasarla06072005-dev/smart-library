import axios from "axios";

// Configurable at build time for deployment: VITE_API_BASE_URL
const baseURL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const api = axios.create({ baseURL });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Centralised handling for the two "your session is no good anymore" cases -
// an expired/invalid token, or an account that got locked after the token was
// issued. Either way, bounce back to login rather than showing a broken page.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    if (status === 401 || status === 423) {
      localStorage.removeItem("token");
      localStorage.removeItem("role");
      if (window.location.pathname !== "/") {
        window.location.href = "/";
      }
    }
    return Promise.reject(error);
  }
);

export default api;
