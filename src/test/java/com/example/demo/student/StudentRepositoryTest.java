package com.example.demo.student;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class StudentRepositoryTest {
    @Autowired
    protected StudentRepository studentRepository;

    @Test
    void itShouldSaveStudent() {
        // Given
        Student student =  new Student(
                "John Doe",
                "john.doe@gmail.com",
                LocalDate.of(2000, 1, 1));

        // When
        Student savedStudent = studentRepository.save(student);

        // Then
        assertThat(savedStudent).isNotNull();
        assertThat(savedStudent.getId()).isNotNull();
        assertThat(savedStudent.getName()).isEqualTo(student.getName());
        assertThat(savedStudent.getEmail()).isEqualTo(student.getEmail());
    }

    @Test
    void itShouldFindStudentByEmail() {
        // Given
        Student student = new Student(
                "Jane Doe",
                "jane.doe@gmail.com",
                LocalDate.of(2001, 1, 1));

        studentRepository.save(student);

        // When
        Optional<Student> optionalStudent = studentRepository
                .findStudentByEmail(student.getEmail());

        // Then
        assertThat(optionalStudent)
                .isPresent()
                .hasValueSatisfying(s -> {
                    assertThat(s.getName()).isEqualTo(student.getName());
                    assertThat(s.getEmail()).isEqualTo(student.getEmail());
                });
    }

    @Test
    void itShouldNotFindStudentByEmailWhenEmailDoesNotExist() {
        // Given
        String email = "nonexistent.email@gmail.com";

        // When
        Optional<Student> optionalStudent = studentRepository
                .findStudentByEmail(email);

        // Then
        assertThat(optionalStudent).isNotPresent();
    }

    @Test
    void itShouldDeleteStudentById() {
        // Given
        Student student = new Student(
                "Mark Smith",
                "mark.smith@gmail.com",
                LocalDate.of(1995, 5, 15));
        Student savedStudent = studentRepository.save(student);
        Long studentId = savedStudent.getId();

        // When
        studentRepository.deleteById(studentId);

        Optional<Student> optionalStudent = studentRepository
                .findById(studentId);

        // Then
        assertThat(optionalStudent).isNotPresent();
    }

    @Test
    void itShouldUpdateStudentEmail() {
        // Given
        Student student = new Student(
                "Alice Johnson",
                "alice.j@gmail.com",
                LocalDate.of(1998, 8, 20));
        studentRepository.save(student);
        String newEmail = "alice.j.updated@gmail.com";

        // When
        student.setEmail(newEmail);
        Student updatedStudent = studentRepository.save(student);

        // Then
        assertThat(updatedStudent.getEmail()).isEqualTo(newEmail);
    }
    @Test
    void itShouldUpdateStudentName() {
        // Given
        Student student = new Student(
                "Alice Johnson",
                "alice.j@gmail.com",
                LocalDate.of(1998, 8, 20));
        studentRepository.save(student);
        String newName = "Alise Johanson";

        // When
        student.setName(newName);
        Student updatedStudent = studentRepository.save(student);

        // Then
        assertThat(updatedStudent.getName()).isEqualTo(newName);
    }
}
