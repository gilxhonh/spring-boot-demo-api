package com.example.demo.student;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

import static java.time.Month.*;

@Configuration
public class StudentConfig {

    @Bean
    CommandLineRunner commandLineRunner(StudentRepository repository) {
        return args -> {
            Student gucci = new Student(
                    "Gucci",
                    "gucci.gang@gmail.com",
                    LocalDate.of(2000, JANUARY, 5)
            );

            Student bello = new Student(
                    "Bello",
                    "bello@gmail.com",
                    LocalDate.of(2001, JANUARY, 5)
            );

            repository.saveAll(
                    List.of(gucci, bello)
            );
        };
    }
}
