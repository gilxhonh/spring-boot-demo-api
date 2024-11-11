package com.example.demo.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getStudentsShouldReturnListOfStudents() throws Exception {
        when(studentService.getStudents()).thenReturn(List.of(
                new Student(
                        "Gucci",
                        "gucci.gang@gmail.com",
                        LocalDate.of(2000,1,1)
                )
        ));

        mockMvc.perform(get("/api/v1/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name")
                        .value("Gucci"));
    }

    @Test
    void registerNewStudent() throws Exception {
        Student student = new Student(
                "New",
                "new.student@gmail.com",
                LocalDate.of(2001,1,5)
        );

        mockMvc.perform(post("/api/v1/student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk());

        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentService).addNewStudent(studentCaptor.capture());

        Student capturedStudent = studentCaptor.getValue();

        assertEquals(student.getName(), capturedStudent.getName());
        assertEquals(student.getEmail(), capturedStudent.getEmail());
        assertEquals(student.getDob(), capturedStudent.getDob());
    }

    @Test
    void deleteStudent() throws Exception {
        Long studentId = 1L;

        mockMvc.perform(delete(
                "/api/v1/student/{studentId}", studentId))
                .andExpect(status().isOk());

        verify(studentService).deleteStudent(studentId);
    }

    @Test
    void updateStudentNameAndEmail() throws Exception {
        Long studentId = 1L;
        String updatedName = "UpdatedName";
        String updatedEmail = "updated.email@gmail.com";

        mockMvc.perform(put("/api/v1/student/{studentId}", studentId)
                .param("name", updatedName)
                .param("email", updatedEmail))
                .andExpect(status().isOk());

        verify(studentService).updateStudent(
                studentId,
                updatedName,
                updatedEmail
        );
    }

    @Test
    void updateStudentEmailOnly() throws Exception {
        Long studentId = 1L;
        String updatedName = "UpdatedName";

        mockMvc.perform(put("/api/v1/student/{studentId}", studentId)
                        .param("name", updatedName))
                        .andExpect(status().isOk());

        verify(studentService).updateStudent(
                studentId,
                updatedName,
                null
        );
    }

    @Test
    void updateStudentNameOnly() throws Exception {
        Long studentId = 1L;
        String updatedEmail = "updated.email@gmail.com";

        mockMvc.perform(put("/api/v1/student/{studentId}", studentId)
                        .param("email", updatedEmail))
                        .andExpect(status().isOk());

        verify(studentService).updateStudent(
                studentId,
                null,
                updatedEmail
        );
    }

}
