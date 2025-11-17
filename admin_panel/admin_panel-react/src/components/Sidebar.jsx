import { useState, useEffect } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Sidebar.css'

const Sidebar = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const { user, logout } = useAuth()
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false)

  const handleLogout = async () => {
    await logout()
    navigate('/login')
  }

  // Mobil menüyü kapat
  useEffect(() => {
    setIsMobileMenuOpen(false)
  }, [location.pathname])

  // Mobil ekran kontrolü
  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth > 768) {
        setIsMobileMenuOpen(false)
      }
    }
    window.addEventListener('resize', handleResize)
    return () => window.removeEventListener('resize', handleResize)
  }, [])

  // Menü öğeleri ve gerekli yetkiler
  const allMenuItems = [
    { path: '/dashboard', label: 'Dashboard', icon: '📊', permission: null }, // Dashboard herkese açık
    { path: '/roles', label: 'Rol Yönetimi', icon: '📋', permission: 'ROLE_MANAGE' },
    { path: '/users', label: 'Kullanıcı Yönetimi', icon: '👥', permission: 'USER_CREATE' },
    { path: '/pazaryerleri', label: 'Pazaryeri Yönetimi', icon: '🏪', permission: 'MARKETPLACE_MANAGE' },
    { path: '/urunler', label: 'Ürün Yönetimi', icon: '📦', permission: 'PRODUCT_MANAGE' },
    { path: '/siparisler', label: 'Sipariş Yönetimi', icon: '🛒', permission: 'ORDER_MANAGE' },
    { path: '/senkronizasyon-loglari', label: 'Senkronizasyon Logları', icon: '🔄', permission: 'SYNC_LOGS_VIEW' },
    { path: '/raporlar', label: 'Raporlar', icon: '📈', permission: null, requiredRole: 'ADMIN' } // Sadece ADMIN rolü
  ]

  // Permissions'ı array'e çevir
  const userPermissions = user?.permissions ? (Array.isArray(user.permissions) ? user.permissions : Array.from(user.permissions)) : []
  
  // Roles'ı array'e çevir
  const userRoles = user?.roles ? (Array.isArray(user.roles) ? user.roles : Array.from(user.roles)) : []
  
  // Kullanıcının yetkilerine göre menü öğelerini filtrele
  const menuItems = allMenuItems.filter(item => {
    // Kullanıcı onaylanmamışsa hiçbir şey gösterilmez
    if (!user?.enabled || userPermissions.length === 0) {
      return false
    }
    
    // Role kontrolü (ADMIN gibi)
    if (item.requiredRole) {
      return userRoles.includes(item.requiredRole)
    }
    
    // Dashboard onaylanmış kullanıcılara açık
    if (!item.permission) return true
    
    // Kullanıcının bu yetkiye sahip olup olmadığını kontrol et
    return userPermissions.includes(item.permission)
  })

  return (
    <>
      {/* Hamburger Menu Button - Mobil için */}
      <button 
        className="mobile-menu-toggle"
        onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
        aria-label="Menüyü aç/kapat"
      >
        <span className={`hamburger ${isMobileMenuOpen ? 'active' : ''}`}>
          <span></span>
          <span></span>
          <span></span>
        </span>
      </button>

      {/* Overlay - Mobil için */}
      {isMobileMenuOpen && (
        <div 
          className="sidebar-overlay"
          onClick={() => setIsMobileMenuOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside className={`sidebar ${isMobileMenuOpen ? 'mobile-open' : ''}`}>
        <div className="sidebar-header">
          <div className="sidebar-logo">
            <div className="logo-icon">⚡</div>
            <h2>Admin Panel</h2>
          </div>
          {user && (
            <div className="user-info">
              <div className="user-avatar">
                {user.firstName?.[0]}{user.lastName?.[0]}
              </div>
              <div className="user-details">
                <p className="user-name">{user.firstName} {user.lastName}</p>
                <p className="user-email">{user.email}</p>
              </div>
            </div>
          )}
        </div>
        
        <nav className="sidebar-nav">
          {menuItems.map((item) => (
            <button
              key={item.path}
              onClick={() => navigate(item.path)}
              className={`nav-item ${location.pathname === item.path ? 'active' : ''}`}
            >
              <span className="nav-icon">{item.icon}</span>
              <span className="nav-label">{item.label}</span>
            </button>
          ))}
        </nav>

        <div className="sidebar-footer">
          <button onClick={handleLogout} className="logout-button">
            <span className="logout-icon">🚪</span>
            <span>Çıkış Yap</span>
          </button>
        </div>
      </aside>
    </>
  )
}

export default Sidebar

