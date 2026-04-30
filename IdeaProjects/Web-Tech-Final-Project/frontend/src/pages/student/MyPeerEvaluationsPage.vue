<script setup>
import { onMounted, ref } from 'vue'

import {
  fallbackMessage,
  getErrorMessage,
  getReceivedPeerEvaluations,
  getSubmittedPeerEvaluations,
} from '@/api/appApi'
import AppEmptyState from '@/components/AppEmptyState.vue'
import CardContainer from '@/components/CardContainer.vue'
import PageHeader from '@/components/PageHeader.vue'

const activeTab = ref('submitted')
const loading = ref(false)
const errorMessage = ref('')
const submittedEvaluations = ref([])
const receivedEvaluations = ref([])

const loadEvaluations = async () => {
  loading.value = true
  errorMessage.value = ''
  try {
    const [submitted, received] = await Promise.all([
      getSubmittedPeerEvaluations(),
      getReceivedPeerEvaluations(),
    ])
    submittedEvaluations.value = submitted
    receivedEvaluations.value = received
  } catch (error) {
    errorMessage.value = getErrorMessage(error, fallbackMessage)
  } finally {
    loading.value = false
  }
}

onMounted(loadEvaluations)
</script>

<template>
  <v-container class="app-page">
    <div class="page-toolbar">
      <PageHeader
        eyebrow="Student"
        title="My Peer Evaluations"
        subtitle="Switch between the evaluations you submitted and the feedback you received."
      />
      <div class="action-cluster">
        <v-chip color="secondary" variant="flat" prepend-icon="mdi-swap-horizontal">
          {{ activeTab === 'submitted' ? submittedEvaluations.length : receivedEvaluations.length }}
          {{ activeTab === 'submitted' ? 'submitted' : 'received' }}
        </v-chip>
        <v-btn color="primary" variant="outlined" prepend-icon="mdi-refresh" :loading="loading" @click="loadEvaluations">
          Refresh
        </v-btn>
      </div>
    </div>

    <v-alert v-if="errorMessage" type="error" class="mb-4">{{ errorMessage }}</v-alert>

    <v-tabs v-model="activeTab" class="mb-4 evaluation-tabs">
      <v-tab value="submitted">Submitted</v-tab>
      <v-tab value="received">Received</v-tab>
    </v-tabs>

    <v-window v-model="activeTab">
      <v-window-item value="submitted">
        <AppEmptyState
          v-if="!loading && submittedEvaluations.length === 0"
          title="No submitted evaluations"
          description="Your submitted peer evaluations will appear here."
          icon="mdi-account-edit-outline"
        />
        <v-row v-else>
          <v-col v-for="evaluation in submittedEvaluations" :key="evaluation.id" cols="12">
            <CardContainer class="evaluation-card interactive-card">
              <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-4">
                <div>
                  <div class="text-h6 font-weight-bold">{{ evaluation.evaluateeName }}</div>
                  <div class="text-body-2 text-medium-emphasis">
                    {{ evaluation.teamName }} | {{ evaluation.rubricName }}
                  </div>
                </div>
                <v-chip color="secondary" size="small">Week {{ evaluation.weekNumber }}</v-chip>
              </div>
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
                    <tr v-for="score in evaluation.criterionScores" :key="score.id">
                      <td>{{ score.criterionName }}</td>
                      <td>{{ score.score }}</td>
                      <td>{{ score.publicComment || 'None' }}</td>
                      <td>{{ score.privateComment || 'None' }}</td>
                    </tr>
                  </tbody>
                </v-table>
            </CardContainer>
          </v-col>
        </v-row>
      </v-window-item>

      <v-window-item value="received">
        <AppEmptyState
          v-if="!loading && receivedEvaluations.length === 0"
          title="No received evaluations"
          description="Feedback from your teammates will appear here after they submit it."
          icon="mdi-account-voice-outline"
        />
        <v-row v-else>
          <v-col v-for="evaluation in receivedEvaluations" :key="evaluation.evaluationId" cols="12" md="6">
            <CardContainer class="evaluation-card interactive-card">
              <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-4">
                <div class="text-h6 font-weight-bold">{{ evaluation.evaluatorName }}</div>
                <v-chip color="secondary" size="small">Week {{ evaluation.weekNumber }}</v-chip>
              </div>
              <div class="text-body-1 mb-2"><strong>Average Score:</strong> {{ evaluation.averageScore }}</div>
              <div class="text-body-2 text-medium-emphasis">
                Submitted at {{ evaluation.submittedAt }}
              </div>
            </CardContainer>
          </v-col>
        </v-row>
      </v-window-item>
    </v-window>
  </v-container>
</template>

<style scoped>
.evaluation-tabs {
  background: rgba(255, 255, 255, 0.82);
  border-radius: 999px;
  padding: 6px;
  display: inline-flex;
}

.evaluation-card {
  background: linear-gradient(180deg, #ffffff, #fcfdff);
}
</style>
