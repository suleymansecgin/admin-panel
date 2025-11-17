import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import Layout from './Layout'

// Sayfa yolları ve gerekli yetkiler
const routePermissions = {
  '/dashboard': null, // Dashboard onaylanmış kullanıcılara açık (yetki gerektirmez)
  '/roles': 'ROLE_MANAGE',
  '/users': 'USER_CREATE',
  '/pazaryerleri': 'MARKETPLACE_MANAGE',
  '/urunler': 'PRODUCT_MANAGE',
  '/siparisler': 'ORDER_MANAGE',
  '/senkronizasyon-loglari': 'SYNC_LOGS_VIEW',
  '/roles/new': 'ROLE_MANAGE',
  '/roles/:id/edit': 'ROLE_MANAGE',
  '/users/:id/roles': 'USER_CREATE',
  '/mfa-setup': null // MFA setup onaylanmış kullanıcılara açık
}

const ProtectedRoute = ({ children, requiredPermission, requiredRole }) => {
  const { user, loading } = useAuth()
  const location = useLocation()

  if (loading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh',
        color: 'white'
      }}>
        Yükleniyor...
      </div>
    )
  }

  if (!user) {
    return <Navigate to="/login" replace />
  }

  // Permissions'ı array'e çevir
  const userPermissions = user.permissions ? (Array.isArray(user.permissions) ? user.permissions : Array.from(user.permissions)) : []
  
  // Roles'ı array'e çevir
  const userRoles = user.roles ? (Array.isArray(user.roles) ? user.roles : Array.from(user.roles)) : []
  
  // Kullanıcı onaylanmamışsa (enabled=false veya yetkisi yoksa)
  if (!user.enabled || userPermissions.length === 0) {
    return (
      <div style={{ 
        display: 'flex', 
        flexDirection: 'column',
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh',
        padding: '40px',
        textAlign: 'center'
      }}>
        <h2 style={{ color: '#ef4444', marginBottom: '20px' }}>Erişim Bekleniyor</h2>
        <p style={{ color: '#666', fontSize: '16px' }}>
          Hesabınız henüz onaylanmamış. Yönetici tarafından onaylandıktan sonra sisteme erişebileceksiniz.
        </p>
      </div>
    )
  }

  // Role kontrolü (öncelikli)
  if (requiredRole && !userRoles.includes(requiredRole)) {
    return (
      <div style={{ 
        display: 'flex', 
        flexDirection: 'column',
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh',
        padding: '40px',
        textAlign: 'center'
      }}>
        <h2 style={{ color: '#ef4444', marginBottom: '20px' }}>Yetkiniz Yok</h2>
        <p style={{ color: '#666', fontSize: '16px' }}>
          Bu sayfaya erişmek için {requiredRole} rolüne sahip olmalısınız.
        </p>
      </div>
    )
  }

  // Yetki kontrolü
  const path = location.pathname
  let neededPermission = requiredPermission
  
  // Eğer requiredPermission belirtilmemişse, routePermissions'tan bul
  if (!neededPermission) {
    // Tam path kontrolü
    neededPermission = routePermissions[path]
    
    // Eğer bulunamazsa, pattern matching yap
    if (!neededPermission) {
      for (const [route, perm] of Object.entries(routePermissions)) {
        if (route.includes(':id')) {
          const pattern = route.replace(/:[^/]+/g, '[^/]+')
          const regex = new RegExp(`^${pattern}$`)
          if (regex.test(path)) {
            neededPermission = perm
            break
          }
        }
      }
    }
  }

  // Yetki gerekiyorsa kontrol et
  if (neededPermission && !userPermissions.includes(neededPermission)) {
    return (
      <div style={{ 
        display: 'flex', 
        flexDirection: 'column',
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh',
        padding: '40px',
        textAlign: 'center'
      }}>
        <h2 style={{ color: '#ef4444', marginBottom: '20px' }}>Yetkiniz Yok</h2>
        <p style={{ color: '#666', fontSize: '16px' }}>
          Bu sayfaya erişmek için gerekli yetkiye sahip değilsiniz.
        </p>
      </div>
    )
  }

  return <Layout>{children}</Layout>
}

export default ProtectedRoute

