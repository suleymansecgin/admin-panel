import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Dashboard.css'

const Dashboard = () => {
  const { user } = useAuth()
  const navigate = useNavigate()
  
  // Roles'ı array'e çevir
  const userRoles = user?.roles ? (Array.isArray(user.roles) ? user.roles : Array.from(user.roles)) : []
  const isAdmin = userRoles.includes('ADMIN')

  const handleMfaSetup = () => {
    navigate('/mfa-setup')
  }

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <div className="dashboard-title">
          <h1>Dashboard</h1>
          <p>Hoş geldiniz, {user?.firstName} {user?.lastName}</p>
        </div>
      </div>


      <div className="dashboard-content">
        <div className="info-card">
          <h2>Kullanıcı Bilgileri</h2>
          <div className="info-item">
            <span className="info-label">Kullanıcı Adı:</span>
            <span className="info-value">{user?.username}</span>
          </div>
          <div className="info-item">
            <span className="info-label">E-posta:</span>
            <span className="info-value">{user?.email}</span>
          </div>
          <div className="info-item">
            <span className="info-label">Roller:</span>
            <span className="info-value">
              {user?.roles?.length > 0 ? user.roles.join(', ') : 'Rol atanmamış'}
            </span>
          </div>
          <div className="info-item">
            <span className="info-label">MFA Durumu:</span>
            <span className="info-value">
              {user?.mfaEnabled ? '✅ Etkin' : '❌ Devre Dışı'}
            </span>
          </div>
          <div className="info-item">
            <button onClick={handleMfaSetup} className="mfa-setup-button">
              MFA Ayarları
            </button>
          </div>
        </div>

        {user?.permissions && user.permissions.length > 0 && (
          <div className="info-card">
            <h2>Yetkiler</h2>
            <div className="permissions-list">
              {user.permissions.map((permission, index) => (
                <span key={index} className="permission-badge">
                  {permission}
                </span>
              ))}
            </div>
          </div>
        )}

        {isAdmin && (
          <div className="info-card rapor-card">
            <h2>Raporlar</h2>
            <p className="rapor-description">
              Kar-zarar analizleri ve detaylı raporlar için rapor sayfasına gidin.
            </p>
            <button 
              onClick={() => navigate('/raporlar')} 
              className="rapor-button"
            >
              📈 Raporları Görüntüle
            </button>
          </div>
        )}
      </div>
    </div>
  )
}

export default Dashboard

