import api from './api'

export const pazaryeriService = {
  getAllPazaryerleri: async () => {
    const response = await api.get('/pazaryerleri')
    return response.data
  },

  getAktifPazaryerleri: async () => {
    const response = await api.get('/pazaryerleri/aktif')
    return response.data
  },

  getPazaryeriById: async (id) => {
    const response = await api.get(`/pazaryerleri/${id}`)
    return response.data
  },

  createPazaryeri: async (data) => {
    const response = await api.post('/pazaryerleri', data)
    return response.data
  },

  updatePazaryeri: async (id, data) => {
    const response = await api.put(`/pazaryerleri/${id}`, data)
    return response.data
  },

  deletePazaryeri: async (id) => {
    const response = await api.delete(`/pazaryerleri/${id}`)
    return response.data
  },
}

