import apiClient from '@/api/axios'

const fallbackMessage = 'Something went wrong while contacting the backend.'

const getErrorMessage = (error, defaultMessage = fallbackMessage) => {
  return (
    error.response?.data?.message ||
    error.response?.data?.error ||
    error.message ||
    defaultMessage
  )
}

const getReferenceData = async () => {
  const response = await apiClient.get('/api/shared/reference-data')
  return response.data
}

const getSharedUsers = async (params = {}) => {
  const response = await apiClient.get('/api/shared/users', { params })
  return response.data
}

const getSharedSections = async () => {
  const response = await apiClient.get('/api/shared/sections')
  return response.data
}

const getSharedTeams = async () => {
  const response = await apiClient.get('/api/shared/teams')
  return response.data
}

const getSharedRubrics = async () => {
  const response = await apiClient.get('/api/shared/rubrics')
  return response.data
}

const getSharedActiveWeeks = async () => {
  const response = await apiClient.get('/api/shared/active-weeks')
  return response.data
}

const getAdminRubrics = async () => {
  const response = await apiClient.get('/api/admin/rubrics')
  return response.data
}

const createAdminRubric = async (payload) => {
  const response = await apiClient.post('/api/admin/rubrics', payload)
  return response.data
}

const getAdminSections = async () => {
  const response = await apiClient.get('/api/admin/sections')
  return response.data
}

const createAdminSection = async (payload) => {
  const response = await apiClient.post('/api/admin/sections', payload)
  return response.data
}

const updateAdminSection = async (id, payload) => {
  const response = await apiClient.put(`/api/admin/sections/${id}`, payload)
  return response.data
}

const getAdminActiveWeeks = async () => {
  const response = await apiClient.get('/api/admin/active-weeks')
  return response.data
}

const createAdminActiveWeek = async (payload) => {
  const response = await apiClient.post('/api/admin/active-weeks', payload)
  return response.data
}

const updateAdminActiveWeek = async (id, payload) => {
  const response = await apiClient.put(`/api/admin/active-weeks/${id}`, payload)
  return response.data
}

const getAdminTeams = async () => {
  const response = await apiClient.get('/api/admin/teams')
  return response.data
}

const createAdminTeam = async (payload) => {
  const response = await apiClient.post('/api/admin/teams', payload)
  return response.data
}

const updateAdminTeam = async (id, payload) => {
  const response = await apiClient.put(`/api/admin/teams/${id}`, payload)
  return response.data
}

const assignStudentToTeam = async (teamId, studentId) => {
  const response = await apiClient.post(`/api/admin/teams/${teamId}/students/${studentId}`)
  return response.data
}

const removeStudentFromTeam = async (teamId, studentId) => {
  const response = await apiClient.delete(`/api/admin/teams/${teamId}/students/${studentId}`)
  return response.data
}

const assignInstructorToTeam = async (teamId, instructorId) => {
  const response = await apiClient.post(`/api/admin/teams/${teamId}/instructors/${instructorId}`)
  return response.data
}

const removeInstructorFromTeam = async (teamId, instructorId) => {
  const response = await apiClient.delete(`/api/admin/teams/${teamId}/instructors/${instructorId}`)
  return response.data
}

const getAdminUsers = async (params = {}) => {
  const response = await apiClient.get('/api/admin/users', { params })
  return response.data
}

const createAdminUser = async (payload) => {
  const response = await apiClient.post('/api/admin/users', payload)
  return response.data
}

const updateAdminUser = async (id, payload) => {
  const response = await apiClient.put(`/api/admin/users/${id}`, payload)
  return response.data
}

const getMyAccount = async () => {
  const response = await apiClient.get('/api/student/account')
  return response.data
}

const createStudentAccount = async (payload) => {
  const response = await apiClient.post('/api/student/account', payload)
  return response.data
}

const updateStudentAccount = async (payload) => {
  const response = await apiClient.put('/api/student/account', payload)
  return response.data
}

const createWar = async (payload) => {
  const response = await apiClient.post('/api/student/wars', payload)
  return response.data
}

const getMyWars = async () => {
  const response = await apiClient.get('/api/student/wars')
  return response.data
}

const createPeerEvaluation = async (payload) => {
  const response = await apiClient.post('/api/student/peer-evaluations', payload)
  return response.data
}

const getSubmittedPeerEvaluations = async () => {
  const response = await apiClient.get('/api/student/peer-evaluations/submitted')
  return response.data
}

const getReceivedPeerEvaluations = async () => {
  const response = await apiClient.get('/api/student/peer-evaluations/received')
  return response.data
}

const getTeamWarReports = async (teamId) => {
  const response = await apiClient.get('/api/instructor/reports/team-wars', { params: { teamId } })
  return response.data
}

const getStudentWarReports = async (studentId) => {
  const response = await apiClient.get('/api/instructor/reports/student-wars', {
    params: { studentId },
  })
  return response.data
}

const getSectionPeerEvaluationReports = async (sectionId) => {
  const response = await apiClient.get('/api/instructor/reports/section-peer-evaluations', {
    params: { sectionId },
  })
  return response.data
}

const getStudentPeerEvaluationReports = async (studentId) => {
  const response = await apiClient.get('/api/instructor/reports/student-peer-evaluations', {
    params: { studentId },
  })
  return response.data
}

export {
  assignInstructorToTeam,
  assignStudentToTeam,
  createAdminActiveWeek,
  createAdminRubric,
  createAdminSection,
  createAdminTeam,
  createAdminUser,
  createPeerEvaluation,
  createStudentAccount,
  createWar,
  fallbackMessage,
  getAdminActiveWeeks,
  getAdminRubrics,
  getAdminSections,
  getAdminTeams,
  getAdminUsers,
  getErrorMessage,
  getMyAccount,
  getMyWars,
  getReceivedPeerEvaluations,
  getReferenceData,
  getSectionPeerEvaluationReports,
  getSharedActiveWeeks,
  getSharedRubrics,
  getSharedSections,
  getSharedTeams,
  getSharedUsers,
  getStudentPeerEvaluationReports,
  getStudentWarReports,
  getSubmittedPeerEvaluations,
  getTeamWarReports,
  removeInstructorFromTeam,
  removeStudentFromTeam,
  updateAdminActiveWeek,
  updateAdminSection,
  updateAdminTeam,
  updateAdminUser,
  updateStudentAccount,
}
