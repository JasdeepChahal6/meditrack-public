import { createContext, useContext, useEffect, useState } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [refreshToken, setRefreshToken] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Load tokens from storage and verify session
  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    const storedRefresh = localStorage.getItem('refreshToken');

    if (!storedToken) {
      setLoading(false);
      return;
    }

    setToken(storedToken);
    setRefreshToken(storedRefresh);
    verifyProfile(storedToken).finally(() => setLoading(false));
  }, []);

  const verifyProfile = async () => {
    try {
      const res = await api.get('/user/profile');
      setUser(res.data);
      setError(null);
    } catch (err) {
      handleLogout();
      throw err;
    }
  };

  const handleLoginState = (accessToken, newRefreshToken, userData) => {
    setToken(accessToken);
    setRefreshToken(newRefreshToken);
    setUser(userData);
    localStorage.setItem('token', accessToken);
    localStorage.setItem('refreshToken', newRefreshToken);
  };

  const login = async (email, password) => {
    setError(null);
    const res = await api.post('/auth/login', { email, password });
    const { token: accessToken, refreshToken: newRefreshToken, user: userData } = res.data;
    handleLoginState(accessToken, newRefreshToken, userData);
    
    // Refresh profile to get latest emailVerified status
    try {
      await verifyProfile();
    } catch (err) {
      // Profile refresh failed, but login succeeded - continue
      console.error('Failed to refresh profile after login:', err);
    }
    
    return userData;
  };

  const register = async (name, email, password) => {
    setError(null);
    const res = await api.post('/auth/register', { name, email, password });
    return res.data; // backend returns UserResponse
  };

  const logout = async () => {
    try {
      const storedRefresh = localStorage.getItem('refreshToken');
      if (storedRefresh) {
        await api.post('/auth/logout', { refreshToken: storedRefresh });
      }
    } catch (err) {
      // swallow logout errors to ensure client state clears
      console.error('Logout failed', err);
    } finally {
      handleLogout();
    }
  };

  const handleLogout = () => {
    setUser(null);
    setToken(null);
    setRefreshToken(null);
    setError(null);
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
  };

  const value = {
    user,
    token,
    refreshToken,
    loading,
    error,
    login,
    register,
    logout,
    setUser,
    setError,
    verifyProfile,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => useContext(AuthContext);
