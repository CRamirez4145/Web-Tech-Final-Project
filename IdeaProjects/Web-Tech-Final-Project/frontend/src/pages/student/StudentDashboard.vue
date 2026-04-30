<script setup>
import { onMounted, ref } from 'vue'

import {
  fallbackMessage,
  getErrorMessage,
  getMyAccount,
  getMyWars,
  getSubmittedPeerEvaluations,
} from '@/api/appApi'
import CardContainer from '@/components/CardContainer.vue'
import AppStatCard from '@/components/AppStatCard.vue'
import PageHeader from '@/components/PageHeader.vue'
import SectionCard from '@/components/SectionCard.vue'

const loading = ref(false)
const errorMessage = ref('')
const profile = ref(null)
const warCount = ref(0)
const peerEvaluationCount = ref(0)

const actions = [
  {
    title: 'Submit Weekly Activity Report',
    description: 'Log your current week’s work and time contribution.',
    to: '/student/wars/new',
  },
  {
    title: 'Submit Peer Evaluation',
    description: 'Score your teammate using the active rubric.',
    to: '/student/peer-evaluations/new',
  },
  {
    title: 'Review My History',
    description: 'See WAR submissions and peer feedback records.',
    to: '/student/wars',
  },
  {
    title: 'Update Profile',
    description: 'Keep your email, section, and team assignment current.',
    to: '/student/profile',
  },
]

const loadDashboard = async () => {
  loading.value = true
  errorMessage.value = ''
  try {
    const [account, wars, evaluations] = await Promise.all([
      getMyAccount(),
      getMyWars(),
      getSubmittedPeerEvaluations(),
    ])
    profile.value = account
    warCount.value = wars.length
    peerEvaluationCount.value = evaluations.length
  } catch (error) {
    errorMessage.value = getErrorMessage(error, fallbackMessage)
  } finally {
    loading.value = false
  }
}

onMounted(loadDashboard)
</script>

<template>
  <v-container class="app-page">
    <PageHeader
      eyebrow="Student"
      title="Student Dashboard"
      subtitle="Track your reporting activity, update your profile, and keep your weekly submissions on schedule."
      class="mb-6"
    />

    <v-alert v-if="errorMessage" type="error" class="mb-4">{{ errorMessage }}</v-alert>

    <v-row class="mb-6">
      <v-col cols="12" md="4">
        <SectionCard
          title="Profile Snapshot"
          description="Your current student profile details and reporting assignment."
          eyebrow="Quick View"
          icon="mdi-account-badge-outline"
          class="h-100 profile-card"
        >
          <div>
            <div v-if="profile">
              <div class="text-h6 font-weight-bold">{{ profile.firstName }} {{ profile.lastName }}</div>
              <div class="text-body-2 text-medium-emphasis mb-3">{{ profile.email }}</div>
              <v-chip color="secondary" class="mr-2 mb-2" size="small">{{ profile.sectionName }}</v-chip>
              <v-chip color="secondary" size="small">{{ profile.teamName }}</v-chip>
            </div>
            <div v-else class="text-body-2 text-medium-emphasis">
              Create your student account in the Profile page to unlock the full workflow.
            </div>
          </div>
        </SectionCard>
      </v-col>
      <v-col cols="12" md="4">
        <AppStatCard
          title="Submitted WARs"
          :value="warCount"
          icon="mdi-file-document-multiple-outline"
          description="Weekly activity reports already submitted."
        />
      </v-col>
      <v-col cols="12" md="4">
        <AppStatCard
          title="Peer Evaluations"
          :value="peerEvaluationCount"
          icon="mdi-account-star-outline"
          description="Peer evaluations you have completed."
        />
      </v-col>
    </v-row>

    <v-row>
      <v-col v-for="action in actions" :key="action.to" cols="12" md="6">
        <router-link :to="action.to" class="dashboard-link">
          <CardContainer class="h-100 action-card interactive-card">
            <div class="d-flex align-center justify-space-between mb-4">
              <div class="text-h6 font-weight-bold">{{ action.title }}</div>
              <v-icon icon="mdi-arrow-top-right" color="primary" />
            </div>
            <p class="text-body-1 text-medium-emphasis mb-6">{{ action.description }}</p>
            <v-btn color="primary" prepend-icon="mdi-arrow-right">Open</v-btn>
          </CardContainer>
        </router-link>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.dashboard-link {
  display: block;
  text-decoration: none;
  color: inherit;
}

.profile-card {
  background: linear-gradient(180deg, #ffffff, #fbfdff);
}
</style>
