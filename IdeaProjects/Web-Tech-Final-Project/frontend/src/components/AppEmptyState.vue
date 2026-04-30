<script setup>
defineProps({
  title: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    required: true,
  },
  icon: {
    type: String,
    default: 'mdi-information-outline',
  },
})
</script>

<template>
  <v-sheet class="empty-state pa-8 text-center" rounded="xl">
    <v-icon :icon="icon" size="48" color="primary" class="mb-3" />
    <div class="text-h6 mb-2">{{ title }}</div>
    <p class="text-body-2 text-medium-emphasis mb-0">{{ description }}</p>
  </v-sheet>
</template>

<style scoped>
.empty-state {
  background: linear-gradient(180deg, rgba(239, 246, 255, 0.72), rgba(255, 255, 255, 0.98));
  border: 1px solid rgba(15, 23, 42, 0.04);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}
</style>
