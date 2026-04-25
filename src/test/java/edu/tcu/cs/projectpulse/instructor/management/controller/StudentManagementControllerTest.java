package edu.tcu.cs.projectpulse.instructor.management.controller;

import edu.tcu.cs.projectpulse.common.exception.GlobalExceptionHandler;
import edu.tcu.cs.projectpulse.config.SecurityConfig;
import edu.tcu.cs.projectpulse.instructor.management.dto.ManagedUserResponse;
import edu.tcu.cs.projectpulse.instructor.management.service.StudentManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentManagementController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class StudentManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentManagementService studentManagementService;

    private ManagedUserResponse studentResponse;

    @BeforeEach
    void setUp() {
        studentResponse = new ManagedUserResponse();
        studentResponse.setId(1L);
        studentResponse.setEmail("student@tcu.edu");
        studentResponse.setFirstName("Alex");
        studentResponse.setLastName("Carter");
        studentResponse.setRole("STUDENT");
        studentResponse.setActive(true);
        studentResponse.setTeamId(2L);
        studentResponse.setTeamName("Team Alpha");
        studentResponse.setSectionId(3L);
        studentResponse.setSectionName("Section 1");
    }

    @Test
    @DisplayName("GET /api/instructor/students returns student summaries")
    void findStudentsShouldReturnList() throws Exception {
        when(studentManagementService.findStudents("alex", 3L, null, true)).thenReturn(List.of(studentResponse));

        mockMvc.perform(get("/api/instructor/students")
                        .param("search", "alex")
                        .param("sectionId", "3")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("STUDENT"));
    }

    @Test
    @DisplayName("DELETE /api/instructor/students/{id} returns 204")
    void deleteStudentShouldReturnNoContent() throws Exception {
        doNothing().when(studentManagementService).deleteStudent(1L);

        mockMvc.perform(delete("/api/instructor/students/1"))
                .andExpect(status().isNoContent());
    }
}
