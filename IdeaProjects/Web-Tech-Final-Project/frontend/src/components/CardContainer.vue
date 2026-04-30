<script setup>
defineProps({
  padded: {
    type: Boolean,
    default: true,
  },
})
</script>

<template>
  <v-card class="card-container" rounded="xl" elevation="0">
    <v-card-text :class="padded ? 'pa-6 pa-md-8' : ''">
      <slot />
    </v-card-text>
  </v-card>
</template>

<style scoped>
.card-container {
  background:
    radial-gradient(circle at top right, rgba(248, 250, 252, 0.94), transparent 32%),
    linear-gradient(180deg, #ffffff, #fcfdff);
  border-radius: 20px;
  box-shadow: 0 12px 32px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(15, 23, 42, 0.04);
}
</style>
