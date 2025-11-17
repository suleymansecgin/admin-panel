import { useState, useEffect } from 'react'
import { raporService } from '../services/raporService'
import { getErrorMessage } from '../utils/errorHandler'
import './Raporlar.css'

const Raporlar = () => {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [rapor, setRapor] = useState(null)
  const [baslangicTarihi, setBaslangicTarihi] = useState('')
  const [bitisTarihi, setBitisTarihi] = useState('')

  useEffect(() => {
    // Varsayılan olarak son 30 günü yükle
    const bitis = new Date()
    const baslangic = new Date()
    baslangic.setDate(baslangic.getDate() - 30)
    
    setBitisTarihi(bitis.toISOString().split('T')[0])
    setBaslangicTarihi(baslangic.toISOString().split('T')[0])
    loadRapor(baslangic.toISOString(), bitis.toISOString())
  }, [])

  const loadRapor = async (baslangic, bitis) => {
    try {
      setLoading(true)
      setError('')
      const data = await raporService.getKarZararRaporu(baslangic, bitis)
      setRapor(data)
    } catch (err) {
      setError(getErrorMessage(err, 'Rapor yüklenemedi'))
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    if (baslangicTarihi && bitisTarihi) {
      const baslangic = new Date(baslangicTarihi)
      const bitis = new Date(bitisTarihi)
      bitis.setHours(23, 59, 59, 999)
      loadRapor(baslangic.toISOString(), bitis.toISOString())
    }
  }

  const formatCurrency = (value) => {
    if (!value) return '0,00 ₺'
    return new Intl.NumberFormat('tr-TR', {
      style: 'currency',
      currency: 'TRY'
    }).format(value)
  }

  const formatPercent = (value) => {
    if (!value) return '0,00%'
    return `${parseFloat(value).toFixed(2)}%`
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
    return <div className="loading">Rapor yükleniyor...</div>
  }

  return (
    <div className="raporlar-container">
      <div className="raporlar-header">
        <h1>Kar-Zarar Raporu</h1>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="rapor-filtre">
        <form onSubmit={handleSubmit}>
          <div className="filtre-row">
            <div className="filtre-group">
              <label>Başlangıç Tarihi</label>
              <input
                type="date"
                value={baslangicTarihi}
                onChange={(e) => setBaslangicTarihi(e.target.value)}
                required
              />
            </div>
            <div className="filtre-group">
              <label>Bitiş Tarihi</label>
              <input
                type="date"
                value={bitisTarihi}
                onChange={(e) => setBitisTarihi(e.target.value)}
                required
              />
            </div>
            <div className="filtre-group">
              <button type="submit" className="filter-button">
                Raporu Getir
              </button>
            </div>
          </div>
        </form>
      </div>

      {rapor && (
        <div className="rapor-content">
          {/* Genel Özet */}
          <div className="rapor-section">
            <h2>Genel Özet</h2>
            <div className="ozet-grid">
              <div className="ozet-card">
                <div className="ozet-label">Toplam Satış Tutarı</div>
                <div className="ozet-value positive">{formatCurrency(rapor.toplamSatisTutari)}</div>
              </div>
              <div className="ozet-card">
                <div className="ozet-label">Toplam Maliyet</div>
                <div className="ozet-value negative">{formatCurrency(rapor.toplamMaliyet)}</div>
              </div>
              <div className="ozet-card">
                <div className="ozet-label">Toplam Kar</div>
                <div className={`ozet-value ${rapor.toplamKar >= 0 ? 'positive' : 'negative'}`}>
                  {formatCurrency(rapor.toplamKar)}
                </div>
              </div>
              <div className="ozet-card">
                <div className="ozet-label">Kar Marjı</div>
                <div className={`ozet-value ${rapor.karMarjiYuzdesi >= 0 ? 'positive' : 'negative'}`}>
                  {formatPercent(rapor.karMarjiYuzdesi)}
                </div>
              </div>
              <div className="ozet-card">
                <div className="ozet-label">Toplam Sipariş Sayısı</div>
                <div className="ozet-value">{rapor.toplamSiparisSayisi}</div>
              </div>
              <div className="ozet-card">
                <div className="ozet-label">Satılan Ürün Adedi</div>
                <div className="ozet-value">{rapor.toplamSatilanUrunAdedi}</div>
              </div>
            </div>
          </div>

          {/* Pazaryeri Bazında */}
          {rapor.pazaryeriBazinda && rapor.pazaryeriBazinda.length > 0 && (
            <div className="rapor-section">
              <h2>Pazaryeri Bazında Kar-Zarar</h2>
              <div className="table-container">
                <table className="rapor-table">
                  <thead>
                    <tr>
                      <th>Pazaryeri</th>
                      <th>Satış Tutarı</th>
                      <th>Maliyet</th>
                      <th>Kar</th>
                      <th>Kar Marjı</th>
                      <th>Sipariş Sayısı</th>
                    </tr>
                  </thead>
                  <tbody>
                    {rapor.pazaryeriBazinda.map((item, index) => (
                      <tr key={index}>
                        <td>{item.pazaryeriAdi}</td>
                        <td>{formatCurrency(item.satisTutari)}</td>
                        <td>{formatCurrency(item.maliyet)}</td>
                        <td className={item.kar >= 0 ? 'positive' : 'negative'}>
                          {formatCurrency(item.kar)}
                        </td>
                        <td className={item.karMarjiYuzdesi >= 0 ? 'positive' : 'negative'}>
                          {formatPercent(item.karMarjiYuzdesi)}
                        </td>
                        <td>{item.siparisSayisi}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {/* Ürün Bazında */}
          {rapor.urunBazinda && rapor.urunBazinda.length > 0 && (
            <div className="rapor-section">
              <h2>Ürün Bazında Kar-Zarar</h2>
              <div className="table-container">
                <table className="rapor-table">
                  <thead>
                    <tr>
                      <th>Ürün Kodu</th>
                      <th>Ürün Adı</th>
                      <th>Satış Tutarı</th>
                      <th>Maliyet</th>
                      <th>Kar</th>
                      <th>Kar Marjı</th>
                      <th>Satılan Miktar</th>
                    </tr>
                  </thead>
                  <tbody>
                    {rapor.urunBazinda.map((item, index) => (
                      <tr key={index}>
                        <td>{item.urunKodu}</td>
                        <td>{item.urunAdi}</td>
                        <td>{formatCurrency(item.satisTutari)}</td>
                        <td>{formatCurrency(item.maliyet)}</td>
                        <td className={item.kar >= 0 ? 'positive' : 'negative'}>
                          {formatCurrency(item.kar)}
                        </td>
                        <td className={item.karMarjiYuzdesi >= 0 ? 'positive' : 'negative'}>
                          {formatPercent(item.karMarjiYuzdesi)}
                        </td>
                        <td>{item.satilanMiktar}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {/* Tarih Bazında */}
          {rapor.tarihBazinda && rapor.tarihBazinda.length > 0 && (
            <div className="rapor-section">
              <h2>Tarih Bazında Kar-Zarar (Günlük)</h2>
              <div className="table-container">
                <table className="rapor-table">
                  <thead>
                    <tr>
                      <th>Tarih</th>
                      <th>Satış Tutarı</th>
                      <th>Maliyet</th>
                      <th>Kar</th>
                      <th>Sipariş Sayısı</th>
                    </tr>
                  </thead>
                  <tbody>
                    {rapor.tarihBazinda.map((item, index) => (
                      <tr key={index}>
                        <td>{formatDate(item.tarih)}</td>
                        <td>{formatCurrency(item.satisTutari)}</td>
                        <td>{formatCurrency(item.maliyet)}</td>
                        <td className={item.kar >= 0 ? 'positive' : 'negative'}>
                          {formatCurrency(item.kar)}
                        </td>
                        <td>{item.siparisSayisi}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}

export default Raporlar

