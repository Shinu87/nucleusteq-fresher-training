// Context for managing user authentication across the application.

import { createContext, useContext, useState } from "react";
import {
  getToken,
  getStoredUser,
  saveAuth,
  clearAuth,
} from "../utils/authStorage";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(getToken());
  const [user, setUser] = useState(getStoredUser());

  function login(newToken, newUser) {
    saveAuth(newToken, newUser);
    setToken(newToken);
    setUser(newUser);
  }

  function logout() {
    clearAuth();
    setToken(null);
    setUser(null);
  }

  const value = {
    token,
    user,
    isAuthenticated: !!token,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
