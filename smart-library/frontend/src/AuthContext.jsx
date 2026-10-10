import { createContext, useContext, useEffect, useState } from "react";
import api from "./api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // On every load, re-validate against the server rather than trusting whatever
  // is cached in localStorage - this is what catches "token still looks valid
  // but the account was locked/role changed since" and logs the user out cleanly.
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      setLoading(false);
      return;
    }
    api.get("/students/me")
      .then((res) => setUser(res.data))
      .catch(() => {
        localStorage.removeItem("token");
        localStorage.removeItem("role");
      })
      .finally(() => setLoading(false));
  }, []);

  function login(loginResponse) {
    localStorage.setItem("token", loginResponse.token);
    localStorage.setItem("role", loginResponse.role);
    setUser({ id: loginResponse.id, name: loginResponse.name, email: loginResponse.email, role: loginResponse.role });
  }

  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    setUser(null);
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
