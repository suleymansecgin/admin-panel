import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Register.css'

const Register = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
  })
  const [errors, setErrors] = useState({})
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(false)
  const { register } = useAuth()
  const navigate = useNavigate()

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSuccess('')
    setErrors({})
    setLoading(true)

    // Frontend validation
    const newErrors = {}
    if (formData.username.length < 3) {
      newErrors.username = 'Kullanıcı adı en az 3 karakter olmalıdır'
    }
    if (formData.password.length < 6) {
      newErrors.password = 'Şifre en az 6 karakter olmalıdır'
    }
    if (!formData.email.includes('@')) {
      newErrors.email = 'Geçerli bir e-posta adresi giriniz'
    }
    if (!formData.firstName.trim()) {
      newErrors.firstName = 'Ad boş olamaz'
    }
    if (!formData.lastName.trim()) {
      newErrors.lastName = 'Soyad boş olamaz'
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors)
      setLoading(false)
      return
    }

    try {
      const result = await register(formData)

      if (result.success) {
        // Eğer token varsa (ilk kullanıcı), dashboard'a yönlendir
        // Token yoksa (onay bekliyor), bilgi mesajı göster
        if (result.user?.enabled) {
          navigate('/dashboard')
        } else {
          setError('')
          setSuccess('Kayıt başarılı! Hesabınız yönetici tarafından onaylandıktan sonra giriş yapabileceksiniz.')
          // 3 saniye sonra login sayfasına yönlendir
          setTimeout(() => {
            navigate('/login')
          }, 3000)
        }
      } else {
        // Backend'den gelen hataları parse et
        if (result.error) {
          setError(result.error)
        }
      }
    } catch (err) {
      setError('Bir hata oluştu. Lütfen tekrar deneyin.')
      console.error('Register error:', err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="register-container">
      <div className="register-card">
        <div className="register-header">
          <h1>Kayıt Ol</h1>
          <p>Yeni hesap oluşturun</p>
        </div>

        {error && <div className="error-message">{error}</div>}
        {success && <div className="success-message">{success}</div>}

        <form onSubmit={handleSubmit} className="register-form">
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="firstName">Ad *</label>
              <input
                type="text"
                id="firstName"
                name="firstName"
                value={formData.firstName}
                onChange={handleChange}
                required
                placeholder="Adınız"
                className={errors.firstName ? 'error' : ''}
              />
              {errors.firstName && <span className="field-error">{errors.firstName}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="lastName">Soyad *</label>
              <input
                type="text"
                id="lastName"
                name="lastName"
                value={formData.lastName}
                onChange={handleChange}
                required
                placeholder="Soyadınız"
                className={errors.lastName ? 'error' : ''}
              />
              {errors.lastName && <span className="field-error">{errors.lastName}</span>}
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="username">Kullanıcı Adı *</label>
            <input
              type="text"
              id="username"
              name="username"
              value={formData.username}
              onChange={handleChange}
              required
              minLength={3}
              maxLength={50}
              placeholder="3-50 karakter arası"
              className={errors.username ? 'error' : ''}
            />
            {errors.username && <span className="field-error">{errors.username}</span>}
            <small>Kullanıcı adı 3-50 karakter arasında olmalıdır</small>
          </div>

          <div className="form-group">
            <label htmlFor="email">E-posta *</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              placeholder="ornek@email.com"
              className={errors.email ? 'error' : ''}
            />
            {errors.email && <span className="field-error">{errors.email}</span>}
          </div>

          <div className="form-group">
            <label htmlFor="password">Şifre *</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              minLength={6}
              placeholder="En az 6 karakter"
              className={errors.password ? 'error' : ''}
            />
            {errors.password && <span className="field-error">{errors.password}</span>}
            <small>Şifre en az 6 karakter olmalıdır</small>
          </div>

          <button type="submit" className="register-button" disabled={loading}>
            {loading ? 'Kayıt yapılıyor...' : 'Kayıt Ol'}
          </button>
        </form>

        <div className="register-footer">
          <p>
            Zaten hesabınız var mı? <Link to="/login">Giriş yapın</Link>
          </p>
        </div>
      </div>
    </div>
  )
}

export default Register

