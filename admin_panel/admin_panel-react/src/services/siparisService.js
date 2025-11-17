import api from './api'

export const siparisService = {
  getAllSiparisler: async () => {
    const response = await api.get('/siparisler')
    return response.data
  },

  getSiparisById: async (id) => {
    const response = await api.get(`/siparisler/${id}`)
    return response.data
  },

  getSiparislerByDurum: async (durum) => {
    const response = await api.get(`/siparisler/durum/${durum}`)
    return response.data
  },

  getSiparislerByPazaryeri: async (pazaryeriId) => {
    const response = await api.get(`/siparisler/pazaryeri/${pazaryeriId}`)
    return response.data
  },

  createSiparis: async (data) => {
    const response = await api.post('/siparisler', data)
    return response.data
  },

  updateSiparisDurum: async (id, data) => {
    const response = await api.put(`/siparisler/${id}/durum`, data)
    return response.data
  },
}

