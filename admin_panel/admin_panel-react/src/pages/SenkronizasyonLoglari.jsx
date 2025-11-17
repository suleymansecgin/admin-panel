import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { senkronizasyonService } from '../services/senkronizasyonService'
import { pazaryeriService } from '../services/pazaryeriService'
import { getErrorMessage } from '../utils/errorHandler'
import './SenkronizasyonLoglari.css'

const SenkronizasyonLoglari = () => {
  const navigate = useNavigate()
  const [loglar, setLoglar] = useState([])
  const [pazaryerleri, setPazaryerleri] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [filterPazaryeri, setFilterPazaryeri] = useState('')
  const [filterDurum, setFilterDurum] = useState('')
  const [filterIslemTipi, setFilterIslemTipi] = useState('')

  useEffect(() => {
    loadLoglar()
    loadPazaryerleri()
  }, [])

  useEffect(() => {
    loadLoglar()
  }, [filterPazaryeri, filterDurum, filterIslemTipi])

  const loadLoglar = async () => {
    try {
      setLoading(true)
      let data
      if (filterPazaryeri) {
        data = await senkronizasyonService.getLogsByPazaryeri(parseInt(filterPazaryeri))
      } else if (filterDurum === 'Hata') {
        data = await senkronizasyonService.getSonHatalar()
      } else {
        data = await senkronizasyonService.getAllLogs()
      }
      
      // Filtreleme
      if (filterDurum && filterDurum !== 'Hata') {
        data = data.filter(log => log.durum === filterDurum)
      }
      if (filterIslemTipi) {
        data = data.filter(log => log.islemTipi === filterIslemTipi)
      }
      
      setLoglar(data)
      setError('')
    } catch (err) {
      setError(getErrorMessage(err, 'Loglar yüklenemedi'))
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

  const handleSyncOrders = async () => {
    if (!window.confirm('Tüm pazaryerlerinden sipariş senkronizasyonu başlatılsın mı?')) {
      return
    }
    try {
      await senkronizasyonService.syncOrdersFromAllPazaryerleri()
      alert('Senkronizasyon başlatıldı')
      setTimeout(() => loadLoglar(), 2000)
    } catch (err) {
      alert(getErrorMessage(err, 'Senkronizasyon başlatılamadı'))
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
      minute: '2-digit',
      second: '2-digit'
    })
  }

  const durumRenkleri = {
    'Başarılı': '#28a745',
    'Hata': '#dc3545',
    'Uyarı': '#ffc107'
  }

  if (loading) {
    return <div className="loading">Yükleniyor...</div>
  }

  return (
    <div className="loglar-container">
      <div className="loglar-header">
        <h1>Senkronizasyon Logları</h1>
        <div>
          <button onClick={handleSyncOrders} className="sync-button">
            🔄 Siparişleri Senkronize Et
          </button>
        </div>
      </div>

      {error && typeof error === 'string' && <div className="error-message">{error}</div>}

      <div className="filters">
        <div className="filter-group">
          <label>Pazaryeri:</label>
          <select value={filterPazaryeri} onChange={(e) => setFilterPazaryeri(e.target.value)}>
            <option value="">Tümü</option>
            {pazaryerleri.map((p) => (
              <option key={p.id} value={p.id}>{p.pazaryeriAdi}</option>
            ))}
          </select>
        </div>
        <div className="filter-group">
          <label>Durum:</label>
          <select value={filterDurum} onChange={(e) => setFilterDurum(e.target.value)}>
            <option value="">Tümü</option>
            <option value="Başarılı">Başarılı</option>
            <option value="Hata">Hata</option>
            <option value="Uyarı">Uyarı</option>
          </select>
        </div>
        <div className="filter-group">
          <label>İşlem Tipi:</label>
          <select value={filterIslemTipi} onChange={(e) => setFilterIslemTipi(e.target.value)}>
            <option value="">Tümü</option>
            <option value="Sipariş">Sipariş</option>
            <option value="Stok">Stok</option>
            <option value="Ürün">Ürün</option>
            <option value="Fiyat">Fiyat</option>
          </select>
        </div>
      </div>

      <div className="loglar-table-container">
        <table className="loglar-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>İşlem Tipi</th>
              <th>Pazaryeri</th>
              <th>Durum</th>
              <th>Mesaj</th>
              <th>Hata Detayı</th>
              <th>Tarih</th>
            </tr>
          </thead>
          <tbody>
            {loglar.length === 0 ? (
              <tr>
                <td colSpan="7" className="no-data">Henüz log kaydı yok</td>
              </tr>
            ) : (
              loglar.map((log) => (
                <tr key={log.id}>
                  <td>{log.id}</td>
                  <td>{log.islemTipi}</td>
                  <td>{log.pazaryeriAdi || '-'}</td>
                  <td>
                    <span className="durum-badge" style={{ backgroundColor: durumRenkleri[log.durum] || '#6c757d', color: 'white' }}>
                      {log.durum}
                    </span>
                  </td>
                  <td>{log.mesaj || '-'}</td>
                  <td>
                    {log.hataDetay ? (
                      <details>
                        <summary style={{ cursor: 'pointer', color: '#dc3545' }}>Hata Detayı</summary>
                        <pre style={{ marginTop: '5px', fontSize: '11px', whiteSpace: 'pre-wrap' }}>{log.hataDetay}</pre>
                      </details>
                    ) : '-'}
                  </td>
                  <td>{formatDate(log.tarih)}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default SenkronizasyonLoglari

