package com.vansh.studentmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vansh.studentmanagement.model.Student;
import com.vansh.studentmanagement.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for all six endpoints, running against an in-memory
 * H2 database (see src/test/resources/application.properties).
 */
@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Student saved;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        saved = repository.save(new Student("Aarav Sharma", "aarav@example.com", "B.Tech CSE", 20));
        repository.save(new Student("Priya Nair", "priya@example.com", "B.Tech CSE", 21));
        repository.save(new Student("Rohan Mehta", "rohan@example.com", "MBA", 24));
    }

    @Test
    @DisplayName("POST /api/students creates a student and returns 201")
    void createStudent_returns201() throws Exception {
        Student input = new Student("Sana Khan", "sana@example.com", "BCA", 19);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Sana Khan"))
                .andExpect(jsonPath("$.email").value("sana@example.com"));
    }

    @Test
    @DisplayName("POST /api/students with a duplicate email returns 409")
    void createStudent_duplicateEmail_returns409() throws Exception {
        Student input = new Student("Someone Else", "aarav@example.com", "BCA", 19);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("already exists")));
    }

    @Test
    @DisplayName("POST /api/students with invalid data returns 400 with field errors")
    void createStudent_invalidInput_returns400() throws Exception {
        Student input = new Student("", "not-an-email", "", 0);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.course").exists())
                .andExpect(jsonPath("$.fieldErrors.age").exists());
    }

    @Test
    @DisplayName("GET /api/students returns all students")
    void getAllStudents_returns200() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("GET /api/students/{id} returns the student, 404 when missing")
    void getStudentById() throws Exception {
        mockMvc.perform(get("/api/students/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Aarav Sharma"));

        mockMvc.perform(get("/api/students/{id}", 99999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Student not found with id: 99999"));
    }

    @Test
    @DisplayName("PUT /api/students/{id} updates the student")
    void updateStudent_returns200() throws Exception {
        Student updated = new Student("Aarav S.", "aarav@example.com", "B.Tech IT", 21);

        mockMvc.perform(put("/api/students/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Aarav S."))
                .andExpect(jsonPath("$.course").value("B.Tech IT"))
                .andExpect(jsonPath("$.age").value(21));

        mockMvc.perform(put("/api/students/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/students/{id} returns 204 and the student is gone")
    void deleteStudent_returns204() throws Exception {
        mockMvc.perform(delete("/api/students/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/students/{id}", saved.getId()))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/students/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/students/course/{course} filters case-insensitively")
    void getStudentsByCourse() throws Exception {
        mockMvc.perform(get("/api/students/course/{course}", "b.tech cse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/api/students/course/{course}", "MBA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Rohan Mehta"));

        mockMvc.perform(get("/api/students/course/{course}", "No Such Course"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/students/abc returns 400, not a 500")
    void getStudentById_nonNumeric_returns400() throws Exception {
        mockMvc.perform(get("/api/students/abc"))
                .andExpect(status().isBadRequest());
    }
}
