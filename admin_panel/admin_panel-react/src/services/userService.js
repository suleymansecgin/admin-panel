import api from './api'

export const userService = {
  getAllUsers: async () => {
    const response = await api.get('/users')
    return response.data
  },

  getUserById: async (id) => {
    const response = await api.get(`/users/${id}`)
    return response.data
  },

  assignRolesToUser: async (userId, roleIds) => {
    const response = await api.put(`/users/${userId}/roles`, { roleIds: Array.from(roleIds) })
    return response.data
  },

  updateUserStatus: async (userId, enabled) => {
    const response = await api.put(`/users/${userId}/status`, null, {
      params: { enabled }
    })
    return response.data
  },
}

