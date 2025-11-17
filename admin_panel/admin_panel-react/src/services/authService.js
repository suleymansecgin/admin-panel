import api from './api'

export const authService = {
  login: async (username, password, mfaCode = null) => {
    const response = await api.post('/auth/login', {
      username,
      password,
      mfaCode,
    })
    return response.data
  },

  register: async (userData) => {
    const response = await api.post('/auth/register', userData)
    return response.data
  },

  logout: async (refreshToken) => {
    await api.post('/auth/logout', { refreshToken })
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')
  },

  setupMfa: async () => {
    const response = await api.post('/auth/mfa/setup')
    return response.data
  },

  enableMfa: async (secret, code) => {
    await api.post(`/auth/mfa/enable?secret=${secret}`, { code })
  },

  disableMfa: async (code) => {
    await api.post('/auth/mfa/disable', { code })
  },
}

