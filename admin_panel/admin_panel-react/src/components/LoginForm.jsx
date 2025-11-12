import { useState } from 'react'
import { useAuth } from '../context/AuthContext'
import './LoginForm.css'

const LoginForm = ({ onSwitchToRegister }) => {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login, isAuthenticated } = useAuth()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    if (!username.trim() || !password.trim()) {
      setError('Lütfen kullanıcı adı ve şifre giriniz.')
      setLoading(false)
      return
    }

    try {
      await login(username, password)
      // Başarılı giriş sonrası yönlendirme burada yapılabilir
      console.log('Giriş başarılı!')
    } catch (err) {
      setError(err.message || 'Giriş başarısız. Lütfen tekrar deneyin.')
    } finally {
      setLoading(false)
    }
  }

  if (isAuthenticated) {
    return (
      <div className="login-container">
        <div className="login-card success">
          <h2>Giriş Başarılı!</h2>
          <p>Hoş geldiniz, {username}!</p>
        </div>
      </div>
    )
  }

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <h1>Admin Panel</h1>
          <p>Lütfen giriş yapın</p>
        </div>

        <form onSubmit={handleSubmit} className="login-form">
          {error && <div className="error-message">{error}</div>}

          <div className="form-group">
            <label htmlFor="username">Kullanıcı Adı</label>
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Kullanıcı adınızı girin"
              disabled={loading}
              autoComplete="username"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Şifre</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Şifrenizi girin"
              disabled={loading}
              autoComplete="current-password"
            />
          </div>

          <button type="submit" className="login-button" disabled={loading}>
            {loading ? 'Giriş yapılıyor...' : 'Giriş Yap'}
          </button>

          <div className="switch-form">
            <p>
              Hesabınız yok mu?{' '}
              <button type="button" onClick={onSwitchToRegister} className="link-button">
                Kaydol
              </button>
            </p>
          </div>
        </form>
      </div>
    </div>
  )
}

export default LoginForm

