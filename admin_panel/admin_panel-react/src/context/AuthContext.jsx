import { createContext, useContext, useState, useEffect } from 'react'
import authService from '../services/authService'

const AuthContext = createContext(null)

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const [token, setToken] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    // Sayfa yüklendiğinde localStorage'dan token'ı kontrol et
    const storedToken = localStorage.getItem('token')
    const storedUser = localStorage.getItem('user')

    if (storedToken && storedUser) {
      setToken(storedToken)
      setUser(JSON.parse(storedUser))
    }
    setLoading(false)
  }, [])

  const login = async (username, password) => {
    try {
      const response = await authService.login(username, password)
      const { token: newToken, username: userUsername, email, role } = response

      setToken(newToken)
      const userData = { username: userUsername, email, role }
      setUser(userData)

      // Token ve kullanıcı bilgilerini localStorage'a kaydet
      localStorage.setItem('token', newToken)
      localStorage.setItem('user', JSON.stringify(userData))

      return response
    } catch (error) {
      throw error
    }
  }

  const logout = () => {
    setToken(null)
    setUser(null)
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  const value = {
    user,
    token,
    login,
    logout,
    isAuthenticated: !!token,
    loading,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

