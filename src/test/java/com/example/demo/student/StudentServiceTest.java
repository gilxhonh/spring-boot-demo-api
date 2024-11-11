package com.example.demo.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void canGetAllStudents() {
        // Given
        Student student1 = new Student(
                "John Doe",
                "john.doe@google.com",
                LocalDate.of(2000, 1, 1));

        Student student2 = new Student(
                "Jane Doe",
                "jane.doe@google.com",
                LocalDate.of(2001, 1, 1));

        when(studentRepository.findAll())
                .thenReturn(List.of(student1, student2));

        // When
        List<Student> students = studentService.getStudents();

        // Then
        assertEquals(2, students.size());
        assertEquals("John Doe", students.get(0).getName());
        assertEquals("Jane Doe", students.get(1).getName());
    }

    @Test
    void canAddNewStudent() {
        // Given
        Student student = new Student(
                "John Doe",
                "john.doe@gmail.com",
                LocalDate.of(2001,1,1));
        when(studentRepository.findStudentByEmail(student.getEmail()))
                .thenReturn(Optional.empty());

        // When
        studentService.addNewStudent(student);

        // Then
        verify(studentRepository).save(student);
    }

    @Test
    void addNewStudentThrowsExceptionWhenEmailAlreadyExists() {
        // Given
        Student student = new Student(
                "John Doe",
                "john.doe@gmail.com",
                LocalDate.of(2001,1,1));
        when(studentRepository.findStudentByEmail(student.getEmail()))
                .thenReturn(Optional.of(student));

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> studentService.addNewStudent(student));
        verify(studentRepository, never()).save(any());
    }


    @Test
    void canDeleteStudent() {
        // Given
        Long studentId = 1L;
        when(studentRepository.existsById(studentId))
                .thenReturn(true);

        // When
        studentService.deleteStudent(studentId);

        // Then
        verify(studentRepository).deleteById(studentId);
    }

    @Test
    void deleteStudentThrowsExceptionWhenStudentDoesNotExist() {
        // Given
        Long studentId = 1L;
        when(studentRepository.existsById(studentId))
                .thenReturn(Boolean.FALSE);

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> studentService.deleteStudent(studentId));
        verify(studentRepository, never()).deleteById(studentId);
    }

    @Test
    void canUpdateStudentNameAndEmail() {
        // Given
        Long studentId = 1L;
        Student student = new Student(
                "John Doe",
                "john.doe@gmail.com",
                LocalDate.of(2001,1,1));
        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));
        when(studentRepository.findStudentByEmail("new.email@gmail.com"))
                .thenReturn(Optional.empty());

        // When
        studentService.updateStudent(
                studentId,
                "New Name",
                "new.email@gmail.com");

        // Then
        assertEquals("New Name", student.getName());
        assertEquals("new.email@gmail.com", student.getEmail());
    }

    @Test
    void canUpdateStudentNameOnly() {
        // Given
        Long studentId = 1L;
        Student student = new Student(
                "John Doe",
                "john.doe@gmail.com",
                LocalDate.of(2001,1,1));
        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));

        // When
        studentService.updateStudent(
                studentId,
                "Updated Name",
                null);

        // Then
        assertEquals("Updated Name", student.getName());
        assertEquals("john.doe@gmail.com", student.getEmail());
    }

    @Test
    void canUpdateStudentEmailOnly() {
        // Given
        Long studentId = 1L;
        Student student = new Student(
                "John Doe",
                "john.doe@gmail.com",
                LocalDate.of(2001,1,1));
        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));
        when(studentRepository.findStudentByEmail("updated.email@gmail.com"))
                .thenReturn(Optional.empty());

        // When
        studentService.updateStudent(
                studentId,
                null,
                "updated.email@gmail.com");

        // Then
        assertEquals("John Doe", student.getName());
        assertEquals("updated.email@gmail.com", student.getEmail());
    }

    @Test
    void updateStudentThrowsExceptionWhenStudentDoesNotExist() {
        // Given
        Long studentId = 1L;
        when(studentRepository.existsById(studentId))
                .thenReturn(Boolean.FALSE);

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> studentService.updateStudent(
                        studentId,
                        "New Name",
                        "new.email@gmail.com"));
    }

    @Test
    void updateStudentThrowsExceptionWhenEmailAlreadyExists() {
        // Given
        Long studentId = 1L;
        Student student = new Student(
                "John Doe",
                "john.doe@gmail.com",
                LocalDate.of(2001,1,1));
        Student existingStudent = new Student(
                "Existing User",
                "existing.email@gmail.com",
                LocalDate.of(2005,5,5)
        );
        when(studentRepository.findById(studentId))
                .thenReturn(Optional.of(student));
        when(studentRepository.findStudentByEmail("existing.email@gmail.com"))
                .thenReturn(Optional.of(existingStudent));

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> studentService.updateStudent(
                        studentId,
                        null,
                        "existing.email@gmail.com"));
    }
}
