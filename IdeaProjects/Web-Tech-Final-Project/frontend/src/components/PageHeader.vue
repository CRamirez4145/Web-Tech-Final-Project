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
  </div>
</template>

<style scoped>
.page-header {
  max-width: 760px;
}

.eyebrow {
  display: inline-flex;
  align-items: center;
  padding: 8px 14px;
  border-radius: 999px;
  color: #2563eb;
  background: rgba(239, 246, 255, 0.9);
  border: 1px solid rgba(37, 99, 235, 0.08);
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.title {
  color: #111827;
  font-size: clamp(2.1rem, 3vw, 2.95rem);
  font-weight: 800;
  line-height: 1.08;
  letter-spacing: -0.03em;
}

.subtitle {
  color: #64748b;
  font-size: 1.03rem;
  line-height: 1.7;
}
</style>
