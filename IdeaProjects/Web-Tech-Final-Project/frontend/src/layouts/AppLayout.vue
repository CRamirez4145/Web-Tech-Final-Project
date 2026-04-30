<script setup>
import NavBar from '@/components/NavBar.vue'

defineProps({
  role: {
    type: String,
    required: true,
  },
})
</script>

<template>
  <v-layout class="app-layout">
    <NavBar :role="role" />

    <v-main class="app-main">
      <div class="page-shell">
        <router-view v-slot="{ Component, route }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" :key="route.fullPath" />
          </transition>
        </router-view>
      </div>
    </v-main>
  </v-layout>
</template>

<style scoped>
.app-layout {
  min-height: 100vh;
  background: #f5f7fb;
}

.app-main {
  min-height: 100vh;
  background: #f5f7fb;
}

.page-shell {
  width: 100%;
  max-width: 1280px;
  margin: 0 auto;
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition:
    opacity 0.18s ease,
    transform 0.18s ease;
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
