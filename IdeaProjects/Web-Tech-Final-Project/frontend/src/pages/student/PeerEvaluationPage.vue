<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'

import {
  createPeerEvaluation,
  fallbackMessage,
  getErrorMessage,
  getMyAccount,
  getSharedActiveWeeks,
  getSharedRubrics,
  getSharedUsers,
} from '@/api/appApi'
import AppEmptyState from '@/components/AppEmptyState.vue'
import PageHeader from '@/components/PageHeader.vue'
import SectionCard from '@/components/SectionCard.vue'
import { useSessionStore } from '@/stores/session'

const sessionStore = useSessionStore()
const loading = ref(false)
const pageLoading = ref(false)
const errorMessage = ref('')
const snackbar = ref({ show: false, color: 'success', message: '' })

const profile = ref(null)
const teammates = ref([])
const rubrics = ref([])
const activeWeeks = ref([])

const form = reactive({
  evaluatorId: null,
  evaluateeId: null,
  teamId: null,
  sectionId: null,
  activeWeekId: null,
  rubricId: null,
  criterionScores: [],
})

const availableWeeks = computed(() => {
  const currentActive = activeWeeks.value.find((week) => week.active)
  if (!currentActive) {
    return []
  }
  return activeWeeks.value
    .filter((week) => week.weekNumber === currentActive.weekNumber - 1)
    .sort((left, right) => right.weekNumber - left.weekNumber)
    .map((week) => ({
      ...week,
      title: `Week ${week.weekNumber}`,
      details: `${week.startDate} to ${week.endDate}`,
    }))
})

const selectedRubric = computed(
  () => rubrics.value.find((rubric) => rubric.id === form.rubricId) || null,
)
const defaultEvaluationWeekId = computed(() => (availableWeeks.value.length === 1 ? availableWeeks.value[0].id : null))
const defaultRubricId = computed(() => (rubrics.value.length === 1 ? rubrics.value[0].id : rubrics.value[0]?.id || null))

watch(selectedRubric, (rubric) => {
  form.criterionScores = (rubric?.criteria || []).map((criterion) => ({
    criterionId: criterion.id,
    criterionName: criterion.name,
    score: null,
    publicComment: '',
    privateComment: '',
  }))
})

const showSnackbar = (message, color) => {
  snackbar.value = { show: true, color, message }
}

const applyDefaultSelections = () => {
  if (!availableWeeks.value.some((week) => week.id === form.activeWeekId)) {
    form.activeWeekId = defaultEvaluationWeekId.value
  }

  if (!rubrics.value.some((rubric) => rubric.id === form.rubricId)) {
    form.rubricId = defaultRubricId.value
  }
}

const resetEvaluationForm = () => {
  form.evaluateeId = null
  form.activeWeekId = defaultEvaluationWeekId.value
  form.rubricId = defaultRubricId.value
  form.criterionScores = form.criterionScores.map((score) => ({
    ...score,
    score: null,
    publicComment: '',
    privateComment: '',
  }))
}

const loadPageData = async () => {
  pageLoading.value = true
  errorMessage.value = ''

  try {
    const [account, allUsers, rubricData, weekData] = await Promise.all([
      getMyAccount(),
      getSharedUsers({ role: 'STUDENT' }),
      getSharedRubrics(),
      getSharedActiveWeeks(),
    ])

    profile.value = account
    rubrics.value = [...rubricData].sort((left, right) => right.id - left.id)
    activeWeeks.value = weekData
    teammates.value = allUsers.filter(
      (user) => user.teamId === account.teamId && user.id !== sessionStore.userId,
    )

    form.evaluatorId = sessionStore.userId
    form.teamId = account.teamId
    form.sectionId = account.sectionId
    applyDefaultSelections()
    form.evaluateeId = null
  } catch (error) {
    errorMessage.value = getErrorMessage(error, fallbackMessage)
  } finally {
    pageLoading.value = false
  }
}

const submitEvaluation = async () => {
  if (
    !form.evaluatorId ||
    !form.evaluateeId ||
    !form.teamId ||
    !form.sectionId ||
    !form.activeWeekId ||
    !form.rubricId
  ) {
    showSnackbar('Complete all peer evaluation context fields before submitting.', 'error')
    return
  }

  if (form.evaluatorId === form.evaluateeId) {
    showSnackbar('Evaluator and evaluatee cannot be the same student.', 'error')
    return
  }

  if (form.criterionScores.length === 0) {
    showSnackbar('Select a rubric with at least one criterion before submitting.', 'error')
    return
  }

  const invalidScore = form.criterionScores.some(
    (score) => score.score === null || Number(score.score) < 0,
  )
  if (invalidScore) {
    showSnackbar('Every criterion needs a score of 0 or higher.', 'error')
    return
  }

  loading.value = true
  try {
    await createPeerEvaluation({
      evaluateeId: form.evaluateeId,
      activeWeekId: form.activeWeekId,
      rubricId: form.rubricId,
      criterionScores: form.criterionScores.map((score) => ({
        criterionId: score.criterionId,
        score: Number(score.score),
        publicComment: score.publicComment,
        privateComment: score.privateComment,
      })),
    })

    resetEvaluationForm()
    showSnackbar('Peer evaluation submitted successfully.', 'success')
  } catch (error) {
    showSnackbar(getErrorMessage(error, fallbackMessage), 'error')
  } finally {
    loading.value = false
  }
}

onMounted(loadPageData)
</script>

<template>
  <v-container class="app-page">
    <PageHeader
      eyebrow="Student"
      title="Submit Peer Evaluation"
      subtitle="Review one teammate from the previous week using the rubric configured by the admin."
      class="mb-6"
    />

    <v-alert v-if="errorMessage" type="error" class="mb-4">{{ errorMessage }}</v-alert>
    <v-alert
      v-if="form.evaluateeId && form.evaluatorId === form.evaluateeId"
      type="warning"
      class="mb-4"
    >
      Evaluator and evaluatee cannot be the same student.
    </v-alert>

    <SectionCard
      title="Evaluation Details"
      description="Choose a teammate, confirm the reporting context, and score each rubric criterion carefully."
      eyebrow="Peer Review"
      icon="mdi-account-star-outline"
      class="peer-form-card"
    >
      <template #actions>
        <v-chip color="secondary" variant="flat" prepend-icon="mdi-account-group-outline">
          {{ teammates.length }} teammate{{ teammates.length === 1 ? '' : 's' }} available
        </v-chip>
      </template>

      <v-row>
        <v-col cols="12" md="4">
          <v-text-field
            v-model="form.evaluatorId"
            label="Evaluator ID"
            prepend-inner-icon="mdi-account-outline"
            readonly
          />
        </v-col>
        <v-col cols="12" md="4">
          <v-text-field
            v-model="form.teamId"
            label="Team ID"
            prepend-inner-icon="mdi-account-group-outline"
            readonly
          />
        </v-col>
        <v-col cols="12" md="4">
          <v-text-field
            v-model="form.sectionId"
            label="Section ID"
            prepend-inner-icon="mdi-google-classroom"
            readonly
          />
        </v-col>
      </v-row>

      <v-row>
        <v-col cols="12" md="4">
          <v-select
            v-model="form.evaluateeId"
            :items="teammates"
            item-title="fullName"
            item-value="id"
            label="Teammate"
            prepend-inner-icon="mdi-account-check-outline"
          />
        </v-col>
        <v-col cols="12" md="4">
          <v-select
            v-model="form.activeWeekId"
            :items="availableWeeks"
            item-title="title"
            item-value="id"
            label="Evaluation Week"
            prepend-inner-icon="mdi-calendar-week"
            clearable
          >
            <template #selection="{ item }">{{ item.raw.title }}</template>
            <template #item="{ props, item }">
              <v-list-item
                v-bind="props"
                :title="item.raw.title"
                :subtitle="item.raw.details"
              />
            </template>
          </v-select>
        </v-col>
        <v-col cols="12" md="4">
          <v-select
            v-model="form.rubricId"
            :items="rubrics"
            item-title="name"
            item-value="id"
            label="Rubric"
            prepend-inner-icon="mdi-format-list-checks"
          />
        </v-col>
      </v-row>

      <AppEmptyState
        v-if="!pageLoading && teammates.length === 0"
        title="No teammates available"
        description="Ask the admin to assign at least two students to your team before submitting peer evaluations."
        icon="mdi-account-group-outline"
        class="mb-6"
      />

      <AppEmptyState
        v-if="!pageLoading && availableWeeks.length === 0"
        title="No previous week available"
        description="Peer evaluations require a currently active week and a previous week to evaluate."
        icon="mdi-calendar-alert-outline"
        class="mb-6"
      />

      <div class="d-flex align-center justify-space-between flex-wrap ga-3 mt-2 mb-4">
        <div>
          <div class="text-h6 font-weight-bold mb-1">Rubric Criteria</div>
          <p class="text-body-2 text-medium-emphasis mb-0">
            Give every criterion a score of 0 or higher and add comments when they help explain the rating.
          </p>
        </div>
        <v-chip color="secondary" variant="flat" prepend-icon="mdi-format-list-checks">
          {{ form.criterionScores.length }} criteri{{ form.criterionScores.length === 1 ? 'on' : 'a' }}
        </v-chip>
      </div>

      <v-card
        v-for="score in form.criterionScores"
        :key="score.criterionId"
        variant="outlined"
        rounded="xl"
        class="mb-4 criterion-card"
      >
        <v-card-title class="d-flex align-center justify-space-between flex-wrap ga-3">
          <span>{{ score.criterionName }}</span>
          <v-chip color="secondary" size="small">Criterion {{ score.criterionId }}</v-chip>
        </v-card-title>
        <v-card-text>
          <v-row>
            <v-col cols="12" md="4">
              <v-text-field
                :model-value="score.criterionId"
                label="Criterion ID"
                prepend-inner-icon="mdi-pound"
                readonly
              />
            </v-col>
            <v-col cols="12" md="8">
              <v-text-field
                v-model="score.score"
                type="number"
                min="0"
                label="Score"
                prepend-inner-icon="mdi-star-outline"
              />
            </v-col>
          </v-row>
          <v-textarea
            v-model="score.publicComment"
            rows="2"
            label="Public Comment"
            class="mb-3"
          />
          <v-textarea
            v-model="score.privateComment"
            rows="2"
            label="Private Comment"
          />
        </v-card-text>
      </v-card>

      <div class="d-flex justify-space-between align-center flex-wrap ga-3 mt-4">
        <p class="text-body-2 text-medium-emphasis mb-0">
          Evaluator and evaluatee must be different students before you can submit.
        </p>
        <div class="action-cluster">
          <v-btn
            color="primary"
            variant="outlined"
            prepend-icon="mdi-refresh"
            :disabled="loading || pageLoading"
            @click="loadPageData"
          >
            Reload Options
          </v-btn>
          <v-btn
            color="success"
            prepend-icon="mdi-check-circle-outline"
            :loading="loading"
            :disabled="loading"
            @click="submitEvaluation"
          >
            Submit Peer Evaluation
          </v-btn>
        </div>
      </div>
    </SectionCard>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="4000">
      {{ snackbar.message }}
    </v-snackbar>
  </v-container>
</template>

<style scoped>
.peer-form-card {
  background:
    radial-gradient(circle at top right, rgba(239, 246, 255, 0.85), transparent 28%),
    linear-gradient(180deg, #ffffff, #fcfcfc);
}

.criterion-card {
  border-color: rgba(0, 0, 0, 0.05);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}
</style>
