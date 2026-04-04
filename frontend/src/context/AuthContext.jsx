import { createContext, useContext, useState, useEffect } from "react";
import api from "../api/interceptor";
import { setAccessToken } from "../api/token";
import { setLogoutHandler } from "./logoutHandler"

const AuthContext = createContext();

export function AuthProvider({ children }) {

  const [user, setUser] = useState(null);

  useEffect(() => {

  const initAuth = async () => {

    try {
        // 🔥 refresh 시도
        const res = await api.post("/auth/refresh");

        setAccessToken(res.data.accessToken);

        // 🔥 유저 정보 조회
        const me = await api.get("/auth/me");
        setUser(me.data);

      } catch (e) {
        setUser(null);
      }

    };

    initAuth();

  }, []);

  const logout = async () => {
    try {
      await api.post("/auth/logout"); // 선택
    } catch (e) {}

    setUser(null);
    setAccessToken(null);
  };

  useEffect(()=> {
    setLogoutHandler(logout);
  }, []);

  return (
    <AuthContext.Provider value={{ user, setUser, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
