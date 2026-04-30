<script setup>
import { computed, onMounted, ref } from 'vue'

import {
  fallbackMessage,
  getErrorMessage,
  getSharedTeams,
  getTeamWarReports,
} from '@/api/appApi'
import AppEmptyState from '@/components/AppEmptyState.vue'
import CardContainer from '@/components/CardContainer.vue'
import PageHeader from '@/components/PageHeader.vue'
import SectionCard from '@/components/SectionCard.vue'

const teams = ref([])
const selectedTeamId = ref(null)
const reports = ref([])
const loading = ref(false)
const errorMessage = ref('')

const selectedTeamName = computed(
  () => teams.value.find((team) => team.id === selectedTeamId.value)?.name || 'Selected team',
)

const loadTeams = async () => {
  try {
    teams.value = await getSharedTeams()
  } catch (error) {
    errorMessage.value = getErrorMessage(error, fallbackMessage)
  }
}

const loadReports = async () => {
  if (!selectedTeamId.value) {
    return
  }
  loading.value = true
  errorMessage.value = ''
  try {
    reports.value = await getTeamWarReports(selectedTeamId.value)
  } catch (error) {
    errorMessage.value = getErrorMessage(error, fallbackMessage)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadTeams()
})
</script>

<template>
  <v-container class="app-page">
    <PageHeader
      eyebrow="Instructor"
      title="Team WAR Reports"
      subtitle="Choose a team to review all weekly activity reports submitted by its members."
      class="mb-8"
    />

    <SectionCard
      title="Report Filters"
      description="Select a team, then load the full WAR history for everyone assigned to it."
      eyebrow="Instructor Reports"
      icon="mdi-filter-outline"
      class="mb-6"
    >
      <template #actions>
        <v-chip color="secondary" variant="flat">
          {{ reports.length }} report{{ reports.length === 1 ? '' : 's' }}
        </v-chip>
      </template>

      <v-row align="center">
        <v-col cols="12" md="8">
          <v-select
            v-model="selectedTeamId"
            :items="teams"
            item-title="name"
            item-value="id"
            label="Team"
            prepend-inner-icon="mdi-account-group-outline"
          />
        </v-col>
        <v-col cols="12" md="4" class="d-flex justify-md-end">
          <v-btn color="primary" prepend-icon="mdi-magnify" :loading="loading" :disabled="!selectedTeamId" @click="loadReports">
            Load Reports
          </v-btn>
        </v-col>
      </v-row>
    </SectionCard>

    <v-alert v-if="errorMessage" type="error" class="mb-4">{{ errorMessage }}</v-alert>

    <AppEmptyState
      v-if="!selectedTeamId"
      title="Choose a team to begin"
      description="The selected team's WAR history will appear here once you load it."
      icon="mdi-account-group-outline"
      class="mb-6"
    />

    <AppEmptyState
      v-else-if="!loading && reports.length === 0"
      title="No WARs found for this team"
      :description="`There are no submitted weekly activity reports for ${selectedTeamName}.`"
      icon="mdi-file-document-outline"
      class="mb-6"
    />

    <v-row v-else class="mt-1">
      <v-col v-for="report in reports" :key="report.id" cols="12">
        <CardContainer class="report-card interactive-card">
          <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-4">
            <div>
              <div class="text-h6 font-weight-bold">{{ report.studentName }}</div>
              <div class="text-body-2 text-medium-emphasis">
                {{ report.sectionName }} | {{ report.teamName }}
              </div>
            </div>
            <div class="d-flex flex-wrap ga-2">
              <v-chip color="secondary" size="small">Week {{ report.weekNumber }}</v-chip>
              <v-chip color="secondary" size="small">{{ report.submittedAt }}</v-chip>
            </div>
          </div>

          <div class="text-body-2 font-weight-medium mb-3">Activity Log</div>
          <div class="report-table">
            <v-table>
              <thead>
                <tr>
                  <th>Description</th>
                  <th>Hours</th>
                  <th>Category</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="activity in report.activities" :key="activity.id">
                  <td>{{ activity.description }}</td>
                  <td>{{ activity.hoursSpent }}</td>
                  <td>{{ activity.category || 'N/A' }}</td>
                </tr>
              </tbody>
            </v-table>
          </div>
        </CardContainer>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.report-card {
  background: linear-gradient(180deg, #ffffff, #fcfdff);
}
</style>
