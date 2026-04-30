<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

import {
  assignInstructorToTeam,
  assignStudentToTeam,
  createAdminTeam,
  fallbackMessage,
  getAdminTeams,
  getErrorMessage,
  getSharedSections,
  getSharedUsers,
  removeInstructorFromTeam,
  removeStudentFromTeam,
  updateAdminTeam,
} from '@/api/appApi'
import AppEmptyState from '@/components/AppEmptyState.vue'
import PageHeader from '@/components/PageHeader.vue'
import SectionCard from '@/components/SectionCard.vue'

const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const snackbar = ref({ show: false, color: 'success', message: '' })

const teams = ref([])
const sections = ref([])
const students = ref([])
const instructors = ref([])

const form = reactive({
  id: null,
  name: '',
  sectionId: null,
  instructorId: null,
})

const assignment = reactive({
  teamId: null,
  studentId: null,
  instructorId: null,
})

const unassignedStudents = computed(() => students.value.filter((student) => !student.teamId))

const showSnackbar = (message, color) => {
  snackbar.value = { show: true, color, message }
}

const isEditing = () => form.id !== null

const resetForm = () => {
  form.id = null
  form.name = ''
  form.sectionId = null
  form.instructorId = null
}

const editTeam = (team) => {
  form.id = team.id
  form.name = team.name
  form.sectionId = team.sectionId
  form.instructorId = team.instructorId
}

const studentsForTeam = (teamId) => students.value.filter((student) => student.teamId === teamId)

const loadData = async () => {
  loading.value = true
  errorMessage.value = ''

  try {
    const [teamData, sectionData, studentData, instructorData] = await Promise.all([
      getAdminTeams(),
      getSharedSections(),
      getSharedUsers({ role: 'STUDENT' }),
      getSharedUsers({ role: 'INSTRUCTOR' }),
    ])
    teams.value = teamData
    sections.value = sectionData
    students.value = studentData
    instructors.value = instructorData
  } catch (error) {
    errorMessage.value = getErrorMessage(error, fallbackMessage)
  } finally {
    loading.value = false
  }
}

const saveTeam = async () => {
  if (!form.name.trim() || !form.sectionId) {
    showSnackbar('Team name and section are required.', 'error')
    return
  }

  saving.value = true
  try {
    const payload = {
      name: form.name,
      sectionId: form.sectionId,
      instructorId: form.instructorId,
    }
    if (isEditing()) {
      await updateAdminTeam(form.id, payload)
      showSnackbar('Team updated successfully.', 'success')
    } else {
      await createAdminTeam(payload)
      showSnackbar('Team created successfully.', 'success')
    }
    resetForm()
    await loadData()
  } catch (error) {
    showSnackbar(getErrorMessage(error, fallbackMessage), 'error')
  } finally {
    saving.value = false
  }
}

const assignStudent = async () => {
  if (!assignment.teamId || !assignment.studentId) {
    showSnackbar('Choose both a team and a student.', 'error')
    return
  }
  try {
    await assignStudentToTeam(assignment.teamId, assignment.studentId)
    assignment.studentId = null
    showSnackbar('Student assigned successfully.', 'success')
    await loadData()
  } catch (error) {
    showSnackbar(getErrorMessage(error, fallbackMessage), 'error')
  }
}

const assignInstructor = async () => {
  if (!assignment.teamId || !assignment.instructorId) {
    showSnackbar('Choose both a team and an instructor.', 'error')
    return
  }
  try {
    await assignInstructorToTeam(assignment.teamId, assignment.instructorId)
    assignment.instructorId = null
    showSnackbar('Instructor assigned successfully.', 'success')
    await loadData()
  } catch (error) {
    showSnackbar(getErrorMessage(error, fallbackMessage), 'error')
  }
}

const unassignStudent = async (teamId, studentId) => {
  try {
    await removeStudentFromTeam(teamId, studentId)
    showSnackbar('Student removed from team.', 'success')
    await loadData()
  } catch (error) {
    showSnackbar(getErrorMessage(error, fallbackMessage), 'error')
  }
}

const unassignInstructor = async (teamId, instructorId) => {
  try {
    await removeInstructorFromTeam(teamId, instructorId)
    showSnackbar('Instructor removed from team.', 'success')
    await loadData()
  } catch (error) {
    showSnackbar(getErrorMessage(error, fallbackMessage), 'error')
  }
}

onMounted(loadData)
</script>

<template>
  <v-container class="app-page">
    <PageHeader
      eyebrow="Admin"
      title="Teams"
      subtitle="Create teams, assign instructors, and manage student membership from one place."
      class="mb-8"
    />

    <v-alert v-if="errorMessage" type="error" class="mb-4">{{ errorMessage }}</v-alert>

    <v-row class="mb-6">
      <v-col cols="12" lg="4">
        <SectionCard
          :title="isEditing() ? 'Edit Team' : 'Create Team'"
          description="Each team belongs to a section and can optionally start with an instructor assignment."
          eyebrow="Team Builder"
          icon="mdi-account-group-outline"
          class="mb-4"
        >
          <v-text-field
            v-model="form.name"
            label="Team Name"
            prepend-inner-icon="mdi-account-group-outline"
            class="mb-3"
          />
          <v-select
            v-model="form.sectionId"
            :items="sections"
            item-title="name"
            item-value="id"
            label="Section"
            prepend-inner-icon="mdi-google-classroom"
            class="mb-3"
          />
          <v-select
            v-model="form.instructorId"
            :items="instructors"
            item-title="fullName"
            item-value="id"
            label="Instructor"
            prepend-inner-icon="mdi-school-outline"
            clearable
            class="mb-4"
          />
          <div class="d-flex flex-wrap ga-3">
            <v-btn color="success" prepend-icon="mdi-content-save-outline" :loading="saving" @click="saveTeam">
              {{ isEditing() ? 'Update Team' : 'Create Team' }}
            </v-btn>
            <v-btn variant="outlined" prepend-icon="mdi-refresh" :disabled="saving" @click="resetForm">Reset Form</v-btn>
          </div>
        </SectionCard>

        <SectionCard
          title="Assignments"
          description="Choose a target team, then attach available students and instructors to it."
          eyebrow="Assignments"
          icon="mdi-account-switch-outline"
        >
          <v-select
            v-model="assignment.teamId"
            :items="teams"
            item-title="name"
            item-value="id"
            label="Select Team"
            prepend-inner-icon="mdi-account-group-outline"
            class="mb-3"
          />
          <v-select
            v-model="assignment.studentId"
            :items="unassignedStudents"
            item-title="fullName"
            item-value="id"
            label="Assign Student"
            prepend-inner-icon="mdi-account-plus-outline"
            class="mb-3"
          />
          <v-btn color="primary" variant="outlined" prepend-icon="mdi-account-plus-outline" block class="mb-4" @click="assignStudent">
            Assign Student
          </v-btn>

          <v-select
            v-model="assignment.instructorId"
            :items="instructors"
            item-title="fullName"
            item-value="id"
            label="Assign Instructor"
            prepend-inner-icon="mdi-school-outline"
            class="mb-3"
          />
          <v-btn color="primary" variant="outlined" prepend-icon="mdi-school-outline" block @click="assignInstructor">
            Assign Instructor
          </v-btn>
        </SectionCard>
      </v-col>

      <v-col cols="12" lg="8">
        <SectionCard
          title="Team Directory"
          description="Expand a team to review members, remove assignments, or jump into edit mode."
          eyebrow="Directory"
          icon="mdi-view-list-outline"
        >
          <template #actions>
            <v-btn color="primary" variant="outlined" prepend-icon="mdi-refresh" :loading="loading" @click="loadData">Refresh</v-btn>
          </template>

          <div>
            <AppEmptyState
              v-if="!loading && teams.length === 0"
              title="No teams created yet"
              description="Create a team first, then assign students and instructors to it."
              icon="mdi-account-group-outline"
            />

            <v-expansion-panels v-else>
              <v-expansion-panel v-for="team in teams" :key="team.id">
                <v-expansion-panel-title>
                  {{ team.name }} | {{ team.sectionName }}
                </v-expansion-panel-title>
                <v-expansion-panel-text>
                  <div class="mb-3">
                    <strong>Instructor:</strong> {{ team.instructorName || 'Not assigned' }}
                    <v-btn
                      v-if="team.instructorId"
                      color="error"
                      size="small"
                      variant="tonal"
                      prepend-icon="mdi-close-circle-outline"
                      @click="unassignInstructor(team.id, team.instructorId)"
                    >
                      Remove Instructor
                    </v-btn>
                  </div>

                  <div class="mb-3">
                    <strong>Students:</strong>
                  </div>
                  <v-chip
                    v-for="student in studentsForTeam(team.id)"
                    :key="student.id"
                    class="ma-1"
                    color="primary"
                    variant="outlined"
                  >
                    {{ student.fullName }}
                    <v-btn
                      icon="mdi-close"
                      size="x-small"
                      variant="text"
                      @click.stop="unassignStudent(team.id, student.id)"
                    />
                  </v-chip>

                  <div v-if="studentsForTeam(team.id).length === 0" class="text-body-2 text-medium-emphasis mb-3">
                    No students assigned yet.
                  </div>

                  <v-btn color="primary" size="small" variant="tonal" prepend-icon="mdi-pencil-outline" @click="editTeam(team)">Edit Team</v-btn>
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
