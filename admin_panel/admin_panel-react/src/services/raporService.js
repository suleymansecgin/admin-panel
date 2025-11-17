import api from './api'

export const raporService = {
  getKarZararRaporu: async (baslangicTarihi, bitisTarihi) => {
    const params = {}
    if (baslangicTarihi) {
      params.baslangicTarihi = baslangicTarihi
    }
    if (bitisTarihi) {
      params.bitisTarihi = bitisTarihi
    }
    const response = await api.get('/raporlar/kar-zarar', { params })
    return response.data
  }
}

