<script setup>
import { onMounted, reactive, ref } from 'vue'

import {
  createAdminSection,
  fallbackMessage,
  getAdminSections,
  getErrorMessage,
  updateAdminSection,
} from '@/api/appApi'
import AppEmptyState from '@/components/AppEmptyState.vue'
import PageHeader from '@/components/PageHeader.vue'
import SectionCard from '@/components/SectionCard.vue'

const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const snackbar = ref({ show: false, color: 'success', message: '' })
const sections = ref([])

const form = reactive({
  id: null,
  name: '',
})

const requiredRule = (value) => !!String(value || '').trim() || 'Section name is required.'

const showSnackbar = (message, color) => {
  snackbar.value = { show: true, color, message }
}

const isEditing = () => form.id !== null

const resetForm = () => {
  form.id = null
  form.name = ''
}

const editSection = (section) => {
  form.id = section.id
  form.name = section.name
}

const loadSections = async () => {
  loading.value = true
  errorMessage.value = ''
  try {
    sections.value = await getAdminSections()
  } catch (error) {
    errorMessage.value = getErrorMessage(error, fallbackMessage)
  } finally {
    loading.value = false
  }
}

const saveSection = async () => {
  if (!form.name.trim()) {
    showSnackbar('Section name is required.', 'error')
    return
  }

  saving.value = true
  try {
    if (isEditing()) {
      await updateAdminSection(form.id, { name: form.name })
      showSnackbar('Section updated successfully.', 'success')
    } else {
      await createAdminSection({ name: form.name })
      showSnackbar('Section created successfully.', 'success')
    }
    resetForm()
    await loadSections()
  } catch (error) {
    showSnackbar(getErrorMessage(error, fallbackMessage), 'error')
  } finally {
    saving.value = false
  }
}

onMounted(loadSections)
</script>

<template>
  <v-container class="app-page">
    <PageHeader
      eyebrow="Admin"
      title="Sections"
      subtitle="Create and update senior design sections so teams and users can be assigned correctly."
      class="mb-8"
    />

    <v-row>
      <v-col cols="12" md="4">
        <SectionCard
          :title="isEditing() ? 'Edit Section' : 'Create Section'"
          description="Give each course section a clear name so teams and users can be organized accurately."
          eyebrow="Course Setup"
          icon="mdi-google-classroom"
          class="h-100"
        >
          <v-text-field
            v-model="form.name"
            label="Section Name"
            :rules="[requiredRule]"
            class="mb-4"
          />
          <div class="d-flex flex-wrap ga-3">
            <v-btn color="success" prepend-icon="mdi-content-save-outline" :loading="saving" @click="saveSection">
              {{ isEditing() ? 'Update Section' : 'Create Section' }}
            </v-btn>
            <v-btn variant="outlined" prepend-icon="mdi-refresh" :disabled="saving" @click="resetForm">Reset Form</v-btn>
          </div>
        </SectionCard>
      </v-col>

      <v-col cols="12" md="8">
        <SectionCard
          title="Section Directory"
          description="Review every configured section and jump back into edit mode when needed."
          eyebrow="Directory"
          icon="mdi-view-list-outline"
        >
          <template #actions>
            <v-btn color="primary" variant="outlined" prepend-icon="mdi-refresh" :loading="loading" @click="loadSections">Refresh</v-btn>
          </template>

          <div>
            <v-alert v-if="errorMessage" type="error" class="mb-4">{{ errorMessage }}</v-alert>
            <AppEmptyState
              v-if="!loading && sections.length === 0"
              title="No sections created yet"
              description="Create your first section to begin organizing teams and users."
              icon="mdi-google-classroom"
            />
            <v-table v-else>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th class="text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="section in sections" :key="section.id">
                  <td>{{ section.id }}</td>
                  <td>{{ section.name }}</td>
                  <td class="text-right">
                    <v-btn color="primary" size="small" variant="tonal" prepend-icon="mdi-pencil-outline" @click="editSection(section)">Edit Section</v-btn>
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
