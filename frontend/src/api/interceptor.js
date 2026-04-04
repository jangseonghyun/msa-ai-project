import api from "./api";
import { getAccessToken, setAccessToken } from "./token";
import { getLogoutHandler } from "../context/logoutHandler";

api.interceptors.request.use((config) => {
  const token = getAccessToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (res) => res,
  async (err) => {
    const originalRequest = err.config;

    // refresh 요청이면 재시도 금지
    if (originalRequest.url.includes("/auth/refresh")) {
      return Promise.reject(err);
    }

    if (err.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const res = await api.post("/auth/refresh");

        setAccessToken(res.data.accessToken);

        originalRequest.headers.Authorization = res.data.accessToken;

        return api(originalRequest);
      } catch (e) {
               
        const logout = getLogoutHandler();

        if(logout) logout();

        return Promise.reject(e);
      }
    }

    return Promise.reject(err);
  }
);

export default api;