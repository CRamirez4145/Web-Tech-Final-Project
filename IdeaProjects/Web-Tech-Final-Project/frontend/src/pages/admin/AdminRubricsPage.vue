<script setup>
import { onMounted, reactive, ref } from 'vue'

import {
  createAdminRubric,
  fallbackMessage,
  getAdminRubrics,
  getErrorMessage,
} from '@/api/appApi'
import AppEmptyState from '@/components/AppEmptyState.vue'
import PageHeader from '@/components/PageHeader.vue'
import SectionCard from '@/components/SectionCard.vue'

const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const snackbar = ref({ show: false, color: 'success', message: '' })
const rubrics = ref([])

const createCriterion = () => ({
  name: '',
  description: '',
})

const rubricForm = reactive({
  name: '',
  criteria: [createCriterion()],
})

const requiredRule = (value) => !!String(value || '').trim() || 'This field is required.'
const sortRubrics = (items) => [...items].sort((left, right) => right.id - left.id)

const showSnackbar = (message, color) => {
  snackbar.value = { show: true, color, message }
}

const addCriterion = () => {
  rubricForm.criteria.push(createCriterion())
}

const removeCriterion = (index) => {
  if (rubricForm.criteria.length === 1) {
    return
  }
  rubricForm.criteria.splice(index, 1)
}

const resetForm = () => {
  rubricForm.name = ''
  rubricForm.criteria = [createCriterion()]
}

const loadRubrics = async () => {
  loading.value = true
  errorMessage.value = ''

  try {
    rubrics.value = sortRubrics(await getAdminRubrics())
  } catch (error) {
    errorMessage.value = getErrorMessage(error, fallbackMessage)
  } finally {
    loading.value = false
  }
}

const saveRubric = async () => {
  if (!rubricForm.name.trim() || rubricForm.criteria.some((criterion) => !criterion.name.trim())) {
    showSnackbar('Rubric name and criterion names are required.', 'error')
    return
  }

  saving.value = true

  try {
    const createdRubric = await createAdminRubric({
      name: rubricForm.name,
      criteria: rubricForm.criteria.map((criterion) => ({
        name: criterion.name,
        description: criterion.description,
      })),
    })
    rubrics.value = sortRubrics([
      createdRubric,
      ...rubrics.value.filter((rubric) => rubric.id !== createdRubric.id),
    ])
    resetForm()
    showSnackbar('Rubric created successfully.', 'success')
    await loadRubrics()
  } catch (error) {
    showSnackbar(getErrorMessage(error, fallbackMessage), 'error')
  } finally {
    saving.value = false
  }
}

onMounted(loadRubrics)
</script>

<template>
  <v-container class="app-page">
    <PageHeader
      eyebrow="Admin"
      title="Rubrics"
      subtitle="Create peer evaluation rubrics and define the scoring criteria students will use."
      class="mb-8"
    />

    <v-row>
      <v-col cols="12" lg="5">
        <SectionCard
          title="Create Rubric"
          description="Build a rubric with clear criteria so peer evaluations stay consistent across teams."
          eyebrow="Course Setup"
          icon="mdi-format-list-checks"
        >
          <v-text-field
            v-model="rubricForm.name"
            label="Rubric Name"
            :rules="[requiredRule]"
            class="mb-4"
          />

          <div class="d-flex align-center justify-space-between flex-wrap ga-3 mb-4">
            <h2 class="text-h6 font-weight-bold mb-0">Criteria</h2>
            <v-btn color="primary" variant="outlined" prepend-icon="mdi-plus" @click="addCriterion">Add Criterion</v-btn>
          </div>

          <v-card
            v-for="(criterion, index) in rubricForm.criteria"
            :key="index"
            variant="outlined"
            rounded="xl"
            class="mb-3 criterion-form-card"
          >
            <v-card-text class="pa-5">
              <v-text-field
                v-model="criterion.name"
                label="Criterion Name"
                :rules="[requiredRule]"
                class="mb-3"
              />
              <v-text-field
                v-model="criterion.description"
                label="Criterion Description"
                class="mb-3"
              />
              <v-btn
                color="error"
                variant="text"
                prepend-icon="mdi-delete-outline"
                :disabled="rubricForm.criteria.length === 1"
                @click="removeCriterion(index)"
              >
                Remove Criterion
              </v-btn>
            </v-card-text>
          </v-card>

          <div class="d-flex flex-wrap ga-3 mt-5">
            <v-btn color="success" prepend-icon="mdi-content-save-outline" :loading="saving" @click="saveRubric">Save Rubric</v-btn>
            <v-btn variant="outlined" prepend-icon="mdi-refresh" :disabled="saving" @click="resetForm">Reset Form</v-btn>
          </div>
        </SectionCard>
      </v-col>

      <v-col cols="12" lg="7">
        <SectionCard
          title="Existing Rubrics"
          description="Review criteria and confirm the rubric library is ready for students."
          eyebrow="Library"
          icon="mdi-book-open-outline"
        >
          <template #actions>
            <v-btn color="primary" variant="outlined" prepend-icon="mdi-refresh" :loading="loading" @click="loadRubrics">Refresh</v-btn>
          </template>

          <div>
            <v-alert v-if="errorMessage" type="error" class="mb-4">{{ errorMessage }}</v-alert>
            <v-progress-linear v-if="loading" indeterminate class="mb-4" />
            <AppEmptyState
              v-if="!loading && rubrics.length === 0"
              title="No rubrics created yet"
              description="Create a rubric to unlock peer evaluations for students."
              icon="mdi-format-list-checks"
            />

            <v-expansion-panels v-else>
              <v-expansion-panel v-for="rubric in rubrics" :key="rubric.id">
                <v-expansion-panel-title>{{ rubric.name }}</v-expansion-panel-title>
                <v-expansion-panel-text>
                  <v-list>
                    <v-list-item
                      v-for="criterion in rubric.criteria"
                      :key="criterion.id"
                      :title="criterion.name"
                      :subtitle="criterion.description || 'No description provided.'"
                    />
                  </v-list>
                </v-expansion-panel-text>
              </v-expansion-panel>
            </v-expansion-panels>
          </div>
        </SectionCard>
      </v-col>
    </v-row>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="4000">
      {{ snackbar.message }}
    </v-snackbar>
  </v-container>
</template>

<style scoped>
.criterion-form-card {
  border-color: rgba(15, 23, 42, 0.05);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}
</style>
