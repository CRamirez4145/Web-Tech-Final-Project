<script setup>
import { ref } from 'vue'
import { useDisplay } from 'vuetify'

const { mdAndUp } = useDisplay()
const drawer = ref(mdAndUp.value)

const navigationItems = [
  {
    title: 'Dashboard',
    to: '/student',
    icon: 'mdi-view-dashboard-outline',
  },
  {
    title: 'Submit WAR',
    to: '/student/wars/new',
    icon: 'mdi-file-document-edit-outline',
  },
  {
    title: 'My WARs',
    to: '/student/wars',
    icon: 'mdi-file-document-multiple-outline',
  },
  {
    title: 'Submit Peer Evaluation',
    to: '/student/peer-evaluations/new',
    icon: 'mdi-account-check-outline',
  },
  {
    title: 'My Peer Evaluations',
    to: '/student/peer-evaluations',
    icon: 'mdi-account-group-outline',
  },
  {
    title: 'Profile',
    to: '/student/profile',
    icon: 'mdi-account-circle-outline',
  },
]
</script>

<template>
  <v-layout class="min-h-screen">
    <v-navigation-drawer v-model="drawer" :permanent="mdAndUp" color="grey-lighten-4">
      <v-list nav>
        <v-list-subheader>Student Module</v-list-subheader>
        <v-list-item
          v-for="item in navigationItems"
          :key="item.to"
          :prepend-icon="item.icon"
          :title="item.title"
          :to="item.to"
          link
        />
      </v-list>
    </v-navigation-drawer>

    <v-app-bar color="primary">
      <template #prepend>
        <v-app-bar-nav-icon v-if="!mdAndUp" @click="drawer = !drawer" />
      </template>

      <v-app-bar-title>Project Pulse</v-app-bar-title>
    </v-app-bar>

    <v-main>
      <router-view />
    </v-main>
  </v-layout>
</template>
