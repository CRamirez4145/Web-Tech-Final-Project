<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { fallbackMessage, getErrorMessage, getSharedUsers } from '@/api/appApi'
import AppEmptyState from '@/components/AppEmptyState.vue'
import CardContainer from '@/components/CardContainer.vue'
import PageHeader from '@/components/PageHeader.vue'
import { useSessionStore } from '@/stores/session'

const router = useRouter()
const sessionStore = useSessionStore()

const role = ref('ADMIN')
const selectedUserId = ref(null)
const loading = ref(false)
const errorMessage = ref('')
const users = ref([])

const roleOptions = [
  { title: 'Admin', value: 'ADMIN', description: 'Configure the course and manage assignments.' },
  { title: 'Student', value: 'STUDENT', description: 'Submit reports and track your progress.' },
  { title: 'Instructor', value: 'INSTRUCTOR', description: 'Review WAR and peer-evaluation reports.' },
]

const selectableUsers = computed(() => users.value.filter((user) => user.role === role.value))

const loadUsers = async () => {
  loading.value = true
  errorMessage.value = ''
  try {
    users.value = await getSharedUsers()
  } catch (error) {
    errorMessage.value = getErrorMessage(error, fallbackMessage)
  } finally {
    loading.value = false
  }
}

const login = async () => {
  if (role.value === 'ADMIN') {
    sessionStore.setSession({
      role: 'ADMIN',
      userId: null,
      userName: 'Admin Demo',
    })
    await router.push('/admin')
    return
  }

  const user = selectableUsers.value.find((item) => item.id === selectedUserId.value)
  if (!user) {
    errorMessage.value = 'Select a user for this role before continuing.'
    return
  }

  sessionStore.setSession({
    role: user.role,
    userId: user.id,
    userName: user.fullName,
  })

  await router.push(role.value === 'STUDENT' ? '/student' : '/instructor')
}

onMounted(loadUsers)
</script>

<template>
  <div class="login-page">
    <v-container class="login-container">
      <v-row justify="center" align="center" class="login-row">
        <v-col cols="12" xl="10">
          <CardContainer class="login-shell" :padded="false">
            <v-row no-gutters class="login-shell-row">
              <v-col cols="12" md="5" class="login-side pa-8 pa-md-10">
                <div class="login-brand-chip mb-6">
                  <v-icon icon="mdi-waveform" size="18" class="mr-2" />
                  Project Pulse
                </div>

                <PageHeader
                  eyebrow="Demo Access"
                  title="Open the right workspace"
                  subtitle="Choose a role, then jump straight into the course management flow without a full auth setup."
                  class="mb-8"
                />

                <div class="login-side-panel mb-6">
                  <div class="text-subtitle-1 font-weight-bold mb-2">What you can do</div>
                  <div class="text-body-2 text-medium-emphasis">
                    Admins manage rubrics, sections, weeks, teams, and users. Students submit reports. Instructors review reporting data.
                  </div>
                </div>

                <v-list class="bg-transparent">
                  <v-list-item
                    v-for="item in roleOptions"
                    :key="item.value"
                    :active="role === item.value"
                    rounded="xl"
                    class="mb-3 role-option"
                    :class="{ 'role-option-active': role === item.value }"
                    @click="role = item.value"
                  >
                    <v-list-item-title>{{ item.title }}</v-list-item-title>
                    <v-list-item-subtitle>{{ item.description }}</v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-col>

              <v-col cols="12" md="7" class="login-form-side pa-8 pa-md-10">
                <div class="text-h5 font-weight-bold mb-2">Enter Workspace</div>
                <p class="text-body-2 text-medium-emphasis mb-6">
                  Choose your role and, when needed, select an existing demo user account.
                </p>

                <v-alert v-if="errorMessage" type="error" class="mb-4">{{ errorMessage }}</v-alert>

                <v-autocomplete
                  v-model="role"
                  :items="roleOptions"
                  item-title="title"
                  item-value="value"
                  label="Role"
                  prepend-inner-icon="mdi-shield-account-outline"
                  class="mb-4"
                />

                <v-autocomplete
                  v-if="role !== 'ADMIN'"
                  v-model="selectedUserId"
                  :items="selectableUsers"
                  item-title="fullName"
                  item-value="id"
                  label="User"
                  prepend-inner-icon="mdi-account-outline"
                  :loading="loading"
                  class="mb-6"
                  hint="These users come from the Admin > Users page."
                  persistent-hint
                />

                <v-alert
                  v-if="role === 'ADMIN'"
                  type="info"
                  icon="mdi-shield-crown-outline"
                  class="mb-6"
                >
                  Admin login uses the built-in demo session and does not require selecting a specific user.
                </v-alert>

                <AppEmptyState
                  v-if="role !== 'ADMIN' && !loading && selectableUsers.length === 0"
                  title="No users available"
                  :description="`Create a ${role.toLowerCase()} user from the admin workspace first.`"
                  icon="mdi-account-plus-outline"
                  class="mb-6"
                />

                <div class="d-flex flex-wrap ga-3">
                  <v-btn color="primary" rounded="pill" size="large" prepend-icon="mdi-login" :loading="loading" @click="login">
                    Open Workspace
                  </v-btn>
                  <v-btn variant="outlined" rounded="pill" size="large" prepend-icon="mdi-arrow-left" to="/">Back Home</v-btn>
                </div>
              </v-col>
            </v-row>
          </CardContainer>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(219, 234, 254, 0.75), transparent 30%),
    radial-gradient(circle at bottom right, rgba(191, 219, 254, 0.55), transparent 28%),
    linear-gradient(180deg, #f5f7fb, #eef3fb);
}

.login-container,
.login-row {
  min-height: 100vh;
}

.login-shell {
  overflow: hidden;
  max-width: 1120px;
  margin: 0 auto;
  box-shadow: 0 24px 50px rgba(15, 23, 42, 0.08);
}

.login-shell-row {
  min-height: 680px;
}

.login-side {
  background:
    radial-gradient(circle at top right, rgba(255, 255, 255, 0.7), transparent 24%),
    linear-gradient(180deg, #dbeafe, #eff6ff 48%, #f8fbff);
}

.login-form-side {
  background: rgba(255, 255, 255, 0.94);
}

.login-brand-chip {
  display: inline-flex;
  align-items: center;
  padding: 10px 16px;
  border-radius: 999px;
  color: #1d4ed8;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 10px 24px rgba(29, 78, 216, 0.1);
  font-weight: 700;
}

.login-side-panel {
  padding: 18px 20px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(37, 99, 235, 0.08);
}

.role-option {
  border: 1px solid rgba(15, 23, 42, 0.06);
  background: rgba(255, 255, 255, 0.58);
  transition:
    transform 0.18s ease,
    box-shadow 0.18s ease,
    background-color 0.18s ease,
    border-color 0.18s ease;
}

.role-option:hover {
  transform: translateY(-1px);
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.08);
}

.role-option-active {
  background: #eff6ff;
  border-color: rgba(37, 99, 235, 0.18);
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.12);
}

@media (max-width: 959px) {
  .login-page,
  .login-container,
  .login-row {
    min-height: auto;
  }

  .login-shell-row {
    min-height: auto;
  }
}
</style>
