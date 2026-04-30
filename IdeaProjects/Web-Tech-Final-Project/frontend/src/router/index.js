import { createRouter, createWebHistory } from 'vue-router'

import AppLayout from '@/layouts/AppLayout.vue'
import AdminActiveWeeksPage from '@/pages/admin/AdminActiveWeeksPage.vue'
import AdminDashboardPage from '@/pages/admin/AdminDashboardPage.vue'
import AdminRubricsPage from '@/pages/admin/AdminRubricsPage.vue'
import AdminSectionsPage from '@/pages/admin/AdminSectionsPage.vue'
import AdminTeamsPage from '@/pages/admin/AdminTeamsPage.vue'
import AdminUsersPage from '@/pages/admin/AdminUsersPage.vue'
import HomePage from '@/pages/HomePage.vue'
import InstructorDashboardPage from '@/pages/instructor/InstructorDashboardPage.vue'
import SectionPeerEvaluationsPage from '@/pages/instructor/SectionPeerEvaluationsPage.vue'
import StudentPeerEvaluationsReportPage from '@/pages/instructor/StudentPeerEvaluationsReportPage.vue'
import StudentWarReportsPage from '@/pages/instructor/StudentWarReportsPage.vue'
import TeamWarReportsPage from '@/pages/instructor/TeamWarReportsPage.vue'
import LoginPage from '@/pages/LoginPage.vue'
import MyPeerEvaluationsPage from '@/pages/student/MyPeerEvaluationsPage.vue'
import MyWarsPage from '@/pages/student/MyWarsPage.vue'
import PeerEvaluationPage from '@/pages/student/PeerEvaluationPage.vue'
import ProfilePage from '@/pages/student/ProfilePage.vue'
import StudentDashboard from '@/pages/student/StudentDashboard.vue'
import WarPage from '@/pages/student/WarPage.vue'
import { getStoredSession } from '@/stores/session'

const requireRole = (role) => (to, from, next) => {
  const session = getStoredSession()
  if (!session || session.role !== role) {
    next('/login')
    return
  }
  next()
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior() {
    return {
      top: 0,
      behavior: 'smooth',
    }
  },
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginPage,
    },
    {
      path: '/admin',
      component: AppLayout,
      props: { role: 'admin' },
      beforeEnter: requireRole('ADMIN'),
      children: [
        { path: '', name: 'admin-dashboard', component: AdminDashboardPage },
        { path: 'rubrics', name: 'admin-rubrics', component: AdminRubricsPage },
        { path: 'sections', name: 'admin-sections', component: AdminSectionsPage },
        { path: 'active-weeks', name: 'admin-active-weeks', component: AdminActiveWeeksPage },
        { path: 'teams', name: 'admin-teams', component: AdminTeamsPage },
        { path: 'users', name: 'admin-users', component: AdminUsersPage },
      ],
    },
    {
      path: '/student',
      component: AppLayout,
      props: { role: 'student' },
      beforeEnter: requireRole('STUDENT'),
      children: [
        { path: '', name: 'student-dashboard', component: StudentDashboard },
        { path: 'wars/new', name: 'student-war-new', component: WarPage },
        { path: 'wars', name: 'student-wars', component: MyWarsPage },
        {
          path: 'peer-evaluations/new',
          name: 'student-peer-evaluation-new',
          component: PeerEvaluationPage,
        },
        {
          path: 'peer-evaluations',
          name: 'student-peer-evaluations',
          component: MyPeerEvaluationsPage,
        },
        { path: 'profile', name: 'student-profile', component: ProfilePage },
      ],
    },
    {
      path: '/instructor',
      component: AppLayout,
      props: { role: 'instructor' },
      beforeEnter: requireRole('INSTRUCTOR'),
      children: [
        { path: '', name: 'instructor-dashboard', component: InstructorDashboardPage },
        {
          path: 'reports/team-wars',
          name: 'instructor-team-wars',
          component: TeamWarReportsPage,
        },
        {
          path: 'reports/student-wars',
          name: 'instructor-student-wars',
          component: StudentWarReportsPage,
        },
        {
          path: 'reports/section-peer-evaluations',
          name: 'instructor-section-peer-evaluations',
          component: SectionPeerEvaluationsPage,
        },
        {
          path: 'reports/student-peer-evaluations',
          name: 'instructor-student-peer-evaluations',
          component: StudentPeerEvaluationsReportPage,
        },
      ],
    },
  ],
})

export default router
