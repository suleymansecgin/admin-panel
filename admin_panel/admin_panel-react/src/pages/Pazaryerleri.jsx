import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { pazaryeriService } from '../services/pazaryeriService'
import { getErrorMessage } from '../utils/errorHandler'
import './Pazaryerleri.css'

const Pazaryerleri = () => {
  const navigate = useNavigate()
  const [pazaryerleri, setPazaryerleri] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [formData, setFormData] = useState({
    pazaryeriAdi: '',
    apiKey: '',
    secretKey: '',
    yetkiToken: '',
    aktif: true,
    ekstraAyarlar: ''
  })
  const [jsonError, setJsonError] = useState('')

  useEffect(() => {
    loadPazaryerleri()
  }, [])

  const loadPazaryerleri = async () => {
    try {
      setLoading(true)
      const data = await pazaryeriService.getAllPazaryerleri()
      setPazaryerleri(data)
      setError('')
    } catch (err) {
      setError(getErrorMessage(err, 'Pazaryerleri yüklenemedi'))
    } finally {
      setLoading(false)
    }
  }

  const validateJson = (jsonString) => {
    if (!jsonString || jsonString.trim() === '') {
      return { valid: true, error: null }
    }
    try {
      JSON.parse(jsonString)
      return { valid: true, error: null }
    } catch (e) {
      return { valid: false, error: 'Geçersiz JSON formatı: ' + e.message }
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    
    // JSON validation
    const jsonValidation = validateJson(formData.ekstraAyarlar)
    if (!jsonValidation.valid) {
      alert(jsonValidation.error)
      return
    }

    try {
      // Form verilerini hazırla
      const submitData = {
        ...formData,
        // Boş string yerine null gönder (backend için daha iyi)
        ekstraAyarlar: formData.ekstraAyarlar.trim() === '' ? null : formData.ekstraAyarlar.trim()
      }

      if (editingId) {
        await pazaryeriService.updatePazaryeri(editingId, submitData)
      } else {
        await pazaryeriService.createPazaryeri(submitData)
      }
      setShowForm(false)
      setEditingId(null)
      resetForm()
      loadPazaryerleri()
    } catch (err) {
      const errorMessage = getErrorMessage(err, 'Pazaryeri kaydedilemedi')
      alert(errorMessage)
      console.error('Pazaryeri kaydetme hatası:', err)
    }
  }

  const handleEdit = (pazaryeri) => {
    const ekstraAyarlar = pazaryeri.ekstraAyarlar || ''
    setFormData({
      pazaryeriAdi: pazaryeri.pazaryeriAdi,
      apiKey: pazaryeri.apiKey || '',
      secretKey: pazaryeri.secretKey || '',
      yetkiToken: pazaryeri.yetkiToken || '',
      aktif: pazaryeri.aktif,
      ekstraAyarlar: ekstraAyarlar
    })
    // JSON validation kontrolü
    if (ekstraAyarlar.trim() !== '') {
      const validation = validateJson(ekstraAyarlar)
      setJsonError(validation.valid ? '' : validation.error)
    } else {
      setJsonError('')
    }
    setEditingId(pazaryeri.id)
    setShowForm(true)
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Bu pazaryerini silmek istediğinizden emin misiniz?')) {
      return
    }
    try {
      await pazaryeriService.deletePazaryeri(id)
      loadPazaryerleri()
    } catch (err) {
      alert(getErrorMessage(err, 'Pazaryeri silinemedi'))
    }
  }

  const resetForm = () => {
    setFormData({
      pazaryeriAdi: '',
      apiKey: '',
      secretKey: '',
      yetkiToken: '',
      aktif: true,
      ekstraAyarlar: ''
    })
    setJsonError('')
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
    <div className="pazaryerleri-container">
      <div className="pazaryerleri-header">
        <h1>Pazaryeri Yönetimi</h1>
        <div>
          <button onClick={() => setShowForm(!showForm)} className="add-button">
            {showForm ? 'İptal' : '+ Yeni Pazaryeri'}
          </button>
        </div>
      </div>

      {error && typeof error === 'string' && <div className="error-message">{error}</div>}

      {showForm && (
        <div className="form-container">
          <h2>{editingId ? 'Pazaryeri Düzenle' : 'Yeni Pazaryeri'}</h2>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Pazaryeri Adı *</label>
              <input
                type="text"
                value={formData.pazaryeriAdi}
                onChange={(e) => setFormData({ ...formData, pazaryeriAdi: e.target.value })}
                required
                placeholder="Hepsiburada, Trendyol, Amazon vb."
              />
            </div>
            <div className="form-group">
              <label>API Key</label>
              <input
                type="text"
                value={formData.apiKey}
                onChange={(e) => setFormData({ ...formData, apiKey: e.target.value })}
                placeholder="API anahtarı"
              />
            </div>
            <div className="form-group">
              <label>Secret Key</label>
              <input
                type="password"
                value={formData.secretKey}
                onChange={(e) => setFormData({ ...formData, secretKey: e.target.value })}
                placeholder="Gizli anahtar"
              />
            </div>
            <div className="form-group">
              <label>Yetki Token</label>
              <input
                type="text"
                value={formData.yetkiToken}
                onChange={(e) => setFormData({ ...formData, yetkiToken: e.target.value })}
                placeholder="Yetkilendirme token'ı"
              />
            </div>
            <div className="form-group">
              <label>
                <input
                  type="checkbox"
                  checked={formData.aktif}
                  onChange={(e) => setFormData({ ...formData, aktif: e.target.checked })}
                />
                Aktif
              </label>
              {formData.aktif && (
                <div style={{ fontSize: '12px', color: '#666', marginTop: '4px', fontStyle: 'italic' }}>
                  Not: Aktif seçeneği işaretliyken API kimlik bilgileri doğrulanacaktır. Test anahtarları için bu seçeneği kapatın.
                </div>
              )}
            </div>
            <div className="form-group">
              <label>Ekstra Ayarlar (JSON)</label>
              <textarea
                value={formData.ekstraAyarlar}
                onChange={(e) => {
                  const value = e.target.value
                  setFormData({ ...formData, ekstraAyarlar: value })
                  // Real-time JSON validation
                  if (value.trim() === '') {
                    setJsonError('')
                  } else {
                    const validation = validateJson(value)
                    setJsonError(validation.valid ? '' : validation.error)
                  }
                }}
                placeholder='{"key": "value"}'
                rows="3"
                className={jsonError ? 'error-input' : ''}
              />
              {jsonError && <div className="error-message" style={{ color: 'red', fontSize: '12px', marginTop: '4px' }}>{jsonError}</div>}
            </div>
            <div className="form-actions">
              <button type="submit" className="save-button">
                {editingId ? 'Güncelle' : 'Kaydet'}
              </button>
              <button type="button" onClick={() => { setShowForm(false); resetForm(); setEditingId(null); }} className="cancel-button">
                İptal
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="pazaryerleri-table-container">
        <table className="pazaryerleri-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Pazaryeri Adı</th>
              <th>Son Senkronizasyon</th>
              <th>Durum</th>
              <th>Oluşturulma</th>
              <th>İşlemler</th>
            </tr>
          </thead>
          <tbody>
            {pazaryerleri.length === 0 ? (
              <tr>
                <td colSpan="6" className="no-data">Henüz pazaryeri eklenmemiş</td>
              </tr>
            ) : (
              pazaryerleri.map((pazaryeri) => (
                <tr key={pazaryeri.id}>
                  <td>{pazaryeri.id}</td>
                  <td>{pazaryeri.pazaryeriAdi}</td>
                  <td>{formatDate(pazaryeri.sonSenkronizasyonTarihi)}</td>
                  <td>
                    <span className={`status-badge ${pazaryeri.aktif ? 'active' : 'inactive'}`}>
                      {pazaryeri.aktif ? 'Aktif' : 'Pasif'}
                    </span>
                  </td>
                  <td>{formatDate(pazaryeri.createdAt)}</td>
                  <td>
                    <button onClick={() => handleEdit(pazaryeri)} className="edit-button">
                      Düzenle
                    </button>
                    <button onClick={() => handleDelete(pazaryeri.id)} className="delete-button">
                      Sil
                    </button>
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

export default Pazaryerleri

