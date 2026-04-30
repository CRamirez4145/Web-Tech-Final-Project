<script setup>
import CardContainer from '@/components/CardContainer.vue'

defineProps({
  title: {
    type: String,
    default: '',
  },
  description: {
    type: String,
    default: '',
  },
  padded: {
    type: Boolean,
    default: true,
  },
  eyebrow: {
    type: String,
    default: '',
  },
  icon: {
    type: String,
    default: '',
  },
})
</script>

<template>
  <CardContainer class="section-card" :padded="padded">
    <div v-if="title || description || $slots.actions" class="section-card-header mb-5">
      <div class="section-card-copy">
        <div v-if="icon || eyebrow" class="section-card-meta mb-3">
          <div v-if="icon" class="section-card-icon">
            <v-icon :icon="icon" size="20" color="primary" />
          </div>
          <div v-if="eyebrow" class="section-card-eyebrow">
            {{ eyebrow }}
          </div>
        </div>
        <div v-if="title" class="section-card-title mb-1">{{ title }}</div>
        <p v-if="description" class="section-card-description mb-0">{{ description }}</p>
      </div>
      <div v-if="$slots.actions" class="section-card-actions">
        <slot name="actions" />
      </div>
    </div>

    <div class="section-card-body">
      <slot />
    </div>
  </CardContainer>
</template>

<style scoped>
.section-card {
  position: relative;
}

.section-card::before {
  content: "";
  position: absolute;
  inset: 0 0 auto 0;
  height: 5px;
  background: linear-gradient(90deg, rgba(37, 99, 235, 0.86), rgba(125, 211, 252, 0.42));
}

.section-card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}

.section-card-copy {
  max-width: 720px;
}

.section-card-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.section-card-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 14px;
  background: linear-gradient(180deg, #eff6ff, #f8fbff);
  box-shadow: 0 10px 20px rgba(37, 99, 235, 0.12);
}

.section-card-eyebrow {
  color: #2563eb;
  font-size: 0.76rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.section-card-title {
  color: #0f172a;
  font-size: 1.08rem;
  font-weight: 700;
  line-height: 1.3;
}

.section-card-description {
  color: #64748b;
  font-size: 0.95rem;
  line-height: 1.65;
}

.section-card-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.section-card-body {
  position: relative;
}
</style>
