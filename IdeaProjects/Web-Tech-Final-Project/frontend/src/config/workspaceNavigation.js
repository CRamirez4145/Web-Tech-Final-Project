export const workspaceNavigation = {
  admin: [
    { title: 'Dashboard', shortTitle: 'Dashboard', to: '/admin', icon: 'mdi-view-grid-outline' },
    { title: 'Rubrics', shortTitle: 'Rubrics', to: '/admin/rubrics', icon: 'mdi-format-list-checks' },
    { title: 'Sections', shortTitle: 'Sections', to: '/admin/sections', icon: 'mdi-google-classroom' },
    { title: 'Active Weeks', shortTitle: 'Weeks', to: '/admin/active-weeks', icon: 'mdi-calendar-week' },
    { title: 'Teams', shortTitle: 'Teams', to: '/admin/teams', icon: 'mdi-account-group-outline' },
    { title: 'Users', shortTitle: 'Users', to: '/admin/users', icon: 'mdi-account-multiple-outline' },
  ],
  student: [
    { title: 'Dashboard', shortTitle: 'Dashboard', to: '/student', icon: 'mdi-view-grid-outline' },
    { title: 'Submit WAR', shortTitle: 'Submit WAR', to: '/student/wars/new', icon: 'mdi-clipboard-text-outline' },
    { title: 'My WARs', shortTitle: 'My WARs', to: '/student/wars', icon: 'mdi-file-document-multiple-outline' },
    {
      title: 'Submit Peer Evaluation',
      shortTitle: 'Submit Evaluation',
      to: '/student/peer-evaluations/new',
      icon: 'mdi-account-star-outline',
    },
    {
      title: 'My Peer Evaluations',
      shortTitle: 'My Evaluations',
      to: '/student/peer-evaluations',
      icon: 'mdi-account-voice-outline',
    },
    { title: 'Profile', shortTitle: 'Profile', to: '/student/profile', icon: 'mdi-account-circle-outline' },
  ],
  instructor: [
    { title: 'Dashboard', shortTitle: 'Dashboard', to: '/instructor', icon: 'mdi-view-grid-outline' },
    {
      title: 'Team WAR Reports',
      shortTitle: 'Team WARs',
      to: '/instructor/reports/team-wars',
      icon: 'mdi-account-group-outline',
    },
    {
      title: 'Student WAR Reports',
      shortTitle: 'Student WARs',
      to: '/instructor/reports/student-wars',
      icon: 'mdi-account-search-outline',
    },
    {
      title: 'Section Peer Evaluations',
      shortTitle: 'Section Evaluations',
      to: '/instructor/reports/section-peer-evaluations',
      icon: 'mdi-google-classroom',
    },
    {
      title: 'Student Peer Evaluations',
      shortTitle: 'Student Evaluations',
      to: '/instructor/reports/student-peer-evaluations',
      icon: 'mdi-chart-box-outline',
    },
  ],
}
