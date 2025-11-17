import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authService } from '../services/authService'
import { getErrorMessage } from '../utils/errorHandler'
import './MfaSetup.css'

const MfaSetup = () => {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [step, setStep] = useState(user?.mfaEnabled ? 'disable' : 'setup')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  
  // Setup için state'ler
  const [secret, setSecret] = useState('')
  const [qrCodeUrl, setQrCodeUrl] = useState('')
  const [verificationCode, setVerificationCode] = useState('')
  
  // Disable için state
  const [disableCode, setDisableCode] = useState('')

  useEffect(() => {
    if (user?.mfaEnabled) {
      setStep('disable')
    } else {
      setStep('setup')
    }
  }, [user])

  const handleSetupMfa = async () => {
    setLoading(true)
    setError('')
    setSuccess('')
    
    try {
      const response = await authService.setupMfa()
      setSecret(response.secret)
      setQrCodeUrl(response.qrCodeUrl)
      setStep('verify')
    } catch (err) {
      setError(getErrorMessage(err, 'MFA ayarlama başarısız'))
    } finally {
      setLoading(false)
    }
  }

  const handleEnableMfa = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    setSuccess('')
    
    try {
      await authService.enableMfa(secret, verificationCode)
      setSuccess('MFA başarıyla etkinleştirildi!')
      setTimeout(() => {
        window.location.reload()
      }, 2000)
    } catch (err) {
      setError(getErrorMessage(err, 'MFA etkinleştirme başarısız'))
    } finally {
      setLoading(false)
    }
  }

  const handleDisableMfa = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    setSuccess('')
    
    try {
      await authService.disableMfa(disableCode)
      setSuccess('MFA başarıyla devre dışı bırakıldı!')
      setTimeout(() => {
        window.location.reload()
      }, 2000)
    } catch (err) {
      setError(getErrorMessage(err, 'MFA devre dışı bırakma başarısız'))
    } finally {
      setLoading(false)
    }
  }

  const handleBack = () => {
    navigate('/dashboard')
  }

  return (
    <div className="mfa-setup-container">
      <div className="mfa-setup-card">
        <div className="mfa-header">
          <h1>İki Faktörlü Kimlik Doğrulama (MFA)</h1>
          <button onClick={handleBack} className="back-button">
            ← Geri Dön
          </button>
        </div>

        {error && typeof error === 'string' && <div className="error-message">{error}</div>}
        {success && <div className="success-message">{success}</div>}

        {step === 'setup' && (
          <div className="mfa-content">
            <p className="mfa-description">
              İki faktörlü kimlik doğrulama, hesabınızın güvenliğini artırır. 
              Bir kimlik doğrulama uygulaması (Google Authenticator, Microsoft Authenticator vb.) kullanarak 
              giriş yaparken ek bir kod girmeniz gerekecektir.
            </p>
            <button 
              onClick={handleSetupMfa} 
              className="setup-button"
              disabled={loading}
            >
              {loading ? 'Yükleniyor...' : 'MFA Ayarlamaya Başla'}
            </button>
          </div>
        )}

        {step === 'verify' && (
          <div className="mfa-content">
            <h2>QR Kodu Tarayın</h2>
            <p className="mfa-description">
              Kimlik doğrulama uygulamanızı açın ve aşağıdaki QR kodu tarayın.
            </p>
            
            {qrCodeUrl && (
              <div className="qr-code-container">
                <img src={qrCodeUrl} alt="QR Code" className="qr-code-image" />
              </div>
            )}

            <div className="manual-entry">
              <p>QR kodu tarayamıyorsanız, bu kodu manuel olarak girin:</p>
              <code className="secret-code">{secret}</code>
            </div>

            <form onSubmit={handleEnableMfa} className="mfa-form">
              <div className="form-group">
                <label htmlFor="verificationCode">Doğrulama Kodu</label>
                <input
                  type="text"
                  id="verificationCode"
                  value={verificationCode}
                  onChange={(e) => setVerificationCode(e.target.value)}
                  placeholder="6 haneli kod"
                  maxLength="6"
                  required
                />
                <small>Kimlik doğrulama uygulamanızdan aldığınız 6 haneli kodu girin</small>
              </div>
              
              <div className="form-actions">
                <button 
                  type="button" 
                  onClick={() => setStep('setup')}
                  className="cancel-button"
                >
                  İptal
                </button>
                <button 
                  type="submit" 
                  className="enable-button"
                  disabled={loading}
                >
                  {loading ? 'Etkinleştiriliyor...' : 'MFA\'yı Etkinleştir'}
                </button>
              </div>
            </form>
          </div>
        )}

        {step === 'disable' && (
          <div className="mfa-content">
            <h2>MFA'yı Devre Dışı Bırak</h2>
            <p className="mfa-description">
              MFA'yı devre dışı bırakmak için kimlik doğrulama uygulamanızdan aldığınız 
              doğrulama kodunu girin.
            </p>

            <form onSubmit={handleDisableMfa} className="mfa-form">
              <div className="form-group">
                <label htmlFor="disableCode">Doğrulama Kodu</label>
                <input
                  type="text"
                  id="disableCode"
                  value={disableCode}
                  onChange={(e) => setDisableCode(e.target.value)}
                  placeholder="6 haneli kod"
                  maxLength="6"
                  required
                />
                <small>Kimlik doğrulama uygulamanızdan aldığınız 6 haneli kodu girin</small>
              </div>
              
              <div className="form-actions">
                <button 
                  type="button" 
                  onClick={handleBack}
                  className="cancel-button"
                >
                  İptal
                </button>
                <button 
                  type="submit" 
                  className="disable-button"
                  disabled={loading}
                >
                  {loading ? 'Devre Dışı Bırakılıyor...' : 'MFA\'yı Devre Dışı Bırak'}
                </button>
              </div>
            </form>
          </div>
        )}
      </div>
    </div>
  )
}

export default MfaSetup

