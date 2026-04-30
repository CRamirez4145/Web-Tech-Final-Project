import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

const storageKey = 'project-pulse-session'

const getStoredSession = () => {
  const rawValue = window.localStorage.getItem(storageKey)
  if (!rawValue) {
    return null
  }

  try {
    return JSON.parse(rawValue)
  } catch {
    window.localStorage.removeItem(storageKey)
    return null
  }
}

export const useSessionStore = defineStore('session', () => {
  const session = ref(getStoredSession())

  const role = computed(() => session.value?.role || null)
  const userId = computed(() => session.value?.userId || null)
  const userName = computed(() => session.value?.userName || '')
  const isLoggedIn = computed(() => !!role.value)

  const setSession = (value) => {
    session.value = value
    window.localStorage.setItem(storageKey, JSON.stringify(value))
  }

  const clearSession = () => {
    session.value = null
    window.localStorage.removeItem(storageKey)
  }

  return {
    clearSession,
    isLoggedIn,
    role,
    session,
    setSession,
    userId,
    userName,
  }
})

export { getStoredSession, storageKey }
