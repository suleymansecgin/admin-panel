import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { siparisService } from '../services/siparisService'
import { pazaryeriService } from '../services/pazaryeriService'
import { getErrorMessage } from '../utils/errorHandler'
import './Siparisler.css'

const Siparisler = () => {
  const navigate = useNavigate()
  const [siparisler, setSiparisler] = useState([])
  const [pazaryerleri, setPazaryerleri] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [filterDurum, setFilterDurum] = useState('')
  const [filterPazaryeri, setFilterPazaryeri] = useState('')
  const [selectedSiparis, setSelectedSiparis] = useState(null)
  const [durumFormData, setDurumFormData] = useState({
    durum: '',
    kargoTakipNo: '',
    kargoFirmasi: ''
  })

  useEffect(() => {
    loadSiparisler()
    loadPazaryerleri()
  }, [])

  const loadSiparisler = async () => {
    try {
      setLoading(true)
      let data
      if (filterDurum) {
        data = await siparisService.getSiparislerByDurum(filterDurum)
      } else if (filterPazaryeri) {
        data = await siparisService.getSiparislerByPazaryeri(parseInt(filterPazaryeri))
      } else {
        data = await siparisService.getAllSiparisler()
      }
      setSiparisler(data)
      setError('')
    } catch (err) {
      setError(getErrorMessage(err, 'Siparişler yüklenemedi'))
    } finally {
      setLoading(false)
    }
  }

  const loadPazaryerleri = async () => {
    try {
      const data = await pazaryeriService.getAllPazaryerleri()
      setPazaryerleri(data)
    } catch (err) {
      console.error('Pazaryerleri yüklenemedi:', err)
    }
  }

  useEffect(() => {
    loadSiparisler()
  }, [filterDurum, filterPazaryeri])

  const handleDurumGuncelle = async (siparisId) => {
    try {
      await siparisService.updateSiparisDurum(siparisId, durumFormData)
      setSelectedSiparis(null)
      setDurumFormData({ durum: '', kargoTakipNo: '', kargoFirmasi: '' })
      loadSiparisler()
      alert('Sipariş durumu güncellendi')
    } catch (err) {
      alert(getErrorMessage(err, 'Sipariş durumu güncellenemedi'))
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

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY'
    }).format(amount)
  }

  const durumRenkleri = {
    'Yeni': '#007bff',
    'Hazırlanıyor': '#ffc107',
    'Kargoya Verildi': '#17a2b8',
    'Teslim Edildi': '#28a745',
    'İptal': '#dc3545'
  }

  if (loading) {
    return <div className="loading">Yükleniyor...</div>
  }

  return (
    <div className="siparisler-container">
      <div className="siparisler-header">
        <h1>Sipariş Yönetimi</h1>
      </div>

      {error && typeof error === 'string' && <div className="error-message">{error}</div>}

      <div className="filters">
        <div className="filter-group">
          <label>Durum Filtresi:</label>
          <select value={filterDurum} onChange={(e) => { setFilterDurum(e.target.value); setFilterPazaryeri(''); }}>
            <option value="">Tümü</option>
            <option value="Yeni">Yeni</option>
            <option value="Hazırlanıyor">Hazırlanıyor</option>
            <option value="Kargoya Verildi">Kargoya Verildi</option>
            <option value="Teslim Edildi">Teslim Edildi</option>
            <option value="İptal">İptal</option>
          </select>
        </div>
        <div className="filter-group">
          <label>Pazaryeri Filtresi:</label>
          <select value={filterPazaryeri} onChange={(e) => { setFilterPazaryeri(e.target.value); setFilterDurum(''); }}>
            <option value="">Tümü</option>
            {pazaryerleri.map((p) => (
              <option key={p.id} value={p.id}>{p.pazaryeriAdi}</option>
            ))}
          </select>
        </div>
      </div>

      {selectedSiparis && (
        <div className="form-container">
          <h2>Sipariş Durumu Güncelle</h2>
          <form onSubmit={(e) => { e.preventDefault(); handleDurumGuncelle(selectedSiparis.id); }}>
            <div className="form-group">
              <label>Durum *</label>
              <select
                value={durumFormData.durum}
                onChange={(e) => setDurumFormData({ ...durumFormData, durum: e.target.value })}
                required
              >
                <option value="">Seçiniz</option>
                <option value="Yeni">Yeni</option>
                <option value="Hazırlanıyor">Hazırlanıyor</option>
                <option value="Kargoya Verildi">Kargoya Verildi</option>
                <option value="Teslim Edildi">Teslim Edildi</option>
                <option value="İptal">İptal</option>
              </select>
            </div>
            <div className="form-group">
              <label>Kargo Takip No</label>
              <input
                type="text"
                value={durumFormData.kargoTakipNo}
                onChange={(e) => setDurumFormData({ ...durumFormData, kargoTakipNo: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>Kargo Firması</label>
              <input
                type="text"
                value={durumFormData.kargoFirmasi}
                onChange={(e) => setDurumFormData({ ...durumFormData, kargoFirmasi: e.target.value })}
              />
            </div>
            <div className="form-actions">
              <button type="submit" className="save-button">Güncelle</button>
              <button type="button" onClick={() => { setSelectedSiparis(null); setDurumFormData({ durum: '', kargoTakipNo: '', kargoFirmasi: '' }); }} className="cancel-button">
                İptal
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="siparisler-table-container">
        <table className="siparisler-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Pazaryeri</th>
              <th>Sipariş No</th>
              <th>Müşteri</th>
              <th>Tutar</th>
              <th>Durum</th>
              <th>Tarih</th>
              <th>İşlemler</th>
            </tr>
          </thead>
          <tbody>
            {siparisler.length === 0 ? (
              <tr>
                <td colSpan="8" className="no-data">Henüz sipariş yok</td>
              </tr>
            ) : (
              siparisler.map((siparis) => (
                <tr key={siparis.id}>
                  <td>{siparis.id}</td>
                  <td>{siparis.pazaryeriAdi}</td>
                  <td>{siparis.pazaryeriSiparisId}</td>
                  <td>{siparis.musteriAdi || '-'}</td>
                  <td>{formatCurrency(siparis.toplamTutar)}</td>
                  <td>
                    <span className="durum-badge" style={{ backgroundColor: durumRenkleri[siparis.durum] || '#6c757d', color: 'white' }}>
                      {siparis.durum}
                    </span>
                  </td>
                  <td>{formatDate(siparis.siparisTarihi)}</td>
                  <td>
                    <button onClick={() => { setSelectedSiparis(siparis); setDurumFormData({ durum: siparis.durum, kargoTakipNo: siparis.kargoTakipNo || '', kargoFirmasi: siparis.kargoFirmasi || '' }); }} className="edit-button">
                      Durum Güncelle
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

export default Siparisler

