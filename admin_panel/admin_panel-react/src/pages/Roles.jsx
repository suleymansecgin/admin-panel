import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { roleService } from '../services/roleService'
import { getErrorMessage } from '../utils/errorHandler'
import './Roles.css'

const Roles = () => {
  const navigate = useNavigate()
  const [roles, setRoles] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    loadRoles()
  }, [])

  const loadRoles = async () => {
    try {
      setLoading(true)
      const data = await roleService.getAllRoles()
      setRoles(data)
      setError('')
    } catch (err) {
      setError(getErrorMessage(err, 'Roller yüklenemedi'))
    } finally {
      setLoading(false)
    }
  }

  const handleCreate = () => {
    navigate('/roles/new')
  }

  const handleEdit = (id) => {
    navigate(`/roles/${id}/edit`)
  }

  const handleDelete = async (id, code) => {
    if (!window.confirm(`"${code}" rolünü silmek istediğinizden emin misiniz?`)) {
      return
    }

    try {
      await roleService.deleteRole(id)
      loadRoles()
    } catch (err) {
      alert(getErrorMessage(err, 'Rol silinemedi'))
    }
  }

  if (loading) {
    return <div className="loading">Yükleniyor...</div>
  }

  return (
    <div className="roles-container">
      <div className="roles-header">
        <h1>Rol Yönetimi</h1>
        <div className="header-actions">
          <button onClick={handleCreate} className="create-button">
            + Yeni Rol
          </button>
        </div>
      </div>

      {error && typeof error === 'string' && <div className="error-message">{error}</div>}

      <div className="roles-table-container">
        <table className="roles-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Kod</th>
              <th>Ad</th>
              <th>Açıklama</th>
              <th>Yetki Sayısı</th>
              <th>İşlemler</th>
            </tr>
          </thead>
          <tbody>
            {roles.length === 0 ? (
              <tr>
                <td colSpan="6" className="empty-message">
                  Henüz rol bulunmamaktadır
                </td>
              </tr>
            ) : (
              roles.map((role) => (
                <tr key={role.id}>
                  <td>{role.id}</td>
                  <td><code>{role.code}</code></td>
                  <td>{role.name}</td>
                  <td>{role.description || '-'}</td>
                  <td>{role.permissions?.length || 0}</td>
                  <td>
                    <div className="action-buttons">
                      <button
                        onClick={() => handleEdit(role.id)}
                        className="edit-button"
                      >
                        Düzenle
                      </button>
                      <button
                        onClick={() => handleDelete(role.id, role.code)}
                        className="delete-button"
                      >
                        Sil
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

export default Roles

