import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { roleService } from '../services/roleService'
import { getErrorMessage } from '../utils/errorHandler'
import './RoleForm.css'

const RoleForm = () => {
  const navigate = useNavigate()
  const { id } = useParams()
  const isEdit = !!id

  const [formData, setFormData] = useState({
    code: '',
    name: '',
    description: '',
    permissionIds: []
  })
  const [permissions, setPermissions] = useState([])
  const [loading, setLoading] = useState(false)
  const [loadingData, setLoadingData] = useState(isEdit)
  const [error, setError] = useState('')

  useEffect(() => {
    loadPermissions()
    if (isEdit) {
      loadRole()
    }
  }, [id])

  const loadPermissions = async () => {
    try {
      const data = await roleService.getAllPermissions()
      setPermissions(data)
    } catch (err) {
      setError('Yetkiler yüklenemedi')
    }
  }

  const loadRole = async () => {
    try {
      setLoadingData(true)
      const role = await roleService.getRoleById(parseInt(id))
      setFormData({
        code: role.code,
        name: role.name,
        description: role.description || '',
        permissionIds: role.permissions?.map(p => p.id) || []
      })
    } catch (err) {
      setError(getErrorMessage(err, 'Rol yüklenemedi'))
    } finally {
      setLoadingData(false)
    }
  }

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handlePermissionToggle = (permissionId) => {
    setFormData(prev => {
      const permissionIds = prev.permissionIds.includes(permissionId)
        ? prev.permissionIds.filter(id => id !== permissionId)
        : [...prev.permissionIds, permissionId]
      return {
        ...prev,
        permissionIds
      }
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')

    try {
      if (isEdit) {
        await roleService.updateRole(parseInt(id), formData)
      } else {
        await roleService.createRole(formData)
      }
      navigate('/roles')
    } catch (err) {
      setError(getErrorMessage(err, 'Rol kaydedilemedi'))
    } finally {
      setLoading(false)
    }
  }

  if (loadingData) {
    return <div className="loading">Yükleniyor...</div>
  }

  return (
    <div className="role-form-container">
      <div className="role-form-card">
        <div className="form-header">
          <h1>{isEdit ? 'Rol Düzenle' : 'Yeni Rol Oluştur'}</h1>
          <button onClick={() => navigate('/roles')} className="back-button">
            ← Geri Dön
          </button>
        </div>

        {error && typeof error === 'string' && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit} className="role-form">
          <div className="form-group">
            <label htmlFor="code">Rol Kodu *</label>
            <input
              type="text"
              id="code"
              name="code"
              value={formData.code}
              onChange={handleChange}
              required
              placeholder="Örn: ADMIN"
              disabled={isEdit}
            />
            <small>Rol kodu benzersiz olmalıdır ve düzenlenemez</small>
          </div>

          <div className="form-group">
            <label htmlFor="name">Rol Adı *</label>
            <input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              placeholder="Örn: Yönetici"
            />
          </div>

          <div className="form-group">
            <label htmlFor="description">Açıklama</label>
            <textarea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              rows="3"
              placeholder="Rol açıklaması..."
            />
          </div>

          <div className="form-group">
            <label>Yetkiler</label>
            <div className="permissions-grid">
              {permissions.map(permission => (
                <label key={permission.id} className="permission-checkbox">
                  <input
                    type="checkbox"
                    checked={formData.permissionIds.includes(permission.id)}
                    onChange={() => handlePermissionToggle(permission.id)}
                  />
                  <div className="permission-info">
                    <span className="permission-code">{permission.code}</span>
                    <span className="permission-name">{permission.name}</span>
                  </div>
                </label>
              ))}
            </div>
            {permissions.length === 0 && (
              <p className="no-permissions">Henüz yetki bulunmamaktadır</p>
            )}
          </div>

          <div className="form-actions">
            <button
              type="button"
              onClick={() => navigate('/roles')}
              className="cancel-button"
            >
              İptal
            </button>
            <button
              type="submit"
              className="submit-button"
              disabled={loading}
            >
              {loading ? 'Kaydediliyor...' : (isEdit ? 'Güncelle' : 'Oluştur')}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default RoleForm

