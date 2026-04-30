<script setup>
import { onMounted, ref } from 'vue'

import { fallbackMessage, getErrorMessage, getMyWars } from '@/api/appApi'
import AppEmptyState from '@/components/AppEmptyState.vue'
import CardContainer from '@/components/CardContainer.vue'
import PageHeader from '@/components/PageHeader.vue'

const loading = ref(false)
const errorMessage = ref('')
const wars = ref([])

const loadWars = async () => {
  loading.value = true
  errorMessage.value = ''
  try {
    wars.value = await getMyWars()
  } catch (error) {
    errorMessage.value = getErrorMessage(error, fallbackMessage)
  } finally {
    loading.value = false
  }
}

onMounted(loadWars)
</script>

<template>
  <v-container class="app-page">
    <div class="page-toolbar">
      <PageHeader
        eyebrow="Student"
        title="My Weekly Activity Reports"
        subtitle="Review the WARs you have already submitted and inspect each activity entry."
      />
      <div class="action-cluster">
        <v-chip color="secondary" variant="flat" prepend-icon="mdi-file-document-outline">
          {{ wars.length }} report{{ wars.length === 1 ? '' : 's' }}
        </v-chip>
        <v-btn color="primary" variant="outlined" prepend-icon="mdi-refresh" :loading="loading" @click="loadWars">
          Refresh
        </v-btn>
      </div>
    </div>

    <v-alert v-if="errorMessage" type="error" class="mb-4">{{ errorMessage }}</v-alert>

    <AppEmptyState
      v-if="!loading && wars.length === 0"
      title="No WARs submitted yet"
      description="Your weekly activity reports will appear here after you submit them."
      icon="mdi-file-document-outline"
    />

    <v-row v-else>
      <v-col v-for="war in wars" :key="war.id" cols="12">
        <CardContainer class="war-history-card interactive-card">
          <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-4">
            <div>
              <div class="text-h6 font-weight-bold">{{ war.studentName }} | Week {{ war.weekNumber }}</div>
              <div class="text-body-2 text-medium-emphasis">{{ war.sectionName }} | {{ war.teamName }}</div>
            </div>
            <v-chip color="secondary" variant="flat" size="small">{{ war.submittedAt }}</v-chip>
          </div>
          <div class="text-body-2 font-weight-medium mb-3">Activities Summary</div>
          <div>
            <v-table>
              <thead>
                <tr>
                  <th>Description</th>
                  <th>Hours</th>
                  <th>Category</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="activity in war.activities" :key="activity.id">
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
.war-history-card {
  background: linear-gradient(180deg, #ffffff, #fcfdff);
}
</style>
