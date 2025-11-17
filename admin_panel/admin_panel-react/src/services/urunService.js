import api from './api'

export const urunService = {
  getAllUrunler: async () => {
    const response = await api.get('/urunler')
    return response.data
  },

  getUrunById: async (id) => {
    const response = await api.get(`/urunler/${id}`)
    return response.data
  },

  createUrun: async (data) => {
    const response = await api.post('/urunler', data)
    return response.data
  },

  updateUrun: async (id, data) => {
    const response = await api.put(`/urunler/${id}`, data)
    return response.data
  },

  deleteUrun: async (id) => {
    const response = await api.delete(`/urunler/${id}`)
    return response.data
  },

  createEslesme: async (urunId, data) => {
    const response = await api.post(`/urunler/${urunId}/pazaryeri-eslesme`, data)
    return response.data
  },

  updateEslesme: async (id, data) => {
    const response = await api.put(`/urunler/eslesme/${id}`, data)
    return response.data
  },

  deleteEslesme: async (id) => {
    const response = await api.delete(`/urunler/eslesme/${id}`)
    return response.data
  },
}

