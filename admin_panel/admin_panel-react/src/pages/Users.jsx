import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { userService } from '../services/userService'
import { getErrorMessage } from '../utils/errorHandler'
import './Users.css'

const Users = () => {
  const navigate = useNavigate()
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    loadUsers()
  }, [])

  const loadUsers = async () => {
    try {
      setLoading(true)
      const data = await userService.getAllUsers()
      setUsers(data)
      setError('')
    } catch (err) {
      setError(getErrorMessage(err, 'Kullanıcılar yüklenemedi'))
    } finally {
      setLoading(false)
    }
  }

  const handleAssignRoles = (id) => {
    navigate(`/users/${id}/roles`)
  }

  const handleToggleStatus = async (id, currentStatus) => {
    const newStatus = !currentStatus
    const action = newStatus ? 'onaylamak' : 'reddetmek'
    
    if (!window.confirm(`Bu kullanıcıyı ${action} istediğinizden emin misiniz?`)) {
      return
    }

    try {
      await userService.updateUserStatus(id, newStatus)
      loadUsers()
    } catch (err) {
      alert(getErrorMessage(err, 'Kullanıcı durumu güncellenemedi'))
    }
  }

  const formatDate = (dateString) => {
    if (!dateString) return '-'
    const date = new Date(dateString)
    return date.toLocaleDateString('tr-TR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  if (loading) {
    return <div className="loading">Yükleniyor...</div>
  }

  return (
    <div className="users-container">
      <div className="users-header">
        <h1>Kullanıcı Yönetimi</h1>
      </div>

      {error && typeof error === 'string' && <div className="error-message">{error}</div>}

      <div className="users-table-container">
        <table className="users-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Kullanıcı Adı</th>
              <th>E-posta</th>
              <th>Ad Soyad</th>
              <th>Roller</th>
              <th>Durum</th>
              <th>MFA</th>
              <th>Son Giriş</th>
              <th>İşlemler</th>
            </tr>
          </thead>
          <tbody>
            {users.length === 0 ? (
              <tr>
                <td colSpan="9" className="empty-message">
                  Henüz kullanıcı bulunmamaktadır
                </td>
              </tr>
            ) : (
              users.map((user) => (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td><strong>{user.username}</strong></td>
                  <td>{user.email}</td>
                  <td>{user.firstName} {user.lastName}</td>
                  <td>
                    {user.roles && user.roles.length > 0 ? (
                      <div className="roles-badge-container">
                        {user.roles.map((role, index) => (
                          <span key={index} className="role-badge">
                            {role.code}
                          </span>
                        ))}
                      </div>
                    ) : (
                      <span className="no-role">Rol yok</span>
                    )}
                  </td>
                  <td>
                    <span className={`status-badge ${user.enabled ? 'enabled' : 'disabled'}`}>
                      {user.enabled ? 'Aktif' : 'Onay Bekliyor'}
                    </span>
                  </td>
                  <td>
                    {user.mfaEnabled ? (
                      <span className="mfa-badge enabled">Etkin</span>
                    ) : (
                      <span className="mfa-badge disabled">Kapalı</span>
                    )}
                  </td>
                  <td>{formatDate(user.lastLoginAt)}</td>
                  <td>
                    <div className="action-buttons">
                      <button
                        onClick={() => handleAssignRoles(user.id)}
                        className="assign-button"
                      >
                        Roller
                      </button>
                      <button
                        onClick={() => handleToggleStatus(user.id, user.enabled)}
                        className={user.enabled ? 'disable-button' : 'enable-button'}
                      >
                        {user.enabled ? 'Reddet' : 'Onayla'}
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default Users

