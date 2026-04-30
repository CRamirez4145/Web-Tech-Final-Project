<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDisplay } from 'vuetify'

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
const { mdAndUp } = useDisplay()

const drawer = ref(mdAndUp.value)

const navigationByRole = {
  admin: [
    { title: 'Dashboard', to: '/admin', icon: 'mdi-view-dashboard-outline' },
    { title: 'Rubrics', to: '/admin/rubrics', icon: 'mdi-format-list-checks' },
    { title: 'Sections', to: '/admin/sections', icon: 'mdi-google-classroom' },
    { title: 'Active Weeks', to: '/admin/active-weeks', icon: 'mdi-calendar-week' },
    { title: 'Teams', to: '/admin/teams', icon: 'mdi-account-group-outline' },
    { title: 'Users', to: '/admin/users', icon: 'mdi-account-cog-outline' },
  ],
  student: [
    { title: 'Dashboard', to: '/student', icon: 'mdi-view-dashboard-outline' },
    { title: 'Submit WAR', to: '/student/wars/new', icon: 'mdi-file-document-edit-outline' },
    { title: 'My WARs', to: '/student/wars', icon: 'mdi-file-document-multiple-outline' },
    {
      title: 'Submit Peer Evaluation',
      to: '/student/peer-evaluations/new',
      icon: 'mdi-account-star-outline',
    },
    {
      title: 'My Peer Evaluations',
      to: '/student/peer-evaluations',
      icon: 'mdi-chart-box-outline',
    },
    { title: 'Profile', to: '/student/profile', icon: 'mdi-account-circle-outline' },
  ],
  instructor: [
    { title: 'Dashboard', to: '/instructor', icon: 'mdi-view-dashboard-outline' },
    {
      title: 'Team WAR Reports',
      to: '/instructor/reports/team-wars',
      icon: 'mdi-file-chart-outline',
    },
    {
      title: 'Student WAR Reports',
      to: '/instructor/reports/student-wars',
      icon: 'mdi-account-details-outline',
    },
    {
      title: 'Section Peer Evaluations',
      to: '/instructor/reports/section-peer-evaluations',
      icon: 'mdi-chart-timeline-variant',
    },
    {
      title: 'Student Peer Evaluations',
      to: '/instructor/reports/student-peer-evaluations',
      icon: 'mdi-account-search-outline',
    },
  ],
}

const titleByRole = {
  admin: 'Admin Workspace',
  student: 'Student Workspace',
  instructor: 'Instructor Workspace',
}

const roleChipByRole = {
  admin: 'Admin',
  student: 'Student',
  instructor: 'Instructor',
}

const navigationItems = computed(() => navigationByRole[props.role] || [])

const logout = async () => {
  sessionStore.clearSession()
  await router.push('/login')
}
</script>

<template>
  <v-layout class="app-shell">
    <v-navigation-drawer v-model="drawer" :permanent="mdAndUp" width="280" class="app-drawer" border="0">
      <div class="drawer-header pa-5">
        <div class="d-flex align-center ga-3 mb-3">
          <v-avatar color="white" size="40">
            <v-icon color="primary" icon="mdi-waveform" />
          </v-avatar>
          <div>
            <div class="text-subtitle-1 font-weight-bold text-white">Project Pulse</div>
            <div class="text-caption text-blue-lighten-4">{{ titleByRole[role] }}</div>
          </div>
        </div>

        <v-chip color="white" variant="flat" size="small">{{ roleChipByRole[role] }}</v-chip>
      </div>

      <div class="px-3 py-4">
        <v-list nav class="bg-transparent">
          <v-list-item
            v-for="item in navigationItems"
            :key="item.to"
            :prepend-icon="item.icon"
            :title="item.title"
            :to="item.to"
            :active="route.path === item.to"
            rounded="lg"
            class="mb-1"
            link
          />
        </v-list>
      </div>

      <template #append>
        <div class="pa-3">
          <v-btn block variant="outlined" color="primary" to="/">Back Home</v-btn>
        </div>
      </template>
    </v-navigation-drawer>

    <v-app-bar class="app-bar" elevation="0">
      <template #prepend>
        <v-app-bar-nav-icon v-if="!mdAndUp" @click="drawer = !drawer" />
      </template>

      <v-app-bar-title class="font-weight-bold">Project Pulse</v-app-bar-title>

      <div class="d-flex align-center ga-3 mr-4">
        <v-chip color="secondary" size="small" variant="flat">
          {{ sessionStore.userName || titleByRole[role] }}
        </v-chip>
        <v-btn color="primary" variant="tonal" @click="logout">Logout</v-btn>
      </div>
    </v-app-bar>

    <v-main class="main-surface">
      <router-view />
    </v-main>
  </v-layout>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(214, 234, 255, 0.8), transparent 28%),
    linear-gradient(180deg, #f5f8fc, #edf3f8);
}

.app-drawer {
  background: linear-gradient(180deg, #0d47a1, #1565c0);
}

.drawer-header {
  border-bottom: 1px solid rgba(255, 255, 255, 0.14);
}

.app-bar {
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(13, 71, 161, 0.08);
}

.main-surface {
  min-height: 100vh;
}
</style>
