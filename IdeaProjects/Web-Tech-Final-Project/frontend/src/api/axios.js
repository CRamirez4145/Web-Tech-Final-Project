import axios from 'axios'

const sessionStorageKey = 'project-pulse-session'
const apiBaseUrl = (import.meta.env.VITE_API_URL || '').replace(/\/$/, '')

const apiClient = axios.create({
  baseURL: apiBaseUrl,
})

apiClient.interceptors.request.use((config) => {
  const rawSession = window.localStorage.getItem(sessionStorageKey)
  if (!rawSession) {
    return config
  }

  try {
    const session = JSON.parse(rawSession)
    config.headers = config.headers || {}

    if (session.userId) {
      config.headers['X-User-Id'] = session.userId
    }

    if (session.role) {
      config.headers['X-User-Role'] = session.role
    }
  } catch {
    window.localStorage.removeItem(sessionStorageKey)
  }

  return config
})

export default apiClient
