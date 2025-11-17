import api from './api'

export const senkronizasyonService = {
  syncOrdersFromAllPazaryerleri: async () => {
    const response = await api.post('/senkronizasyon/siparisler/tum')
    return response.data
  },

  syncOrdersFromPazaryeri: async (pazaryeriId) => {
    const response = await api.post(`/senkronizasyon/siparisler/${pazaryeriId}`)
    return response.data
  },

  syncStockToAllPazaryerleri: async (urunId) => {
    const response = await api.post(`/senkronizasyon/stok/${urunId}`)
    return response.data
  },

  getAllLogs: async () => {
    const response = await api.get('/senkronizasyon/loglar')
    return response.data
  },

  getLogsByPazaryeri: async (pazaryeriId) => {
    const response = await api.get(`/senkronizasyon/loglar/pazaryeri/${pazaryeriId}`)
    return response.data
  },

  getSonHatalar: async () => {
    const response = await api.get('/senkronizasyon/loglar/hatalar')
    return response.data
  },
}

