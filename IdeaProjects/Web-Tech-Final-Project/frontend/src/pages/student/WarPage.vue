<script setup>
import { computed, onMounted, ref } from 'vue'

import { createWar, fallbackMessage, getErrorMessage, getMyAccount, getSharedActiveWeeks } from '@/api/appApi'
import SectionCard from '@/components/SectionCard.vue'

const loading = ref(false)
const pageLoading = ref(false)
const errorMessage = ref('')
const profile = ref(null)
const activeWeeks = ref([])
const selectedWeekId = ref(null)
const snackbar = ref({
  show: false,
  color: 'success',
  message: '',
})

const categoryOptions = ['Development', 'Research', 'Meeting', 'Testing', 'Documentation', 'Other']

const createActivity = () => ({
  description: '',
  hoursSpent: null,
  category: '',
})

const activities = ref([createActivity()])

const activeWeekItems = computed(() =>
  activeWeeks.value
    .filter((week) => week.active)
    .sort((left, right) => right.weekNumber - left.weekNumber)
    .map((week) => ({
      id: week.id,
      name: `Week ${week.weekNumber}`,
      details: `${week.startDate} to ${week.endDate}`,
    })),
)

const defaultWeekId = computed(() => (activeWeekItems.value.length === 1 ? activeWeekItems.value[0].id : null))

const hasAssignedContext = computed(() => !!profile.value?.teamId && !!profile.value?.sectionId)

const showSnackbar = (message, color) => {
  snackbar.value = {
    show: true,
    color,
    message,
  }
}

const applyDefaultWeekSelection = () => {
  if (!activeWeekItems.value.some((week) => week.id === selectedWeekId.value)) {
    selectedWeekId.value = defaultWeekId.value
  }
}

const addActivity = () => {
  activities.value.push(createActivity())
}

const removeActivity = (index) => {
  if (activities.value.length === 1) {
    return
  }

  activities.value.splice(index, 1)
}

const resetForm = () => {
  activities.value = [createActivity()]
  selectedWeekId.value = defaultWeekId.value
}

const validateActivities = () => {
  for (const activity of activities.value) {
    if (!activity.description.trim()) {
      return 'Description is required for every activity.'
    }

    if (activity.hoursSpent === null || activity.hoursSpent === '' || Number(activity.hoursSpent) <= 0) {
      return 'Hours spent must be greater than 0 for every activity.'
    }

    if (!activity.category.trim()) {
      return 'Category is required for every activity.'
    }
  }

  return ''
}

const loadPageData = async () => {
  pageLoading.value = true
  errorMessage.value = ''

  try {
    const [account, weeks] = await Promise.all([getMyAccount(), getSharedActiveWeeks()])

    profile.value = account
    activeWeeks.value = weeks
    applyDefaultWeekSelection()
  } catch (error) {
    errorMessage.value = getErrorMessage(error, fallbackMessage)
  } finally {
    pageLoading.value = false
  }
}

const submitWar = async () => {
  if (!selectedWeekId.value) {
    showSnackbar('Select an active week before submitting.', 'error')
    return
  }

  if (!hasAssignedContext.value) {
    showSnackbar('You must be assigned to both a section and a team before submitting a report.', 'error')
    return
  }

  const activityError = validateActivities()
  if (activityError) {
    showSnackbar(activityError, 'error')
    return
  }

  loading.value = true

  try {
    await createWar({
      activeWeekId: selectedWeekId.value,
      activities: activities.value.map((activity) => ({
        description: activity.description.trim(),
        hoursSpent: Number(activity.hoursSpent),
        category: activity.category.trim(),
      })),
    })

    showSnackbar('Weekly Activity Report submitted successfully.', 'success')
    resetForm()
  } catch (error) {
    console.error('WAR submit failed', error)
    showSnackbar(getErrorMessage(error, fallbackMessage), 'error')
  } finally {
    loading.value = false
  }
}

onMounted(loadPageData)
</script>

<template>
  <v-container class="app-page">
    <v-row justify="center">
      <v-col cols="12" lg="10">
        <SectionCard
          eyebrow="Student Workspace"
          icon="mdi-clipboard-text-outline"
          title="Weekly Activity Report"
          description="Log your work for this week with clear descriptions, time spent, and activity categories."
          class="war-page-card"
        >
          <template #actions>
            <v-chip
              v-if="profile"
              color="secondary"
              variant="flat"
              prepend-icon="mdi-account-circle-outline"
            >
              {{ profile.firstName }} {{ profile.lastName }}
            </v-chip>
          </template>

          <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-6">
            <p class="text-body-1 text-medium-emphasis mb-0 war-intro">
              Select the active week, log each activity separately, and submit once every row is complete.
            </p>

            <v-chip color="secondary" variant="flat" prepend-icon="mdi-clipboard-list-outline">
              {{ activities.length }} activit{{ activities.length === 1 ? 'y' : 'ies' }}
            </v-chip>
          </div>

          <v-alert v-if="errorMessage" type="error" class="mb-4">
            {{ errorMessage }}
          </v-alert>

          <v-row class="mb-4">
            <v-col cols="12" md="4">
              <v-card variant="flat" color="secondary" rounded="xl" class="context-card h-100">
                <v-card-text class="pa-5">
                  <div class="text-caption text-medium-emphasis mb-1">Section</div>
                  <div class="text-subtitle-1 font-weight-medium">
                    {{ profile?.sectionName || 'Not assigned' }}
                  </div>
                </v-card-text>
              </v-card>
            </v-col>

            <v-col cols="12" md="4">
              <v-card variant="flat" color="secondary" rounded="xl" class="context-card h-100">
                <v-card-text class="pa-5">
                  <div class="text-caption text-medium-emphasis mb-1">Team</div>
                  <div class="text-subtitle-1 font-weight-medium">
                    {{ profile?.teamName || 'Not assigned' }}
                  </div>
                </v-card-text>
              </v-card>
            </v-col>

            <v-col cols="12" md="4">
              <v-select
                v-model="selectedWeekId"
                :items="activeWeekItems"
                item-title="name"
                item-value="id"
                label="Select Active Week"
                prepend-inner-icon="mdi-calendar-week"
                clearable
              >
                <template #item="{ props, item }">
                  <v-list-item
                    v-bind="props"
                    :title="item.raw.name"
                    :subtitle="item.raw.details"
                  />
                </template>
              </v-select>
            </v-col>
          </v-row>

          <v-alert
            v-if="!pageLoading && activeWeekItems.length === 0"
            type="warning"
            variant="tonal"
            class="mb-4"
          >
            No active week available.
          </v-alert>

          <v-alert
            v-if="!pageLoading && !hasAssignedContext"
            type="info"
            variant="tonal"
            class="mb-4"
          >
            Your section or team assignment is incomplete. Update your student profile before submitting a report.
          </v-alert>

          <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-4">
            <div>
              <h2 class="text-h6 font-weight-bold mb-1">Activities</h2>
              <p class="text-body-2 text-medium-emphasis mb-0">
                Add each task you worked on and the hours you spent.
              </p>
            </div>

            <div class="action-cluster">
              <v-btn
                color="primary"
                prepend-icon="mdi-plus"
                :disabled="loading"
                @click="addActivity"
              >
                Add Activity
              </v-btn>

              <v-btn
                color="primary"
                variant="outlined"
                prepend-icon="mdi-refresh"
                :disabled="loading"
                @click="resetForm"
              >
                Reset Form
              </v-btn>
            </div>
          </div>

          <v-row>
            <v-col
              v-for="(activity, index) in activities"
              :key="index"
              cols="12"
            >
              <v-card variant="outlined" rounded="xl" class="activity-card">
                <v-card-title class="d-flex align-center justify-space-between py-5 px-5">
                  <div class="d-flex align-center ga-2">
                    <v-icon color="primary" icon="mdi-clipboard-text-outline" />
                    <span class="font-weight-bold">Activity {{ index + 1 }}</span>
                  </div>

                  <v-btn
                    color="error"
                    variant="text"
                    size="small"
                    icon="mdi-delete-outline"
                    :disabled="activities.length === 1 || loading"
                    @click="removeActivity(index)"
                  />
                </v-card-title>

                <v-card-text class="px-5 pb-5">
                  <v-row>
                    <v-col cols="12">
                      <v-text-field
                        v-model="activity.description"
                        label="Description"
                        prepend-inner-icon="mdi-text-box-outline"
                        :disabled="loading"
                      />
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-text-field
                        v-model="activity.hoursSpent"
                        label="Hours Spent"
                        type="number"
                        min="0.01"
                        step="0.25"
                        prepend-inner-icon="mdi-timer-outline"
                        :disabled="loading"
                      />
                    </v-col>

                    <v-col cols="12" md="6">
                      <v-select
                        v-model="activity.category"
                        :items="categoryOptions"
                        label="Category"
                        prepend-inner-icon="mdi-shape-outline"
                        :disabled="loading"
                      />
                    </v-col>
                  </v-row>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>

          <div class="d-flex justify-space-between align-center flex-wrap ga-3 mt-6">
            <p class="text-body-2 text-medium-emphasis mb-0">
              Select a week and complete every activity before you submit the report.
            </p>

            <v-btn
              color="success"
              prepend-icon="mdi-check-circle-outline"
              :loading="loading"
              :disabled="loading"
              @click="submitWar"
            >
              Submit Weekly Report
            </v-btn>
          </div>
        </SectionCard>
      </v-col>
    </v-row>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="4000" location="top right">
      {{ snackbar.message }}
    </v-snackbar>
  </v-container>
</template>

<style scoped>
.war-page-card {
  background:
    radial-gradient(circle at top right, rgba(237, 244, 255, 0.95), transparent 28%),
    linear-gradient(180deg, #ffffff, #fcfcfc);
}

.war-intro {
  max-width: 700px;
  line-height: 1.7;
}

.context-card {
  border: 1px solid rgba(15, 23, 42, 0.04);
}

.activity-card {
  border-color: rgba(15, 23, 42, 0.06);
  background: #ffffff;
  box-shadow: 0 6px 20px rgba(15, 23, 42, 0.05);
}
</style>
