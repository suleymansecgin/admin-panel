import axios from 'axios'

const API_BASE_URL = '/api/auth'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Token'ı otomatik olarak header'a ekleyen interceptor
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

export const authService = {
  async login(username, password) {
    try {
      const response = await api.post('/login', {
        username,
        password,
      })
      return response.data
    } catch (error) {
      if (error.response) {
        throw new Error(
          error.response.data?.message || 'Giriş başarısız. Lütfen bilgilerinizi kontrol edin.'
        )
      }
      throw new Error('Sunucuya bağlanılamadı. Lütfen daha sonra tekrar deneyin.')
    }
  },

  async register(username, email, password, role = 'USER') {
    try {
      const response = await api.post('/register', {
        username,
        email,
        password,
        role,
      })
      return response.data
    } catch (error) {
      if (error.response) {
        throw new Error(
          error.response.data?.message || 'Kayıt başarısız. Lütfen bilgilerinizi kontrol edin.'
        )
      }
      throw new Error('Sunucuya bağlanılamadı. Lütfen daha sonra tekrar deneyin.')
    }
  },
}

export default authService

