<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

import {
  createAdminUser,
  fallbackMessage,
  getAdminUsers,
  getErrorMessage,
  getSharedSections,
  getSharedTeams,
  updateAdminUser,
} from '@/api/appApi'
import AppEmptyState from '@/components/AppEmptyState.vue'
import PageHeader from '@/components/PageHeader.vue'
import SectionCard from '@/components/SectionCard.vue'

const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const search = ref('')
const selectedRoleFilter = ref(null)
const snackbar = ref({ show: false, color: 'success', message: '' })

const users = ref([])
const sections = ref([])
const teams = ref([])

const roleOptions = ['ADMIN', 'STUDENT', 'INSTRUCTOR']

const form = reactive({
  id: null,
  firstName: '',
  lastName: '',
  email: '',
  role: 'STUDENT',
  sectionId: null,
  teamId: null,
})

const filteredTeams = computed(() =>
  teams.value.filter((team) => !form.sectionId || team.sectionId === form.sectionId),
)

const showSnackbar = (message, color) => {
  snackbar.value = { show: true, color, message }
}

const isEditing = () => form.id !== null

const resetForm = () => {
  form.id = null
  form.firstName = ''
  form.lastName = ''
  form.email = ''
  form.role = 'STUDENT'
  form.sectionId = null
  form.teamId = null
}

const editUser = (user) => {
  form.id = user.id
  form.firstName = user.firstName
  form.lastName = user.lastName
  form.email = user.email
  form.role = user.role
  form.sectionId = user.sectionId
  form.teamId = user.teamId
}

const loadData = async () => {
  loading.value = true
  errorMessage.value = ''

  try {
    const [userData, sectionData, teamData] = await Promise.all([
      getAdminUsers({ role: selectedRoleFilter.value || undefined, search: search.value || undefined }),
      getSharedSections(),
      getSharedTeams(),
    ])
    users.value = userData
    sections.value = sectionData
    teams.value = teamData
  } catch (error) {
    errorMessage.value = getErrorMessage(error, fallbackMessage)
  } finally {
    loading.value = false
  }
}

const saveUser = async () => {
  if (!form.firstName.trim() || !form.lastName.trim() || !form.email.trim()) {
    showSnackbar('First name, last name, and email are required.', 'error')
    return
  }

  if (form.role === 'STUDENT' && (!form.sectionId || !form.teamId)) {
    showSnackbar('Students must have both a section and a team.', 'error')
    return
  }

  if (form.role === 'INSTRUCTOR' && !form.sectionId) {
    showSnackbar('Instructors must have a section.', 'error')
    return
  }

  saving.value = true
  try {
    const payload = {
      firstName: form.firstName,
      lastName: form.lastName,
      email: form.email,
      role: form.role,
      sectionId: form.role === 'ADMIN' ? null : form.sectionId,
      teamId: form.role === 'STUDENT' ? form.teamId : null,
    }

    if (isEditing()) {
      await updateAdminUser(form.id, payload)
      showSnackbar('User updated successfully.', 'success')
    } else {
      await createAdminUser(payload)
      showSnackbar('User created successfully.', 'success')
    }

    resetForm()
    await loadData()
  } catch (error) {
    showSnackbar(getErrorMessage(error, fallbackMessage), 'error')
  } finally {
    saving.value = false
  }
}

onMounted(loadData)
</script>

<template>
  <v-container class="app-page">
    <PageHeader
      eyebrow="Admin"
      title="Users"
      subtitle="Create and manage student, instructor, and admin users from a single directory."
      class="mb-8"
    />

    <v-row>
      <v-col cols="12" lg="4">
        <SectionCard
          :title="isEditing() ? 'Edit User' : 'Create User'"
          description="Set the person’s role first, then complete their section and team details when required."
          eyebrow="Identity Setup"
          icon="mdi-account-edit-outline"
        >
          <v-text-field v-model="form.firstName" label="First Name" prepend-inner-icon="mdi-account-outline" class="mb-3" />
          <v-text-field v-model="form.lastName" label="Last Name" prepend-inner-icon="mdi-account-outline" class="mb-3" />
          <v-text-field v-model="form.email" label="Email" type="email" prepend-inner-icon="mdi-email-outline" class="mb-3" />
          <v-select v-model="form.role" :items="roleOptions" label="Role" prepend-inner-icon="mdi-shield-account-outline" class="mb-3" />
          <v-select
            v-if="form.role !== 'ADMIN'"
            v-model="form.sectionId"
            :items="sections"
            item-title="name"
            item-value="id"
            label="Section"
            prepend-inner-icon="mdi-google-classroom"
            class="mb-3"
          />
          <v-select
            v-if="form.role === 'STUDENT'"
            v-model="form.teamId"
            :items="filteredTeams"
            item-title="name"
            item-value="id"
            label="Team"
            prepend-inner-icon="mdi-account-group-outline"
            class="mb-4"
          />
          <div class="d-flex flex-wrap ga-3">
            <v-btn color="success" prepend-icon="mdi-content-save-outline" :loading="saving" @click="saveUser">
              {{ isEditing() ? 'Update User' : 'Create User' }}
            </v-btn>
            <v-btn variant="outlined" prepend-icon="mdi-refresh" :disabled="saving" @click="resetForm">Reset Form</v-btn>
          </div>
        </SectionCard>
      </v-col>

      <v-col cols="12" lg="8">
        <SectionCard
          title="User Directory"
          description="Filter the directory by role or search by name or email to edit the right account quickly."
          eyebrow="Directory"
          icon="mdi-account-multiple-outline"
        >

          <div>
            <v-row align="center" class="mb-2">
              <v-col cols="12" md="5">
                <v-text-field
                  v-model="search"
                  label="Search Users"
                  prepend-inner-icon="mdi-magnify"
                />
              </v-col>
              <v-col cols="12" md="4">
                <v-select
                  v-model="selectedRoleFilter"
                  :items="roleOptions"
                  label="Filter by Role"
                  prepend-inner-icon="mdi-filter-variant"
                  clearable
                />
              </v-col>
              <v-col cols="12" md="3" class="d-flex justify-md-end">
                <v-btn color="primary" variant="outlined" prepend-icon="mdi-magnify" :loading="loading" @click="loadData">
                  Search Users
                </v-btn>
              </v-col>
            </v-row>

            <v-alert v-if="errorMessage" type="error" class="mb-4">{{ errorMessage }}</v-alert>
            <AppEmptyState
              v-if="!loading && users.length === 0"
              title="No users match the current filters"
              description="Adjust the search terms or role filter to see more accounts."
              icon="mdi-account-search-outline"
            />

            <v-table v-else>
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Role</th>
                  <th>Email</th>
                  <th>Section</th>
                  <th>Team</th>
                  <th class="text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in users" :key="user.id">
                  <td>{{ user.fullName }}</td>
                  <td>{{ user.role }}</td>
                  <td>{{ user.email }}</td>
                  <td>{{ user.sectionName || 'N/A' }}</td>
                  <td>{{ user.teamName || 'N/A' }}</td>
                  <td class="text-right">
                    <v-btn color="primary" variant="tonal" size="small" prepend-icon="mdi-pencil-outline" @click="editUser(user)">Edit User</v-btn>
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
