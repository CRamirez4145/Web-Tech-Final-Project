<script setup>
import { computed, onMounted, ref } from 'vue'

import {
  fallbackMessage,
  getErrorMessage,
  getSharedUsers,
  getStudentPeerEvaluationReports,
} from '@/api/appApi'
import AppEmptyState from '@/components/AppEmptyState.vue'
import CardContainer from '@/components/CardContainer.vue'
import PageHeader from '@/components/PageHeader.vue'
import SectionCard from '@/components/SectionCard.vue'

const students = ref([])
const selectedStudentId = ref(null)
const reports = ref([])
const loading = ref(false)
const errorMessage = ref('')

const selectedStudentName = computed(
  () => students.value.find((student) => student.id === selectedStudentId.value)?.fullName || 'Selected student',
)

const loadStudents = async () => {
  try {
    students.value = await getSharedUsers({ role: 'STUDENT' })
  } catch (error) {
    errorMessage.value = getErrorMessage(error, fallbackMessage)
  }
}

const loadReports = async () => {
  if (!selectedStudentId.value) {
    return
  }
  loading.value = true
  errorMessage.value = ''
  try {
    reports.value = await getStudentPeerEvaluationReports(selectedStudentId.value)
  } catch (error) {
    errorMessage.value = getErrorMessage(error, fallbackMessage)
  } finally {
    loading.value = false
  }
}

onMounted(loadStudents)
</script>

<template>
  <v-container class="app-page">
    <PageHeader
      eyebrow="Instructor"
      title="Student Peer Evaluation Reports"
      subtitle="Choose a student to inspect the peer evaluations they have received."
      class="mb-8"
    />

    <SectionCard
      title="Report Filters"
      description="Select a student to review every peer evaluation submitted about them."
      eyebrow="Instructor Reports"
      icon="mdi-filter-outline"
      class="mb-6"
    >
      <template #actions>
        <v-chip color="secondary" variant="flat">
          {{ reports.length }} evaluation{{ reports.length === 1 ? '' : 's' }}
        </v-chip>
      </template>

      <v-row align="center">
        <v-col cols="12" md="8">
          <v-select
            v-model="selectedStudentId"
            :items="students"
            item-title="fullName"
            item-value="id"
            label="Student"
            prepend-inner-icon="mdi-account-search-outline"
          />
        </v-col>
        <v-col cols="12" md="4" class="d-flex justify-md-end">
          <v-btn color="primary" prepend-icon="mdi-magnify" :loading="loading" :disabled="!selectedStudentId" @click="loadReports">
            Load Reports
          </v-btn>
        </v-col>
      </v-row>
    </SectionCard>

    <v-alert v-if="errorMessage" type="error" class="mb-4">{{ errorMessage }}</v-alert>

    <AppEmptyState
      v-if="!selectedStudentId"
      title="Choose a student to begin"
      description="Peer evaluation reports for the selected student will appear here."
      icon="mdi-account-search-outline"
      class="mb-6"
    />

    <AppEmptyState
      v-else-if="!loading && reports.length === 0"
      title="No peer evaluations found"
      :description="`There are no peer evaluations recorded for ${selectedStudentName}.`"
      icon="mdi-account-voice-outline"
      class="mb-6"
    />

    <v-row v-else class="mt-1">
      <v-col v-for="report in reports" :key="report.id" cols="12">
        <CardContainer class="report-card interactive-card">
          <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-4">
            <div>
              <div class="text-h6 font-weight-bold">
                {{ report.evaluatorName }} -> {{ report.evaluateeName }}
              </div>
              <div class="text-body-2 text-medium-emphasis">
                {{ report.teamName }} | {{ report.rubricName }}
              </div>
            </div>
            <v-chip color="secondary" size="small">Week {{ report.weekNumber }}</v-chip>
          </div>
          <div class="text-body-2 font-weight-medium mb-3">Criterion Scores</div>
          <div class="report-table">
            <v-table>
              <thead>
                <tr>
                  <th>Criterion</th>
                  <th>Score</th>
                  <th>Public Comment</th>
                  <th>Private Comment</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="score in report.criterionScores" :key="score.id">
                  <td>{{ score.criterionName }}</td>
                  <td>{{ score.score }}</td>
                  <td>{{ score.publicComment || 'None' }}</td>
                  <td>{{ score.privateComment || 'None' }}</td>
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
