package edu.tcu.cs.projectpulse.admin.team.controller;

import edu.tcu.cs.projectpulse.admin.team.dto.TeamDetailResponse;
import edu.tcu.cs.projectpulse.admin.team.dto.TeamAssignmentResponse;
import edu.tcu.cs.projectpulse.admin.team.dto.TeamAssignmentStudentResponse;
import edu.tcu.cs.projectpulse.admin.team.dto.TeamAssignmentTeamResponse;
import edu.tcu.cs.projectpulse.admin.team.dto.TeamStudentResponse;
import edu.tcu.cs.projectpulse.admin.team.dto.TeamSummaryResponse;
import edu.tcu.cs.projectpulse.admin.team.service.AdminTeamService;
import edu.tcu.cs.projectpulse.common.exception.BusinessRuleException;
import edu.tcu.cs.projectpulse.common.exception.GlobalExceptionHandler;
import edu.tcu.cs.projectpulse.common.exception.ResourceNotFoundException;
import edu.tcu.cs.projectpulse.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminTeamController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class AdminTeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminTeamService adminTeamService;

    private TeamSummaryResponse teamResponse;
    private TeamDetailResponse teamDetailResponse;
    private TeamAssignmentResponse teamAssignmentResponse;

    @BeforeEach
    void setUp() {
        teamResponse = new TeamSummaryResponse();
        teamResponse.setId(1L);
        teamResponse.setName("Alpha");
        teamResponse.setDescription("Platform team");
        teamResponse.setWebsite("https://alpha.example.com");
        teamResponse.setSectionId(3L);
        teamResponse.setSectionName("Section A");

        TeamStudentResponse student = new TeamStudentResponse();
        student.setId(10L);
        student.setFirstName("Ada");
        student.setLastName("Lovelace");
        student.setEmail("ada@tcu.edu");

        teamDetailResponse = new TeamDetailResponse();
        teamDetailResponse.setId(1L);
        teamDetailResponse.setName("Alpha");
        teamDetailResponse.setDescription("Platform team");
        teamDetailResponse.setWebsite("https://alpha.example.com");
        teamDetailResponse.setSectionId(3L);
        teamDetailResponse.setSectionName("Section A");
        teamDetailResponse.setStudents(List.of(student));

        TeamAssignmentTeamResponse team = new TeamAssignmentTeamResponse();
        team.setId(1L);
        team.setName("Alpha");
        team.setStudentCount(2);

        TeamAssignmentStudentResponse assignedStudent = new TeamAssignmentStudentResponse();
        assignedStudent.setId(10L);
        assignedStudent.setFirstName("Ada");
        assignedStudent.setLastName("Lovelace");
        assignedStudent.setEmail("ada@tcu.edu");
        assignedStudent.setTeamId(1L);
        assignedStudent.setTeamName("Alpha");

        teamAssignmentResponse = new TeamAssignmentResponse();
        teamAssignmentResponse.setSectionId(3L);
        teamAssignmentResponse.setSectionName("Section A");
        teamAssignmentResponse.setTeams(List.of(team));
        teamAssignmentResponse.setStudents(List.of(assignedStudent));
    }

    @Test
    @DisplayName("POST /api/teams returns 201 when team is created")
    void createTeamShouldReturnCreated() throws Exception {
        when(adminTeamService.createTeam(any())).thenReturn(teamDetailResponse);

        String requestBody = """
                {
                  "name": "Alpha",
                  "description": "Platform team",
                  "website": "https://alpha.example.com",
                  "sectionId": 3
                }
                """;

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alpha"))
                .andExpect(jsonPath("$.description").value("Platform team"))
                .andExpect(jsonPath("$.website").value("https://alpha.example.com"))
                .andExpect(jsonPath("$.sectionId").value(3))
                .andExpect(jsonPath("$.students.length()").value(1));
    }

    @Test
    @DisplayName("POST /api/teams returns 400 for invalid payload")
    void createTeamShouldReturnBadRequestForInvalidPayload() throws Exception {
        String invalidBody = """
                {
                  "name": "",
                  "description": "x",
                  "website": "alpha.example.com"
                }
                """;

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.website").exists())
                .andExpect(jsonPath("$.fieldErrors.sectionId").exists());
    }

    @Test
    @DisplayName("POST /api/teams returns 400 when team name already exists")
    void createTeamShouldReturnBadRequestForDuplicateName() throws Exception {
        when(adminTeamService.createTeam(any())).thenThrow(new BusinessRuleException("Team name is already in use."));

        String requestBody = """
                {
                  "name": "Alpha",
                  "description": "Platform team",
                  "website": "https://alpha.example.com",
                  "sectionId": 3
                }
                """;

        mockMvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Team name is already in use."));
    }

    @Test
    @DisplayName("PUT /api/teams/{id} reuses the create form payload and returns updated team")
    void updateTeamShouldReturnUpdatedTeam() throws Exception {
        when(adminTeamService.updateTeam(any(), any())).thenReturn(teamDetailResponse);

        String requestBody = """
                {
                  "name": "Alpha",
                  "description": "Platform team",
                  "website": "https://alpha.example.com",
                  "sectionId": 3
                }
                """;

        mockMvc.perform(put("/api/teams/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alpha"))
                .andExpect(jsonPath("$.description").value("Platform team"))
                .andExpect(jsonPath("$.website").value("https://alpha.example.com"))
                .andExpect(jsonPath("$.sectionId").value(3));
    }

    @Test
    @DisplayName("PUT /api/teams/{id} returns 400 for invalid payload")
    void updateTeamShouldReturnBadRequestForInvalidPayload() throws Exception {
        String invalidBody = """
                {
                  "name": "",
                  "website": "alpha.example.com"
                }
                """;

        mockMvc.perform(put("/api/teams/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.website").exists())
                .andExpect(jsonPath("$.fieldErrors.sectionId").exists());
    }

    @Test
    @DisplayName("PUT /api/teams/{id} returns 404 when team is missing")
    void updateTeamShouldReturnNotFoundWhenMissing() throws Exception {
        when(adminTeamService.updateTeam(any(), any())).thenThrow(new ResourceNotFoundException("Team not found with id: 99"));

        String requestBody = """
                {
                  "name": "Alpha",
                  "description": "Platform team",
                  "website": "https://alpha.example.com",
                  "sectionId": 3
                }
                """;

        mockMvc.perform(put("/api/teams/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Team not found with id: 99"));
    }

    @Test
    @DisplayName("POST /api/teams/assign returns updated assignment state")
    void assignStudentsShouldReturnUpdatedAssignmentState() throws Exception {
        when(adminTeamService.assignStudents(any())).thenReturn(teamAssignmentResponse);

        String requestBody = """
                {
                  "teamId": 1,
                  "studentIds": [10, 11]
                }
                """;

        mockMvc.perform(post("/api/teams/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sectionId").value(3))
                .andExpect(jsonPath("$.teams[0].studentCount").value(2))
                .andExpect(jsonPath("$.students[0].teamName").value("Alpha"));
    }

    @Test
    @DisplayName("POST /api/teams/assign returns 400 for invalid payload")
    void assignStudentsShouldReturnBadRequestForInvalidPayload() throws Exception {
        String invalidBody = """
                {
                  "teamId": null,
                  "studentIds": []
                }
                """;

        mockMvc.perform(post("/api/teams/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.fieldErrors.teamId").exists())
                .andExpect(jsonPath("$.fieldErrors.studentIds").exists());
    }

    @Test
    @DisplayName("POST /api/teams/assign returns 400 for duplicate students")
    void assignStudentsShouldReturnBadRequestForDuplicateStudents() throws Exception {
        when(adminTeamService.assignStudents(any())).thenThrow(new BusinessRuleException("Student ids must be unique."));

        String requestBody = """
                {
                  "teamId": 1,
                  "studentIds": [10, 10]
                }
                """;

        mockMvc.perform(post("/api/teams/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Student ids must be unique."));
    }

    @Test
    @DisplayName("DELETE /api/teams/{teamId}/students/{studentId} removes the student from the team")
    void removeStudentFromTeamShouldReturnUpdatedAssignmentState() throws Exception {
        TeamAssignmentStudentResponse unassignedStudent = new TeamAssignmentStudentResponse();
        unassignedStudent.setId(10L);
        unassignedStudent.setFirstName("Ada");
        unassignedStudent.setLastName("Lovelace");
        unassignedStudent.setEmail("ada@tcu.edu");

        teamAssignmentResponse.getTeams().get(0).setStudentCount(1);
        teamAssignmentResponse.setStudents(List.of(unassignedStudent));

        when(adminTeamService.removeStudentFromTeam(1L, 10L)).thenReturn(teamAssignmentResponse);

        mockMvc.perform(delete("/api/teams/1/students/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teams[0].studentCount").value(1))
                .andExpect(jsonPath("$.students[0].id").value(10))
                .andExpect(jsonPath("$.students[0].teamId").doesNotExist())
                .andExpect(jsonPath("$.students[0].teamName").doesNotExist());
    }

    @Test
    @DisplayName("DELETE /api/teams/{teamId}/students/{studentId} returns 400 when the student is not assigned to the team")
    void removeStudentFromTeamShouldReturnBadRequestWhenStudentNotOnTeam() throws Exception {
        when(adminTeamService.removeStudentFromTeam(1L, 10L))
                .thenThrow(new BusinessRuleException("Student is not assigned to this team."));

        mockMvc.perform(delete("/api/teams/1/students/10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Student is not assigned to this team."));
    }

    @Test
    @DisplayName("DELETE /api/teams/{teamId}/students/{studentId} returns 404 when the student is missing")
    void removeStudentFromTeamShouldReturnNotFoundWhenStudentMissing() throws Exception {
        when(adminTeamService.removeStudentFromTeam(1L, 99L))
                .thenThrow(new ResourceNotFoundException("Student not found with id: 99"));

        mockMvc.perform(delete("/api/teams/1/students/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Student not found with id: 99"));
    }

    @Test
    @DisplayName("DELETE /api/teams/{id} returns 204 when team is deleted")
    void deleteTeamShouldReturnNoContent() throws Exception {
        doNothing().when(adminTeamService).deleteTeam(1L);

        mockMvc.perform(delete("/api/teams/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/teams/{id} returns 400 when deleting would cause data loss")
    void deleteTeamShouldReturnBadRequestWhenUnsafe() throws Exception {
        doThrow(new BusinessRuleException("Team cannot be deleted while students are still assigned. Removing the team would cause data loss."))
                .when(adminTeamService).deleteTeam(1L);

        mockMvc.perform(delete("/api/teams/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Team cannot be deleted while students are still assigned. Removing the team would cause data loss."));
    }

    @Test
    @DisplayName("DELETE /api/teams/{id} returns 404 when team is missing")
    void deleteTeamShouldReturnNotFoundWhenMissing() throws Exception {
        doThrow(new ResourceNotFoundException("Team not found with id: 99"))
                .when(adminTeamService).deleteTeam(99L);

        mockMvc.perform(delete("/api/teams/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Team not found with id: 99"));
    }

    @Test
    @DisplayName("GET /api/teams returns a team list")
    void findTeamsShouldReturnTeamList() throws Exception {
        when(adminTeamService.findTeams(null, null)).thenReturn(List.of(teamResponse));

        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alpha"))
                .andExpect(jsonPath("$[0].sectionName").value("Section A"));
    }

    @Test
    @DisplayName("GET /api/teams passes section and name filters through to the service")
    void findTeamsShouldPassFilters() throws Exception {
        when(adminTeamService.findTeams(3L, "alp")).thenReturn(List.of(teamResponse));

        mockMvc.perform(get("/api/teams")
                        .param("sectionId", "3")
                        .param("name", "alp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].sectionId").value(3));
    }

    @Test
    @DisplayName("GET /api/teams returns empty array when nothing matches")
    void findTeamsShouldReturnEmptyArray() throws Exception {
        when(adminTeamService.findTeams(99L, "missing")).thenReturn(List.of());

        mockMvc.perform(get("/api/teams")
                        .param("sectionId", "99")
                        .param("name", "missing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/teams/{id} returns team details")
    void getTeamShouldReturnDetails() throws Exception {
        when(adminTeamService.getTeam(1L)).thenReturn(teamDetailResponse);

        mockMvc.perform(get("/api/teams/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alpha"))
                .andExpect(jsonPath("$.description").value("Platform team"))
                .andExpect(jsonPath("$.website").value("https://alpha.example.com"))
                .andExpect(jsonPath("$.sectionName").value("Section A"))
                .andExpect(jsonPath("$.students[0].email").value("ada@tcu.edu"));
    }

    @Test
    @DisplayName("GET /api/teams/{id} returns 404 when team is missing")
    void getTeamShouldReturnNotFoundWhenMissing() throws Exception {
        when(adminTeamService.getTeam(99L)).thenThrow(new ResourceNotFoundException("Team not found with id: 99"));

        mockMvc.perform(get("/api/teams/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Team not found with id: 99"));
    }
}
