import axios from 'axios'

const sessionStorageKey = 'project-pulse-session'

const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
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
