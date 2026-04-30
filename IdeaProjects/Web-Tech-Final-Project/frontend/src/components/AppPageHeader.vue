<script setup>
defineProps({
  title: {
    type: String,
    required: true,
  },
  subtitle: {
    type: String,
    default: '',
  },
  eyebrow: {
    type: String,
    default: '',
  },
})
</script>

<template>
  <div class="page-header">
    <div v-if="eyebrow" class="eyebrow mb-3">{{ eyebrow }}</div>
    <h1 class="title mb-2">{{ title }}</h1>
    <p v-if="subtitle" class="subtitle mb-0">{{ subtitle }}</p>
    <slot />
  </div>
</template>

<style scoped>
.page-header {
  max-width: 760px;
}

.eyebrow {
  color: #2563eb;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.title {
  color: #111827;
  font-size: clamp(2rem, 3vw, 2.75rem);
  font-weight: 700;
  line-height: 1.08;
}

.subtitle {
  color: #6b7280;
  font-size: 1rem;
  line-height: 1.6;
}
</style>
