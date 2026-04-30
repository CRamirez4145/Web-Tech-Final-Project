<script setup>
import CardContainer from '@/components/CardContainer.vue'
import PageHeader from '@/components/PageHeader.vue'

const reportCards = [
  {
    title: 'Team WAR Reports',
    description: 'View all weekly activity reports submitted by one team.',
    to: '/instructor/reports/team-wars',
    icon: 'mdi-account-group-outline',
  },
  {
    title: 'Student WAR Reports',
    description: 'Review the WAR history for a specific student.',
    to: '/instructor/reports/student-wars',
    icon: 'mdi-account-search-outline',
  },
  {
    title: 'Section Peer Evaluations',
    description: 'Review peer evaluation submissions across a whole section.',
    to: '/instructor/reports/section-peer-evaluations',
    icon: 'mdi-google-classroom',
  },
  {
    title: 'Student Peer Evaluations',
    description: 'Inspect peer evaluations received by a specific student.',
    to: '/instructor/reports/student-peer-evaluations',
    icon: 'mdi-chart-box-outline',
  },
]
</script>

<template>
  <v-container class="app-page">
    <PageHeader
      eyebrow="Instructor"
      title="Instructor Dashboard"
      subtitle="Open reporting views for teams, individual students, and section-wide peer evaluation analysis."
      class="mb-6"
    />

    <v-row>
      <v-col v-for="card in reportCards" :key="card.to" cols="12" md="6">
        <CardContainer class="h-100 report-card interactive-card">
          <div class="d-flex align-center justify-space-between mb-4">
            <div class="text-h6 font-weight-bold">{{ card.title }}</div>
            <v-avatar color="secondary" size="46">
              <v-icon color="primary" :icon="card.icon" />
            </v-avatar>
          </div>
          <p class="text-body-1 text-medium-emphasis mb-6">{{ card.description }}</p>
          <v-btn color="primary" variant="tonal" prepend-icon="mdi-arrow-right" :to="card.to">Open Report</v-btn>
        </CardContainer>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.report-card {
  background:
    radial-gradient(circle at top right, rgba(239, 246, 255, 0.8), transparent 32%),
    linear-gradient(180deg, #ffffff, #fcfdff);
}
</style>
