<script setup>
import { onMounted, reactive, ref } from 'vue'

import {
  createAdminActiveWeek,
  fallbackMessage,
  getAdminActiveWeeks,
  getErrorMessage,
  updateAdminActiveWeek,
} from '@/api/appApi'
import AppEmptyState from '@/components/AppEmptyState.vue'
import PageHeader from '@/components/PageHeader.vue'
import SectionCard from '@/components/SectionCard.vue'

const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const snackbar = ref({ show: false, color: 'success', message: '' })
const activeWeeks = ref([])

const form = reactive({
  id: null,
  weekNumber: '',
  startDate: '',
  endDate: '',
  active: false,
})

const showSnackbar = (message, color) => {
  snackbar.value = { show: true, color, message }
}

const isEditing = () => form.id !== null

const resetForm = () => {
  form.id = null
  form.weekNumber = ''
  form.startDate = ''
  form.endDate = ''
  form.active = false
}

const editWeek = (week) => {
  form.id = week.id
  form.weekNumber = week.weekNumber
  form.startDate = week.startDate
  form.endDate = week.endDate
  form.active = week.active
}

const loadActiveWeeks = async () => {
  loading.value = true
  errorMessage.value = ''
  try {
    activeWeeks.value = await getAdminActiveWeeks()
  } catch (error) {
    errorMessage.value = getErrorMessage(error, fallbackMessage)
  } finally {
    loading.value = false
  }
}

const saveWeek = async () => {
  if (!form.weekNumber || !form.startDate || !form.endDate) {
    showSnackbar('Week number, start date, and end date are required.', 'error')
    return
  }

  saving.value = true
  try {
    const payload = {
      weekNumber: Number(form.weekNumber),
      startDate: form.startDate,
      endDate: form.endDate,
      active: form.active,
    }

    if (isEditing()) {
      await updateAdminActiveWeek(form.id, payload)
      showSnackbar('Active week updated successfully.', 'success')
    } else {
      await createAdminActiveWeek(payload)
      showSnackbar('Active week created successfully.', 'success')
    }

    resetForm()
    await loadActiveWeeks()
  } catch (error) {
    showSnackbar(getErrorMessage(error, fallbackMessage), 'error')
  } finally {
    saving.value = false
  }
}

onMounted(loadActiveWeeks)
</script>

<template>
  <v-container class="app-page">
    <PageHeader
      eyebrow="Admin"
      title="Active Weeks"
      subtitle="Manage the reporting calendar and control which week students can submit against."
      class="mb-8"
    />

    <v-row>
      <v-col cols="12" md="4">
        <SectionCard
          :title="isEditing() ? 'Edit Active Week' : 'Create Active Week'"
          description="Keep the course timeline current so WAR and peer-evaluation submissions open on the correct week."
          eyebrow="Schedule"
          icon="mdi-calendar-week"
          class="h-100"
        >
          <v-text-field v-model="form.weekNumber" label="Week Number" type="number" class="mb-3" />
          <v-text-field v-model="form.startDate" label="Start Date" type="date" class="mb-3" />
          <v-text-field v-model="form.endDate" label="End Date" type="date" class="mb-4" />
          <v-switch v-model="form.active" label="Mark as active week" inset class="mb-4" />
          <div class="d-flex flex-wrap ga-3">
            <v-btn color="success" prepend-icon="mdi-content-save-outline" :loading="saving" @click="saveWeek">
              {{ isEditing() ? 'Update Week' : 'Create Week' }}
            </v-btn>
            <v-btn variant="outlined" prepend-icon="mdi-refresh" :disabled="saving" @click="resetForm">Reset Form</v-btn>
          </div>
        </SectionCard>
      </v-col>

      <v-col cols="12" md="8">
        <SectionCard
          title="Configured Weeks"
          description="Activate one reporting week at a time to keep student submissions aligned."
          eyebrow="Calendar"
          icon="mdi-calendar-clock-outline"
        >
          <template #actions>
            <v-btn color="primary" variant="outlined" prepend-icon="mdi-refresh" :loading="loading" @click="loadActiveWeeks">Refresh</v-btn>
          </template>

          <div>
            <v-alert v-if="errorMessage" type="error" class="mb-4">{{ errorMessage }}</v-alert>
            <AppEmptyState
              v-if="!loading && activeWeeks.length === 0"
              title="No weeks configured"
              description="Create a reporting week so students have a valid submission window."
              icon="mdi-calendar-plus"
            />
            <v-table v-else>
              <thead>
                <tr>
                  <th>Week</th>
                  <th>Start</th>
                  <th>End</th>
                  <th>Status</th>
                  <th class="text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="week in activeWeeks" :key="week.id">
                  <td>{{ week.weekNumber }}</td>
                  <td>{{ week.startDate }}</td>
                  <td>{{ week.endDate }}</td>
                  <td>
                    <v-chip :color="week.active ? 'success' : 'grey'" size="small">
                      {{ week.active ? 'Active' : 'Inactive' }}
                    </v-chip>
                  </td>
                  <td class="text-right">
                    <v-btn color="primary" size="small" variant="tonal" prepend-icon="mdi-pencil-outline" @click="editWeek(week)">Edit Week</v-btn>
                  </td>
                </tr>
              </tbody>
            </v-table>
          </div>
        </SectionCard>
      </v-col>
    </v-row>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="4000">
      {{ snackbar.message }}
    </v-snackbar>
  </v-container>
</template>
