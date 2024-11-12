package com.example.demo.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class StudentIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    void itShouldCreateAndRetrieveStudent() throws Exception {
        // Given
        Student student = new Student(
                "Alice",
                "alice@example.com",
                LocalDate.of(2001, 1, 5));

        // When - POST
        mockMvc.perform(post("/api/v1/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk());

        // Then - GET
        mockMvc.perform(get("/api/v1/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(student.getName()))
                .andExpect(jsonPath("$[0].email").value(student.getEmail()));
    }

    @Test
    void itShouldDeleteStudent() throws Exception {
        // Given
        Student student = new Student(
                "Bob",
                "bob@example.com",
                LocalDate.of(2000, 2, 10));
        Student savedStudent = studentRepository.save(student);

        // When - DELETE
        mockMvc.perform(delete("/api/v1/student/{studentId}", savedStudent.getId()))
                .andExpect(status().isOk());

        // Then - Verify deletion in the database
        Optional<Student> deletedStudent = studentRepository
                .findById(savedStudent.getId());

        assertThat(deletedStudent).isNotPresent();
    }

    @Test
    void itShouldUpdateStudent() throws Exception {
        // Given
        Student student = new Student(
                "Charlie",
                "charlie@example.com",
                LocalDate.of(1999, 3, 15));
        Student savedStudent = studentRepository.save(student);
        String updatedName = "Charlie Updated";
        String updatedEmail = "charlie.updated@example.com";

        // When - PUT
        mockMvc.perform(put("/api/v1/student/{studentId}", savedStudent.getId())
                        .param("name", updatedName)
                        .param("email", updatedEmail))
                .andExpect(status().isOk());

        // Then - Verify update in the database
        Optional<Student> updatedStudent = studentRepository
                .findById(savedStudent.getId());

        assertThat(updatedStudent)
                .isPresent()
                .hasValueSatisfying(s -> {
                    assertThat(s.getName()).isEqualTo(updatedName);
                    assertThat(s.getEmail()).isEqualTo(updatedEmail);
                });
    }
}
