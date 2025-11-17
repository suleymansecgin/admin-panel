import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authService } from '../services/authService'
import './Login.css'

const Login = () => {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [mfaCode, setMfaCode] = useState('')
  const [mfaRequired, setMfaRequired] = useState(false)
  const [mfaSecret, setMfaSecret] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const result = await login(username, password, mfaCode || null)

      if (result.mfaRequired) {
        setMfaRequired(true)
        setLoading(false)
        return
      }

      if (result.success) {
        navigate('/dashboard')
      } else {
        setError(result.error || 'Giriş başarısız')
      }
    } catch (err) {
      setError('Bir hata oluştu. Lütfen tekrar deneyin.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <h1>Admin Panel</h1>
          <p>Hesabınıza giriş yapın</p>
        </div>

        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label htmlFor="username">Kullanıcı Adı</label>
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              disabled={mfaRequired}
              placeholder="Kullanıcı adınızı girin"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Şifre</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              disabled={mfaRequired}
              placeholder="Şifrenizi girin"
            />
          </div>

          {mfaRequired && (
            <div className="form-group">
              <label htmlFor="mfaCode">MFA Kodu</label>
              <input
                type="text"
                id="mfaCode"
                value={mfaCode}
                onChange={(e) => setMfaCode(e.target.value)}
                required
                placeholder="6 haneli MFA kodunu girin"
                maxLength={6}
              />
              <small>Authenticator uygulamanızdan 6 haneli kodu girin</small>
            </div>
          )}

          <button type="submit" className="login-button" disabled={loading}>
            {loading ? 'Giriş yapılıyor...' : mfaRequired ? 'Doğrula' : 'Giriş Yap'}
          </button>
        </form>

        <div className="login-footer">
          <p>
            Hesabınız yok mu? <Link to="/register">Kayıt olun</Link>
          </p>
        </div>
      </div>
    </div>
  )
}

export default Login

