<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { workspaceNavigation } from '@/config/workspaceNavigation'
import { useSessionStore } from '@/stores/session'

const props = defineProps({
  role: {
    type: String,
    required: true,
  },
})

const route = useRoute()
const router = useRouter()
const sessionStore = useSessionStore()

const navigationItems = computed(() => workspaceNavigation[props.role] || [])

const isActive = (targetPath) => {
  if (targetPath === `/${props.role}`) {
    return route.path === targetPath
  }

  return route.path === targetPath || route.path.startsWith(`${targetPath}/`)
}

const logout = async () => {
  sessionStore.clearSession()
  await router.push('/login')
}
</script>

<template>
  <v-app-bar class="nav-shell px-2 px-md-6" elevation="0" height="88">
    <div class="nav-inner d-flex align-center justify-space-between w-100">
      <router-link to="/" class="brand-link">
        <div class="d-flex align-center ga-3">
          <v-avatar color="primary" size="46" class="brand-avatar">
            <v-icon color="white" icon="mdi-waveform" />
          </v-avatar>
          <div>
            <div class="brand-title">Project Pulse</div>
            <div class="brand-subtitle text-capitalize">{{ role }} workspace</div>
          </div>
        </div>
      </router-link>

      <div class="nav-links d-none d-lg-flex align-center ga-2">
        <router-link
          v-for="item in navigationItems"
          :key="item.to"
          :to="item.to"
          class="nav-link"
          :class="{ 'nav-link-active': isActive(item.to) }"
        >
          <v-icon :icon="item.icon" size="18" class="mr-2" />
          <span>{{ item.shortTitle || item.title }}</span>
        </router-link>
      </div>

      <div class="nav-actions d-flex align-center ga-3">
        <v-menu class="d-lg-none">
          <template #activator="{ props: menuProps }">
            <v-btn
              v-bind="menuProps"
              icon="mdi-menu"
              variant="text"
              class="mobile-menu-btn"
            />
          </template>
          <v-list rounded="xl" class="pa-2">
            <v-list-item
              v-for="item in navigationItems"
              :key="item.to"
              :to="item.to"
              :active="isActive(item.to)"
              rounded="lg"
              link
            >
              <template #prepend>
                <v-icon :icon="item.icon" />
              </template>
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-chip color="secondary" variant="flat" size="small" class="user-chip d-none d-sm-inline-flex">
          {{ sessionStore.userName || 'Demo User' }}
        </v-chip>
        <v-btn color="primary" variant="elevated" rounded="pill" size="large" prepend-icon="mdi-logout" @click="logout">
          Logout
        </v-btn>
      </div>
    </div>
  </v-app-bar>
</template>

<style scoped>
.nav-shell {
  position: sticky;
  top: 0;
  z-index: 20;
  background: rgba(255, 255, 255, 0.97);
  backdrop-filter: blur(18px);
  border-bottom: 1px solid rgba(15, 23, 42, 0.06);
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
}

.nav-inner {
  max-width: 1280px;
  margin: 0 auto;
}

.brand-link {
  text-decoration: none;
  color: inherit;
  min-width: 176px;
}

.brand-title {
  color: #111827;
  font-size: 1rem;
  font-weight: 700;
  line-height: 1.1;
  white-space: nowrap;
}

.brand-subtitle {
  color: #64748b;
  font-size: 0.78rem;
  font-weight: 600;
  letter-spacing: 0.04em;
  white-space: nowrap;
}

.brand-avatar {
  box-shadow: 0 10px 22px rgba(37, 99, 235, 0.24);
}

.nav-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  color: #475569;
  padding: 12px 18px;
  border-radius: 999px;
  white-space: nowrap;
  font-weight: 600;
  transition:
    transform 0.18s ease,
    background-color 0.18s ease,
    color 0.18s ease,
    box-shadow 0.18s ease;
}

.nav-link:hover {
  background: #f8fafc;
  color: #111827;
  transform: translateY(-1px);
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.05);
}

.nav-link-active {
  background: #eff6ff;
  color: #2563eb;
  box-shadow: inset 0 0 0 1px rgba(37, 99, 235, 0.12);
}

.mobile-menu-btn {
  min-width: 48px;
  min-height: 48px;
}

.user-chip {
  min-height: 38px;
  font-weight: 600;
}

@media (max-width: 1279px) {
  .nav-inner {
    gap: 12px;
  }

  .nav-actions {
    margin-left: auto;
  }
}

@media (max-width: 599px) {
  .brand-title {
    font-size: 0.95rem;
  }

  .brand-subtitle {
    font-size: 0.72rem;
  }
}
</style>
