<script setup>
import { computed, onMounted, reactive, ref } from 'vue'

import {
  createStudentAccount,
  fallbackMessage,
  getErrorMessage,
  getMyAccount,
  getSharedSections,
  getSharedTeams,
  updateStudentAccount,
} from '@/api/appApi'
import PageHeader from '@/components/PageHeader.vue'
import SectionCard from '@/components/SectionCard.vue'

const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const snackbar = ref({ show: false, color: 'success', message: '' })
const sections = ref([])
const teams = ref([])
const accountExists = ref(false)

const form = reactive({
  email: '',
  firstName: '',
  lastName: '',
  sectionId: null,
  teamId: null,
})

const filteredTeams = computed(() => teams.value.filter((team) => team.sectionId === form.sectionId))

const showSnackbar = (message, color) => {
  snackbar.value = { show: true, color, message }
}

const loadProfile = async () => {
  loading.value = true
  errorMessage.value = ''
  try {
    const [sectionData, teamData] = await Promise.all([getSharedSections(), getSharedTeams()])
    sections.value = sectionData
    teams.value = teamData

    try {
      const account = await getMyAccount()
      accountExists.value = true
      form.email = account.email
      form.firstName = account.firstName
      form.lastName = account.lastName
      form.sectionId = account.sectionId
      form.teamId = account.teamId
    } catch {
      accountExists.value = false
    }
  } catch (error) {
    errorMessage.value = getErrorMessage(error, fallbackMessage)
  } finally {
    loading.value = false
  }
}

const saveProfile = async () => {
  if (
    !form.email.trim() ||
    !form.firstName.trim() ||
    !form.lastName.trim() ||
    !form.sectionId ||
    !form.teamId
  ) {
    showSnackbar('All profile fields are required.', 'error')
    return
  }

  saving.value = true
  try {
    const payload = {
      email: form.email,
      firstName: form.firstName,
      lastName: form.lastName,
      sectionId: form.sectionId,
      teamId: form.teamId,
    }

    if (accountExists.value) {
      await updateStudentAccount(payload)
      showSnackbar('Profile updated successfully.', 'success')
    } else {
      await createStudentAccount(payload)
      accountExists.value = true
      showSnackbar('Student account created successfully.', 'success')
    }
  } catch (error) {
    showSnackbar(getErrorMessage(error, fallbackMessage), 'error')
  } finally {
    saving.value = false
  }
}

onMounted(loadProfile)
</script>

<template>
  <v-container class="app-page">
    <PageHeader
      eyebrow="Student"
      title="My Profile"
      subtitle="Create or update your student account details, section, and team assignment."
      class="mb-6"
    />

    <v-alert v-if="errorMessage" type="error" class="mb-4">{{ errorMessage }}</v-alert>
    <v-alert v-if="!accountExists && !loading" type="info" variant="tonal" class="mb-4">
      No student profile exists for this user yet. Complete the form to create one.
    </v-alert>

    <v-row class="profile-layout">
      <v-col cols="12" lg="8">
        <SectionCard
          :title="accountExists ? 'Profile Details' : 'Create Your Profile'"
          description="Keep your name, email, section, and team current so reports are linked to the right records."
          eyebrow="Student Account"
          icon="mdi-account-circle-outline"
          class="profile-form-card"
        >
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.firstName"
                label="First Name"
                prepend-inner-icon="mdi-account-outline"
                class="mb-3"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.lastName"
                label="Last Name"
                prepend-inner-icon="mdi-account-outline"
                class="mb-3"
              />
            </v-col>
          </v-row>
        <v-text-field
          v-model="form.email"
          label="Email"
          type="email"
          prepend-inner-icon="mdi-email-outline"
          class="mb-3"
          />
          <v-row>
            <v-col cols="12" md="6">
              <v-select
                v-model="form.sectionId"
                :items="sections"
                item-title="name"
                item-value="id"
                label="Section"
                prepend-inner-icon="mdi-google-classroom"
                class="mb-3"
              />
            </v-col>
            <v-col cols="12" md="6">
              <v-select
                v-model="form.teamId"
                :items="filteredTeams"
                item-title="name"
                item-value="id"
                label="Team"
                prepend-inner-icon="mdi-account-group-outline"
              />
            </v-col>
          </v-row>
          <div class="action-cluster mt-4">
            <v-btn color="success" prepend-icon="mdi-content-save-outline" :loading="saving" @click="saveProfile">
              {{ accountExists ? 'Save Profile' : 'Create Profile' }}
            </v-btn>
            <v-btn color="primary" variant="outlined" prepend-icon="mdi-refresh" :loading="loading" @click="loadProfile">
              Reload Details
            </v-btn>
          </div>
        </SectionCard>
      </v-col>

      <v-col cols="12" lg="4">
        <SectionCard
          title="Profile Guidance"
          description="A complete profile keeps your WARs and peer evaluations tied to the right course records."
          eyebrow="Checklist"
          icon="mdi-check-decagram-outline"
          class="h-100"
        >
          <v-list class="bg-transparent pa-0">
            <v-list-item prepend-icon="mdi-email-check-outline" class="px-0">
              <v-list-item-title>Use the same email shown in the user directory.</v-list-item-title>
            </v-list-item>
            <v-list-item prepend-icon="mdi-google-classroom" class="px-0">
              <v-list-item-title>Select your assigned section before choosing a team.</v-list-item-title>
            </v-list-item>
            <v-list-item prepend-icon="mdi-account-group-outline" class="px-0">
              <v-list-item-title>Choose the team your admin assigned to your account.</v-list-item-title>
            </v-list-item>
          </v-list>

          <v-alert type="info" variant="tonal" class="mt-4">
            If your team or section is missing, ask an admin to finish the setup before you submit reports.
          </v-alert>
        </SectionCard>
      </v-col>
    </v-row>

    <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="4000">
      {{ snackbar.message }}
    </v-snackbar>
  </v-container>
</template>

<style scoped>
.profile-form-card {
  background: #ffffff;
}

.profile-layout {
  align-items: stretch;
}
</style>
