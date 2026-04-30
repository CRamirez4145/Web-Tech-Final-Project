<script setup>
import { onMounted, ref } from 'vue'

import apiClient from '@/api/axios'
import AppStatCard from '@/components/AppStatCard.vue'
import CardContainer from '@/components/CardContainer.vue'
import PageHeader from '@/components/PageHeader.vue'

const backendMessage = ref('Checking backend connection...')

const loadBackendMessage = async () => {
  try {
    const response = await apiClient.get('/')
    backendMessage.value =
      typeof response.data === 'string' ? response.data : JSON.stringify(response.data)
  } catch {
    backendMessage.value = 'Backend not connected'
  }
}

onMounted(loadBackendMessage)
</script>

<template>
  <v-container class="app-page">
    <v-row align="center" class="mb-8">
      <v-col cols="12" md="7">
        <PageHeader
          eyebrow="Senior Design Workflow"
          title="Project Pulse"
          subtitle="A polished demo workspace for weekly activity reports, peer evaluations, and instructor insights."
          class="mb-6"
        />

        <div class="d-flex flex-wrap ga-3 mb-6">
          <v-btn color="primary" prepend-icon="mdi-login" to="/login">Open Demo Login</v-btn>
          <v-btn variant="outlined" prepend-icon="mdi-account-switch-outline" to="/login">Choose Role</v-btn>
        </div>

        <v-row>
          <v-col cols="12" md="4">
            <AppStatCard
              title="Admin Setup"
              value="1"
              icon="mdi-shield-crown-outline"
              description="Configure rubrics, sections, users, teams, and active weeks."
            />
          </v-col>
          <v-col cols="12" md="4">
            <AppStatCard
              title="Student Flow"
              value="2"
              icon="mdi-account-school-outline"
              description="Submit weekly activity reports and peer evaluations."
            />
          </v-col>
          <v-col cols="12" md="4">
            <AppStatCard
              title="Instructor Review"
              value="3"
              icon="mdi-chart-line"
              description="Filter team and student reports across the course."
            />
          </v-col>
        </v-row>
      </v-col>

      <v-col cols="12" md="5">
        <CardContainer class="hero-panel interactive-card">
            <div class="text-overline text-primary mb-2">System Status</div>
            <div class="text-h5 font-weight-bold mb-3">Backend Connection</div>
            <p class="text-body-1 mb-6">{{ backendMessage }}</p>

            <v-timeline density="compact" side="end" truncate-line="both">
              <v-timeline-item dot-color="primary" size="small">
                <div class="font-weight-medium">Admin prepares the course</div>
                <div class="text-body-2 text-medium-emphasis">Rubrics, sections, teams, and users</div>
              </v-timeline-item>
              <v-timeline-item dot-color="success" size="small">
                <div class="font-weight-medium">Students submit progress</div>
                <div class="text-body-2 text-medium-emphasis">WARs and peer evaluations</div>
              </v-timeline-item>
              <v-timeline-item dot-color="accent" size="small">
                <div class="font-weight-medium">Instructors review outcomes</div>
                <div class="text-body-2 text-medium-emphasis">Team and student reports</div>
              </v-timeline-item>
            </v-timeline>
        </CardContainer>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.hero-panel {
  background:
    radial-gradient(circle at top right, rgba(219, 234, 254, 0.95), transparent 30%),
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(249, 250, 251, 0.98));
}
</style>
