import api from './api'

export const roleService = {
  getAllRoles: async () => {
    const response = await api.get('/roles')
    return response.data
  },

  getRoleById: async (id) => {
    const response = await api.get(`/roles/${id}`)
    return response.data
  },

  getRoleByCode: async (code) => {
    const response = await api.get(`/roles/code/${code}`)
    return response.data
  },

  createRole: async (roleData) => {
    const response = await api.post('/roles', roleData)
    return response.data
  },

  updateRole: async (id, roleData) => {
    const response = await api.put(`/roles/${id}`, roleData)
    return response.data
  },

  deleteRole: async (id) => {
    await api.delete(`/roles/${id}`)
  },

  getAllPermissions: async () => {
    const response = await api.get('/roles/permissions')
    return response.data
  },
}

