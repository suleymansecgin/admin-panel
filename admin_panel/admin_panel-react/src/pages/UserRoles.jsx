import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { userService } from '../services/userService'
import { roleService } from '../services/roleService'
import { getErrorMessage } from '../utils/errorHandler'
import './UserRoles.css'

const UserRoles = () => {
  const navigate = useNavigate()
  const { id } = useParams()
  const [user, setUser] = useState(null)
  const [roles, setRoles] = useState([])
  const [selectedRoleIds, setSelectedRoleIds] = useState([])
  const [loading, setLoading] = useState(false)
  const [loadingData, setLoadingData] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  useEffect(() => {
    loadData()
  }, [id])

  const loadData = async () => {
    try {
      setLoadingData(true)
      const [userData, rolesData] = await Promise.all([
        userService.getUserById(parseInt(id)),
        roleService.getAllRoles()
      ])
      setUser(userData)
      setRoles(rolesData)
      setSelectedRoleIds(userData.roles?.map(r => r.id) || [])
      setError('')
    } catch (err) {
      setError(getErrorMessage(err, 'Veriler yüklenemedi'))
    } finally {
      setLoadingData(false)
    }
  }

  const handleRoleToggle = (roleId) => {
    setSelectedRoleIds(prev => {
      if (prev.includes(roleId)) {
        return prev.filter(id => id !== roleId)
      } else {
        return [...prev, roleId]
      }
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    setSuccess('')

    try {
      await userService.assignRolesToUser(parseInt(id), selectedRoleIds)
      setSuccess('Roller başarıyla güncellendi!')
      setTimeout(() => {
        navigate('/users')
      }, 1500)
    } catch (err) {
      setError(getErrorMessage(err, 'Roller güncellenemedi'))
    } finally {
      setLoading(false)
    }
  }

  if (loadingData) {
    return <div className="loading">Yükleniyor...</div>
  }

  if (!user) {
    return <div className="error-message">Kullanıcı bulunamadı</div>
  }

  return (
    <div className="user-roles-container">
      <div className="user-roles-card">
        <div className="form-header">
          <div>
            <h1>Kullanıcı Rolleri</h1>
            <p className="user-info">
              {user.firstName} {user.lastName} ({user.username})
            </p>
          </div>
          <button onClick={() => navigate('/users')} className="back-button">
            ← Geri Dön
          </button>
        </div>

        {error && typeof error === 'string' && <div className="error-message">{error}</div>}
        {success && <div className="success-message">{success}</div>}

        <form onSubmit={handleSubmit} className="user-roles-form">
          <div className="form-group">
            <label>Roller</label>
            <div className="roles-grid">
              {roles.length === 0 ? (
                <p className="no-roles">Henüz rol bulunmamaktadır</p>
              ) : (
                roles.map(role => (
                  <label key={role.id} className="role-checkbox">
                    <input
                      type="checkbox"
                      checked={selectedRoleIds.includes(role.id)}
                      onChange={() => handleRoleToggle(role.id)}
                    />
                    <div className="role-info">
                      <span className="role-code">{role.code}</span>
                      <span className="role-name">{role.name}</span>
                      {role.description && (
                        <span className="role-description">{role.description}</span>
                      )}
                      <span className="role-permissions">
                        {role.permissions?.length || 0} yetki
                      </span>
                    </div>
                  </label>
                ))
              )}
            </div>
          </div>

          <div className="form-actions">
            <button
              type="button"
              onClick={() => navigate('/users')}
              className="cancel-button"
            >
              İptal
            </button>
            <button
              type="submit"
              className="submit-button"
              disabled={loading}
            >
              {loading ? 'Kaydediliyor...' : 'Rolleri Güncelle'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default UserRoles

