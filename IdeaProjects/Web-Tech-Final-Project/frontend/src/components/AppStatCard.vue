<script setup>
defineProps({
  title: {
    type: String,
    required: true,
  },
  value: {
    type: [String, Number],
    required: true,
  },
  description: {
    type: String,
    default: '',
  },
  icon: {
    type: String,
    default: 'mdi-chart-box-outline',
  },
})
</script>

<template>
  <v-card class="stat-card h-100" rounded="xl">
    <v-card-text>
      <div class="d-flex align-start justify-space-between mb-4">
        <div>
          <div class="text-overline text-medium-emphasis">{{ title }}</div>
          <div class="text-h4 font-weight-bold">{{ value }}</div>
        </div>
        <v-avatar color="secondary" size="44">
          <v-icon :icon="icon" />
        </v-avatar>
      </div>
      <p v-if="description" class="text-body-2 text-medium-emphasis mb-0">{{ description }}</p>
    </v-card-text>
  </v-card>
</template>

<style scoped>
.stat-card {
  background:
    radial-gradient(circle at top right, rgba(219, 234, 254, 0.95), transparent 34%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(249, 250, 251, 0.98));
  border: 1px solid rgba(15, 23, 42, 0.04);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}
</style>
