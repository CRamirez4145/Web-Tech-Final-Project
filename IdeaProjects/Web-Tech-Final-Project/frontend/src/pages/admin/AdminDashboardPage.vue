<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { fallbackMessage, getErrorMessage, getReferenceData } from '@/api/appApi'
import CardContainer from '@/components/CardContainer.vue'
import PageHeader from '@/components/PageHeader.vue'

const router = useRouter()
const loading = ref(false)
const errorMessage = ref('')
const summary = ref({
  sections: [],
  teams: [],
  users: [],
  rubrics: [],
  activeWeeks: [],
})

const managementCards = computed(() => [
  {
    title: 'Rubrics',
    value: summary.value.rubrics.length,
    icon: 'mdi-format-list-checks',
    description: 'Peer evaluation scoring structures available for students.',
    to: '/admin/rubrics',
    action: 'Manage Rubrics',
  },
  {
    title: 'Sections',
    value: summary.value.sections.length,
    icon: 'mdi-google-classroom',
    description: 'Senior design sections configured for the course.',
    to: '/admin/sections',
    action: 'Manage Sections',
  },
  {
    title: 'Teams',
    value: summary.value.teams.length,
    icon: 'mdi-account-group-outline',
    description: 'Student teams available for assignments and reporting.',
    to: '/admin/teams',
    action: 'Manage Teams',
  },
  {
    title: 'Users',
    value: summary.value.users.length,
    icon: 'mdi-account-multiple-outline',
    description: 'Admin, student, and instructor accounts in the demo.',
    to: '/admin/users',
    action: 'Manage Users',
  },
])

const activeWeekCount = computed(() => summary.value.activeWeeks.filter((week) => week.active).length)
const totalWeekCount = computed(() => summary.value.activeWeeks.length)

const loadSummary = async () => {
  loading.value = true
  errorMessage.value = ''
  try {
    summary.value = await getReferenceData()
  } catch (error) {
    errorMessage.value = getErrorMessage(error, fallbackMessage)
  } finally {
    loading.value = false
  }
}

onMounted(loadSummary)
</script>

<template>
  <v-container class="app-page">
    <div class="page-toolbar">
      <PageHeader
        eyebrow="Admin"
        title="Course Setup Dashboard"
        subtitle="Prepare the full Project Pulse workflow from rubric creation through team assignments."
      />
      <v-btn color="primary" variant="outlined" rounded="pill" prepend-icon="mdi-refresh" :loading="loading" @click="loadSummary">
        Refresh
      </v-btn>
    </div>

    <v-alert v-if="errorMessage" type="error" class="mb-4">{{ errorMessage }}</v-alert>

    <v-row class="mb-2">
      <v-col v-for="card in managementCards" :key="card.title" cols="12" md="6" lg="3">
        <CardContainer class="management-card h-100 interactive-card">
          <div class="management-accent mb-5">
            <v-avatar color="secondary" size="52" class="management-icon">
              <v-icon :icon="card.icon" color="primary" />
            </v-avatar>
          </div>
          <div class="text-h6 font-weight-bold mb-2">{{ card.title }}</div>
          <div class="management-count mb-2">{{ card.value }}</div>
          <p class="text-body-2 text-medium-emphasis mb-6">{{ card.description }}</p>
          <v-btn color="primary" variant="tonal" prepend-icon="mdi-arrow-right" block @click="router.push(card.to)">
            {{ card.action }}
          </v-btn>
        </CardContainer>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols="12" lg="8">
        <CardContainer class="h-100 dashboard-panel">
          <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-5">
            <div>
              <div class="text-h6 font-weight-bold mb-1">Recommended Setup Order</div>
              <p class="text-body-2 text-medium-emphasis mb-0">
                Follow this path to get from an empty workspace to a full end-to-end demo.
              </p>
            </div>
            <v-chip color="secondary" variant="flat">
              {{ activeWeekCount }} active of {{ totalWeekCount }} configured week{{ totalWeekCount === 1 ? '' : 's' }}
            </v-chip>
          </div>
          <div class="setup-grid">
            <v-timeline density="compact" side="end" truncate-line="both">
              <v-timeline-item dot-color="primary" size="small">
                <div class="font-weight-medium">Create a rubric</div>
                <div class="text-body-2 text-medium-emphasis">Required before students can submit peer evaluations.</div>
              </v-timeline-item>
              <v-timeline-item dot-color="primary" size="small">
                <div class="font-weight-medium">Create a section and active weeks</div>
                <div class="text-body-2 text-medium-emphasis">Set up the course and reporting schedule.</div>
              </v-timeline-item>
              <v-timeline-item dot-color="primary" size="small">
                <div class="font-weight-medium">Create users and teams</div>
                <div class="text-body-2 text-medium-emphasis">Add students and instructors to the workspace.</div>
              </v-timeline-item>
              <v-timeline-item dot-color="success" size="small">
                <div class="font-weight-medium">Assign members</div>
                <div class="text-body-2 text-medium-emphasis">Attach students and instructors to teams for the demo flow.</div>
              </v-timeline-item>
            </v-timeline>
            <div class="setup-actions">
              <div class="text-subtitle-1 font-weight-bold mb-2">Next Best Actions</div>
              <p class="text-body-2 text-medium-emphasis mb-4">
                Use these shortcuts to complete the setup sequence without jumping through the top nav.
              </p>
              <div class="d-flex flex-column ga-3">
                <v-btn color="primary" variant="tonal" prepend-icon="mdi-format-list-checks" block @click="router.push('/admin/rubrics')">
                  Create a Rubric
                </v-btn>
                <v-btn color="primary" variant="tonal" prepend-icon="mdi-google-classroom" block @click="router.push('/admin/sections')">
                  Add a Section
                </v-btn>
                <v-btn color="primary" variant="tonal" prepend-icon="mdi-calendar-week" block @click="router.push('/admin/active-weeks')">
                  Configure Active Weeks
                </v-btn>
              </div>
            </div>
          </div>
        </CardContainer>
      </v-col>

      <v-col cols="12" lg="4">
        <CardContainer class="h-100 quick-panel">
          <div class="text-h6 font-weight-bold mb-2">Workspace Snapshot</div>
          <p class="text-body-2 text-medium-emphasis mb-6">
            Keep an eye on the core records that drive the reporting flow.
          </p>
          <div class="snapshot-list">
            <div class="snapshot-item">
              <span class="snapshot-label">Configured Weeks</span>
              <span class="snapshot-value">{{ totalWeekCount }}</span>
            </div>
            <div class="snapshot-item">
              <span class="snapshot-label">Active Weeks</span>
              <span class="snapshot-value">{{ activeWeekCount }}</span>
            </div>
            <div class="snapshot-item">
              <span class="snapshot-label">Students + Instructors</span>
              <span class="snapshot-value">{{ summary.users.length }}</span>
            </div>
            <div class="snapshot-item">
              <span class="snapshot-label">Teams Ready</span>
              <span class="snapshot-value">{{ summary.teams.length }}</span>
            </div>
          </div>

          <div class="d-flex flex-column ga-3 mt-6">
            <v-btn block color="primary" prepend-icon="mdi-account-group-outline" @click="router.push('/admin/teams')">Open Team Management</v-btn>
            <v-btn block color="primary" variant="outlined" prepend-icon="mdi-account-multiple-outline" @click="router.push('/admin/users')">
              Open User Directory
            </v-btn>
          </div>
        </CardContainer>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.management-card {
  text-align: left;
}

.management-accent {
  display: inline-flex;
  padding: 10px;
  border-radius: 18px;
  background: linear-gradient(180deg, #eff6ff, #f8fbff);
}

.management-icon {
  box-shadow: 0 8px 18px rgba(37, 99, 235, 0.12);
}

.management-count {
  color: #0f172a;
  font-size: 2rem;
  font-weight: 700;
  line-height: 1;
}

.setup-grid {
  display: grid;
  gap: 24px;
}

.dashboard-panel {
  background: linear-gradient(180deg, #ffffff, #fcfdff);
}

.setup-actions {
  border-top: 1px solid rgba(15, 23, 42, 0.06);
  padding-top: 24px;
}

.quick-panel {
  background:
    radial-gradient(circle at top right, rgba(239, 246, 255, 0.85), transparent 30%),
    linear-gradient(180deg, #ffffff, #fcfcfc);
}

.snapshot-list {
  display: grid;
  gap: 14px;
}

.snapshot-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 16px;
  background: rgba(248, 250, 252, 0.9);
  border: 1px solid rgba(15, 23, 42, 0.04);
}

.snapshot-label {
  color: #6b7280;
  font-size: 0.95rem;
}

.snapshot-value {
  color: #111827;
  font-size: 1.125rem;
  font-weight: 700;
}

@media (min-width: 960px) {
  .setup-grid {
    grid-template-columns: minmax(0, 1.7fr) minmax(260px, 1fr);
    align-items: start;
  }

  .setup-actions {
    border-top: 0;
    border-left: 1px solid rgba(15, 23, 42, 0.06);
    padding-top: 0;
    padding-left: 24px;
  }
}
</style>
