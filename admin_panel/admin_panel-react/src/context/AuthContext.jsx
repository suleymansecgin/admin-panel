import { createContext, useState, useContext, useEffect } from 'react'
import { authService } from '../services/authService'

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
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      try {
        setUser(JSON.parse(storedUser))
      } catch (e) {
        localStorage.removeItem('user')
      }
    }
    setLoading(false)
  }, [])

  const login = async (username, password, mfaCode = null) => {
    try {
      const response = await authService.login(username, password, mfaCode)
      
      if (response.mfaRequired) {
        return { mfaRequired: true }
      }

      if (response.accessToken) {
        localStorage.setItem('accessToken', response.accessToken)
        localStorage.setItem('refreshToken', response.refreshToken)
        localStorage.setItem('user', JSON.stringify(response.user))
        setUser(response.user)
        return { success: true }
      }
    } catch (error) {
      console.error('Login error:', error.response?.data)
      
      // Validation hataları için özel işleme
      if (error.response?.data?.errors) {
        const errors = error.response.data.errors
        const errorMessages = Object.values(errors).join(', ')
        return {
          success: false,
          error: errorMessages || 'Giriş başarısız',
        }
      }
      
      // Diğer hatalar için
      const errorMessage = 
        error.response?.data?.message?.ofStatic || 
        error.response?.data?.message?.messageType?.message ||
        error.response?.data?.message ||
        error.message ||
        'Giriş başarısız'
      
      return {
        success: false,
        error: errorMessage,
      }
    }
  }

  const register = async (userData) => {
    try {
      const response = await authService.register(userData)
      // İlk kullanıcı ise token var, diğerleri için token yok (onay bekliyor)
      if (response.accessToken) {
        localStorage.setItem('accessToken', response.accessToken)
        localStorage.setItem('refreshToken', response.refreshToken)
        localStorage.setItem('user', JSON.stringify(response.user))
        setUser(response.user)
        return { success: true, user: response.user }
      } else if (response.user) {
        // Onay bekleyen kullanıcı için sadece user bilgisini döndür
        return { success: true, user: response.user }
      }
      return { success: false, error: 'Kayıt başarısız' }
    } catch (error) {
      console.error('Register error:', error.response?.data)
      
      // Validation hataları için özel işleme
      if (error.response?.data?.errors) {
        const errors = error.response.data.errors
        const errorMessages = Object.values(errors).join(', ')
        return {
          success: false,
          error: errorMessages || 'Kayıt başarısız',
        }
      }
      
      // Diğer hatalar için
      const errorMessage = 
        error.response?.data?.message?.ofStatic || 
        error.response?.data?.message?.messageType?.message ||
        error.response?.data?.message ||
        error.message ||
        'Kayıt başarısız'
      
      return {
        success: false,
        error: errorMessage,
      }
    }
  }

  const logout = async () => {
    const refreshToken = localStorage.getItem('refreshToken')
    if (refreshToken) {
      try {
        await authService.logout(refreshToken)
      } catch (error) {
        console.error('Logout error:', error)
      }
    }
    setUser(null)
  }

  const value = {
    user,
    login,
    register,
    logout,
    loading,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

