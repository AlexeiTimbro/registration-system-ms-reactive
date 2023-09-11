package com.champlain.enrollmentsservice.dataaccesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

}