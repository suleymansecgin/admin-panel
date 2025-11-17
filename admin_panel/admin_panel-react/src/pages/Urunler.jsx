import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { urunService } from '../services/urunService'
import { pazaryeriService } from '../services/pazaryeriService'
import { getErrorMessage } from '../utils/errorHandler'
import './Urunler.css'

const Urunler = () => {
  const navigate = useNavigate()
  const [urunler, setUrunler] = useState([])
  const [pazaryerleri, setPazaryerleri] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [showEslesmeForm, setShowEslesmeForm] = useState(false)
  const [selectedUrunId, setSelectedUrunId] = useState(null)
  const [editingId, setEditingId] = useState(null)
  const [editingEslesmeId, setEditingEslesmeId] = useState(null)
  const [currentUrunEslesmeleri, setCurrentUrunEslesmeleri] = useState([])
  const [formData, setFormData] = useState({
    urunKodu: '',
    urunAdi: '',
    aciklama: '',
    stokMiktari: 0,
    temelFiyat: '',
    maliyetFiyati: '',
    kategori: '',
    resimUrl: '',
    aktif: true
  })
  const [eslesmeFormData, setEslesmeFormData] = useState({
    pazaryeriId: '',
    pazaryeriUrunKodu: '',
    fiyat: '',
    aktif: true
  })

  useEffect(() => {
    loadUrunler()
    loadPazaryerleri()
  }, [])

  const loadUrunler = async () => {
    try {
      setLoading(true)
      const data = await urunService.getAllUrunler()
      setUrunler(data)
      setError('')
    } catch (err) {
      setError(getErrorMessage(err, 'Ürünler yüklenemedi'))
    } finally {
      setLoading(false)
    }
  }

  const loadPazaryerleri = async () => {
    try {
      const data = await pazaryeriService.getAktifPazaryerleri()
      setPazaryerleri(data)
    } catch (err) {
      console.error('Pazaryerleri yüklenemedi:', err)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      const submitData = {
        ...formData,
        stokMiktari: parseInt(formData.stokMiktari),
        temelFiyat: parseFloat(formData.temelFiyat),
        maliyetFiyati: formData.maliyetFiyati ? parseFloat(formData.maliyetFiyati) : null
      }
      if (editingId) {
        await urunService.updateUrun(editingId, submitData)
      } else {
        await urunService.createUrun(submitData)
      }
      setShowForm(false)
      setEditingId(null)
      setCurrentUrunEslesmeleri([])
      resetForm()
      loadUrunler()
    } catch (err) {
      alert(getErrorMessage(err, 'Ürün kaydedilemedi'))
    }
  }

  const handleEslesmeSubmit = async (e) => {
    e.preventDefault()
    try {
      const submitData = {
        ...eslesmeFormData,
        urunId: selectedUrunId,
        pazaryeriId: parseInt(eslesmeFormData.pazaryeriId),
        fiyat: eslesmeFormData.fiyat ? parseFloat(eslesmeFormData.fiyat) : null,
        aktif: eslesmeFormData.aktif
      }
      if (editingEslesmeId) {
        await urunService.updateEslesme(editingEslesmeId, submitData)
      } else {
        await urunService.createEslesme(selectedUrunId, submitData)
      }
      setShowEslesmeForm(false)
      setEditingEslesmeId(null)
      resetEslesmeForm()
      // Eğer düzenleme modundaysak, eşleştirmeleri yeniden yükle
      if (editingId) {
        const allUrunler = await urunService.getAllUrunler()
        const foundUrun = allUrunler.find(u => u.id === editingId)
        if (foundUrun) {
          setCurrentUrunEslesmeleri(foundUrun.pazaryeriEslesmeleri || [])
        }
      }
      await loadUrunler()
    } catch (err) {
      alert(getErrorMessage(err, 'Eşleştirme kaydedilemedi'))
    }
  }

  const handleEdit = (urun) => {
    setFormData({
      urunKodu: urun.urunKodu,
      urunAdi: urun.urunAdi,
      aciklama: urun.aciklama || '',
      stokMiktari: urun.stokMiktari,
      temelFiyat: urun.temelFiyat,
      maliyetFiyati: urun.maliyetFiyati || '',
      kategori: urun.kategori || '',
      resimUrl: urun.resimUrl || '',
      aktif: urun.aktif
    })
    setEditingId(urun.id)
    setCurrentUrunEslesmeleri(urun.pazaryeriEslesmeleri || [])
    setShowForm(true)
  }

  const handleEditEslesme = (eslesme) => {
    setEslesmeFormData({
      pazaryeriId: eslesme.pazaryeriId.toString(),
      pazaryeriUrunKodu: eslesme.pazaryeriUrunKodu,
      fiyat: eslesme.fiyat || '',
      aktif: eslesme.aktif
    })
    setSelectedUrunId(eslesme.urunId)
    setEditingEslesmeId(eslesme.id)
    setShowEslesmeForm(true)
  }

  const handleDeleteEslesme = async (id) => {
    if (!window.confirm('Bu eşleştirmeyi silmek istediğinizden emin misiniz?')) {
      return
    }
    try {
      await urunService.deleteEslesme(id)
      // Eğer düzenleme modundaysak, eşleştirmeleri yeniden yükle
      if (editingId) {
        const allUrunler = await urunService.getAllUrunler()
        const foundUrun = allUrunler.find(u => u.id === editingId)
        if (foundUrun) {
          setCurrentUrunEslesmeleri(foundUrun.pazaryeriEslesmeleri || [])
        }
      }
      await loadUrunler()
    } catch (err) {
      alert(getErrorMessage(err, 'Eşleştirme silinemedi'))
    }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Bu ürünü silmek istediğinizden emin misiniz?')) {
      return
    }
    try {
      await urunService.deleteUrun(id)
      loadUrunler()
    } catch (err) {
      alert(getErrorMessage(err, 'Ürün silinemedi'))
    }
  }

  const handleAddEslesme = (urunId) => {
    setSelectedUrunId(urunId)
    setEditingEslesmeId(null)
    resetEslesmeForm()
    setShowEslesmeForm(true)
  }

  const resetForm = () => {
    setFormData({
      urunKodu: '',
      urunAdi: '',
      aciklama: '',
      stokMiktari: 0,
      temelFiyat: '',
      maliyetFiyati: '',
      kategori: '',
      resimUrl: '',
      aktif: true
    })
  }

  const resetEslesmeForm = () => {
    setEslesmeFormData({
      pazaryeriId: '',
      pazaryeriUrunKodu: '',
      fiyat: '',
      aktif: true
    })
    setSelectedUrunId(null)
    setEditingEslesmeId(null)
  }

  const formatDate = (dateString) => {
    if (!dateString) return '-'
    const date = new Date(dateString)
    return date.toLocaleDateString('tr-TR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    })
  }

  if (loading) {
    return <div className="loading">Yükleniyor...</div>
  }

  return (
    <div className="urunler-container">
      <div className="urunler-header">
        <h1>Ürün Yönetimi</h1>
        <div>
          <button onClick={() => setShowForm(!showForm)} className="add-button">
            {showForm ? 'İptal' : '+ Yeni Ürün'}
          </button>
        </div>
      </div>

      {error && typeof error === 'string' && <div className="error-message">{error}</div>}

      {showForm && (
        <div className="form-container">
          <h2>{editingId ? 'Ürün Düzenle' : 'Yeni Ürün'}</h2>
          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-group">
                <label>Ürün Kodu *</label>
                <input
                  type="text"
                  value={formData.urunKodu}
                  onChange={(e) => setFormData({ ...formData, urunKodu: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label>Ürün Adı *</label>
                <input
                  type="text"
                  value={formData.urunAdi}
                  onChange={(e) => setFormData({ ...formData, urunAdi: e.target.value })}
                  required
                />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Stok Miktarı *</label>
                <input
                  type="number"
                  value={formData.stokMiktari}
                  onChange={(e) => setFormData({ ...formData, stokMiktari: e.target.value })}
                  required
                  min="0"
                />
              </div>
              <div className="form-group">
                <label>Temel Fiyat *</label>
                <input
                  type="number"
                  step="0.01"
                  value={formData.temelFiyat}
                  onChange={(e) => setFormData({ ...formData, temelFiyat: e.target.value })}
                  required
                  min="0"
                />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Maliyet Fiyatı</label>
                <input
                  type="number"
                  step="0.01"
                  value={formData.maliyetFiyati}
                  onChange={(e) => setFormData({ ...formData, maliyetFiyati: e.target.value })}
                  min="0"
                  placeholder="Kar-zarar analizi için"
                />
              </div>
            </div>
            <div className="form-group">
              <label>Açıklama</label>
              <textarea
                value={formData.aciklama}
                onChange={(e) => setFormData({ ...formData, aciklama: e.target.value })}
                rows="3"
              />
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Kategori</label>
                <input
                  type="text"
                  value={formData.kategori}
                  onChange={(e) => setFormData({ ...formData, kategori: e.target.value })}
                />
              </div>
              <div className="form-group">
                <label>Resim URL</label>
                <input
                  type="url"
                  value={formData.resimUrl}
                  onChange={(e) => setFormData({ ...formData, resimUrl: e.target.value })}
                />
              </div>
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
            </div>

            {editingId && currentUrunEslesmeleri && currentUrunEslesmeleri.length > 0 && (
              <div className="eslesmeler-section">
                <h3>Pazaryeri Eşleştirmeleri</h3>
                <div className="eslesmeler-list">
                  {currentUrunEslesmeleri.map((eslesme) => (
                    <div key={eslesme.id} className="eslesme-item">
                      <div className="eslesme-info">
                        <div className="eslesme-header">
                          <strong>{eslesme.pazaryeriAdi}</strong>
                          <span className={`status-badge ${eslesme.aktif ? 'active' : 'inactive'}`}>
                            {eslesme.aktif ? 'Aktif' : 'Pasif'}
                          </span>
                        </div>
                        <div className="eslesme-details">
                          <div><strong>Ürün Kodu:</strong> {eslesme.pazaryeriUrunKodu}</div>
                          <div><strong>Fiyat:</strong> {eslesme.fiyat ? `${eslesme.fiyat} ₺` : 'Temel fiyat kullanılıyor'}</div>
                        </div>
                      </div>
                      <div className="eslesme-actions">
                        <button 
                          onClick={() => handleEditEslesme(eslesme)} 
                          className="edit-button-small"
                          title="Düzenle"
                        >
                          ✏️
                        </button>
                        <button 
                          onClick={() => handleDeleteEslesme(eslesme.id)} 
                          className="delete-button-small"
                          title="Sil"
                        >
                          🗑️
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
                <button 
                  onClick={() => handleAddEslesme(editingId)} 
                  className="add-eslesme-button-inline"
                >
                  + Yeni Eşleştirme Ekle
                </button>
              </div>
            )}

            {editingId && (!currentUrunEslesmeleri || currentUrunEslesmeleri.length === 0) && (
              <div className="eslesmeler-section">
                <h3>Pazaryeri Eşleştirmeleri</h3>
                <p className="no-eslesme-message">Henüz pazaryeri eşleştirmesi yok.</p>
                <button 
                  onClick={() => handleAddEslesme(editingId)} 
                  className="add-eslesme-button-inline"
                >
                  + İlk Eşleştirmeyi Ekle
                </button>
              </div>
            )}

            <div className="form-actions">
              <button type="submit" className="save-button">
                {editingId ? 'Güncelle' : 'Kaydet'}
              </button>
              <button type="button" onClick={() => { setShowForm(false); resetForm(); setEditingId(null); setCurrentUrunEslesmeleri([]); }} className="cancel-button">
                İptal
              </button>
            </div>
          </form>
        </div>
      )}

      {showEslesmeForm && (
        <div className="form-container">
          <h2>{editingEslesmeId ? 'Pazaryeri Eşleştirmesi Düzenle' : 'Pazaryeri Eşleştirmesi Ekle'}</h2>
          <form onSubmit={handleEslesmeSubmit}>
            <div className="form-group">
              <label>Pazaryeri *</label>
              <select
                value={eslesmeFormData.pazaryeriId}
                onChange={(e) => setEslesmeFormData({ ...eslesmeFormData, pazaryeriId: e.target.value })}
                required
                disabled={!!editingEslesmeId}
              >
                <option value="">Seçiniz</option>
                {pazaryerleri.map((p) => (
                  <option key={p.id} value={p.id}>{p.pazaryeriAdi}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Pazaryeri Ürün Kodu (ASIN/MLID) *</label>
              <input
                type="text"
                value={eslesmeFormData.pazaryeriUrunKodu}
                onChange={(e) => setEslesmeFormData({ ...eslesmeFormData, pazaryeriUrunKodu: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label>Fiyat (Boş bırakılırsa temel fiyat kullanılır)</label>
              <input
                type="number"
                step="0.01"
                value={eslesmeFormData.fiyat}
                onChange={(e) => setEslesmeFormData({ ...eslesmeFormData, fiyat: e.target.value })}
                min="0"
              />
            </div>
            <div className="form-group">
              <label>
                <input
                  type="checkbox"
                  checked={eslesmeFormData.aktif}
                  onChange={(e) => setEslesmeFormData({ ...eslesmeFormData, aktif: e.target.checked })}
                />
                Aktif
              </label>
            </div>
            <div className="form-actions">
              <button type="submit" className="save-button">
                {editingEslesmeId ? 'Güncelle' : 'Kaydet'}
              </button>
              <button type="button" onClick={() => { setShowEslesmeForm(false); resetEslesmeForm(); }} className="cancel-button">
                İptal
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="urunler-table-container">
        <table className="urunler-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Ürün Kodu</th>
              <th>Ürün Adı</th>
              <th>Stok</th>
              <th>Fiyat</th>
              <th>Kategori</th>
              <th>Durum</th>
              <th>Eşleştirmeler</th>
              <th>İşlemler</th>
            </tr>
          </thead>
          <tbody>
            {urunler.length === 0 ? (
              <tr>
                <td colSpan="9" className="no-data">Henüz ürün eklenmemiş</td>
              </tr>
            ) : (
              urunler.map((urun) => (
                <tr key={urun.id}>
                  <td>{urun.id}</td>
                  <td>{urun.urunKodu}</td>
                  <td>{urun.urunAdi}</td>
                  <td>{urun.stokMiktari}</td>
                  <td>{urun.temelFiyat} ₺</td>
                  <td>{urun.kategori || '-'}</td>
                  <td>
                    <span className={`status-badge ${urun.aktif ? 'active' : 'inactive'}`}>
                      {urun.aktif ? 'Aktif' : 'Pasif'}
                    </span>
                  </td>
                  <td>
                    {urun.pazaryeriEslesmeleri && urun.pazaryeriEslesmeleri.length > 0 ? (
                      <div className="eslesme-list">
                        {urun.pazaryeriEslesmeleri.map((e, idx) => (
                          <span key={idx} className="eslesme-badge">{e.pazaryeriAdi}</span>
                        ))}
                      </div>
                    ) : (
                      '-'
                    )}
                    <button onClick={() => handleAddEslesme(urun.id)} className="add-eslesme-button">
                      + Eşleştirme Ekle
                    </button>
                  </td>
                  <td>
                    <button onClick={() => handleEdit(urun)} className="edit-button">
                      Düzenle
                    </button>
                    <button onClick={() => handleDelete(urun.id)} className="delete-button">
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

export default Urunler

