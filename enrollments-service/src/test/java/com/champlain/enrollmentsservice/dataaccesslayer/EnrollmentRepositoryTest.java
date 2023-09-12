package com.champlain.enrollmentsservice.dataaccesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static com.champlain.enrollmentsservice.dataaccesslayer.Semester.SPRING;
import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
class EnrollmentRepositoryTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @BeforeEach
    public void setupDB(){
        StepVerifier
                .create(enrollmentRepository.deleteAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findEnrollmentByEnrollmentId_validId_shouldSucceed(){
        //arrange
        Enrollment enrollment = Enrollment.builder()
                .enrollmentId("123")
                .enrollmentYear(2023)
                .semester(SPRING)
                .studentId("123")
                .studentFirstName("John")
                .studentLastName("Doe")
                .courseId("123")
                .courseName("CourseName")
                .courseNumber("420-NA")
                .build();

        Mono<Enrollment> setup = enrollmentRepository.save(enrollment);

        StepVerifier
                .create(setup)
                .consumeNextWith(insertedEnrollment -> {
                    assertNotNull(insertedEnrollment);
                    assertEquals(enrollment.getEnrollmentId(), insertedEnrollment.getEnrollmentId());

                })
                .verifyComplete();

        Mono<Enrollment> addedEnrollment = enrollmentRepository
                .findEnrollmentByEnrollmentId(enrollment.getEnrollmentId());

        StepVerifier
                .create(addedEnrollment)
                .consumeNextWith(foundEnrollment -> {
                    assertNotNull(foundEnrollment);
                    assertEquals(enrollment.getEnrollmentId(), foundEnrollment.getEnrollmentId());
                })
                .verifyComplete();
    }

    @Test
    void findAllEnrollmentsByStudentId_validId_shouldSucceed() {
        // Arrange
        String studentId = "123";

        Enrollment enrollment1 = Enrollment.builder()
                .enrollmentId("enrollment_id_1")
                .enrollmentYear(2023)
                .semester(SPRING)
                .studentId(studentId)
                .studentFirstName("John")
                .studentLastName("Doe")
                .courseId("course_id_1")
                .courseName("CourseName")
                .courseNumber("420-NA")
                .build();

        Enrollment enrollment2 = Enrollment.builder()
                .enrollmentId("enrollment_id_2")
                .enrollmentYear(2023)
                .semester(SPRING)
                .studentId(studentId)
                .studentFirstName("Jane")
                .studentLastName("Smith")
                .courseId("course_id_2")
                .courseName("AnotherCourse")
                .courseNumber("421-NB")
                .build();

        Flux<Enrollment> saveAll = enrollmentRepository.saveAll(Arrays.asList(enrollment1, enrollment2));

        StepVerifier.create(saveAll).expectNextCount(2).verifyComplete();

        // Act
        Flux<Enrollment> getByStudentId = enrollmentRepository.findAllEnrollmentByStudentId(studentId);

        // Assert
        StepVerifier.create(getByStudentId)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findAllEnrollmentByCourseId_validId_shouldSucceed() {
        // Arrange
        String courseId = "course_id_1";

        Enrollment enrollment1 = Enrollment.builder()
                .enrollmentId("enrollment_id_1")
                .enrollmentYear(2023)
                .semester(SPRING)
                .studentId("student_id_1")
                .studentFirstName("John")
                .studentLastName("Doe")
                .courseId(courseId)
                .courseName("CourseName")
                .courseNumber("420-NA")
                .build();

        Enrollment enrollment2 = Enrollment.builder()
                .enrollmentId("enrollment_id_2")
                .enrollmentYear(2023)
                .semester(SPRING)
                .studentId("student_id_2")
                .studentFirstName("Jane")
                .studentLastName("Smith")
                .courseId("course_id_2")
                .courseName("AnotherCourse")
                .courseNumber("421-NB")
                .build();

        Flux<Enrollment> saveAll = enrollmentRepository.saveAll(Arrays.asList(enrollment1, enrollment2));

        StepVerifier.create(saveAll).expectNextCount(2).verifyComplete();

        // Act
        Flux<Enrollment> getByCourseId = enrollmentRepository.findAllEnrollmentByCourseId(courseId);

        // Assert
        StepVerifier.create(getByCourseId)
                .expectNextCount(1) // Assuming there's one enrollment for this course
                .verifyComplete();
    }

    @Test
    void findAllEnrollmentByEnrollmentYear_validYear_shouldSucceed() {
        // Arrange
        Integer enrollmentYear = 2023;

        Enrollment enrollment1 = Enrollment.builder()
                .enrollmentId("enrollment_id_1")
                .enrollmentYear(enrollmentYear)
                .semester(SPRING)
                .studentId("student_id_1")
                .studentFirstName("John")
                .studentLastName("Doe")
                .courseId("course_id_1")
                .courseName("CourseName")
                .courseNumber("420-NA")
                .build();

        Enrollment enrollment2 = Enrollment.builder()
                .enrollmentId("enrollment_id_2")
                .enrollmentYear(enrollmentYear)
                .semester(SPRING)
                .studentId("student_id_2")
                .studentFirstName("Jane")
                .studentLastName("Smith")
                .courseId("course_id_2")
                .courseName("AnotherCourse")
                .courseNumber("421-NB")
                .build();

        Flux<Enrollment> saveAll = enrollmentRepository.saveAll(Arrays.asList(enrollment1, enrollment2));

        StepVerifier.create(saveAll).expectNextCount(2).verifyComplete();

        // Act
        Flux<Enrollment> getByEnrollmentYear = enrollmentRepository.findAllEnrollmentByEnrollmentYear(enrollmentYear);

        // Assert
        StepVerifier.create(getByEnrollmentYear)
                .expectNextCount(2) // Assuming there are two enrollments for this year
                .verifyComplete();
    }

}