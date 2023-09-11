package com.champlain.enrollmentsservice.presentationlayer;

import com.champlain.enrollmentsservice.dataaccesslayer.EnrollmentRepository;
import com.champlain.enrollmentsservice.dataaccesslayer.Semester;
import com.champlain.enrollmentsservice.domainclientlayer.CourseClient;
import com.champlain.enrollmentsservice.domainclientlayer.CourseResponseDTO;
import com.champlain.enrollmentsservice.domainclientlayer.StudentClient;
import com.champlain.enrollmentsservice.domainclientlayer.StudentResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
class EnrollmentControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private StudentClient studentClient;

    @MockBean
    private CourseClient courseClient;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    StudentResponseDTO studentResponseDTO = StudentResponseDTO.builder()
            .firstName("Donna")
            .lastName("Hornsby")
            .studentId("student123")
            .program("History")
            .build();

    CourseResponseDTO courseResponseDTO = CourseResponseDTO.builder()
            .courseId("course123")
            .courseName("Web Services")
            .courseNumber("420-N45-LA")
            .department("Computer Science")
            .numCredits(2.0)
            .numHours(60)
            .build();

    EnrollmentRequestDTO enrollmentRequestDTO = EnrollmentRequestDTO.builder()
            .enrollmentYear(2023)
            .semester(Semester.FALL)
            .studentId(studentResponseDTO.getStudentId())
            .courseId(courseResponseDTO.getCourseId())
            .build();

    @Test
    void addEnrollment() {
        //arrange

        when(studentClient.getStudentByStudentId(enrollmentRequestDTO.
                getStudentId()))
                .thenReturn(Mono.just(studentResponseDTO));

        when(courseClient.getCourseByCourseId(enrollmentRequestDTO.getCourseId()))
                .thenReturn(Mono.just(courseResponseDTO));

        //act and assert
        webTestClient
                .post()
                .uri("/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(enrollmentRequestDTO)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(EnrollmentResponseDTO.class)
                .value((enrollmentResponseDTO) -> {
                    assertNotNull(enrollmentResponseDTO);
                    assertNotNull(enrollmentResponseDTO.getEnrollmentId());
                    assertEquals(enrollmentRequestDTO.getEnrollmentYear(), enrollmentResponseDTO.getEnrollmentYear());
                    assertEquals(enrollmentRequestDTO.getSemester(), enrollmentResponseDTO.getSemester());
                    assertEquals(enrollmentRequestDTO.getStudentId(), enrollmentResponseDTO.getStudentId());
                    assertEquals(studentResponseDTO.getFirstName(), enrollmentResponseDTO.getStudentFirstName());
                    assertEquals(studentResponseDTO.getLastName(), enrollmentResponseDTO.getStudentLastName());
                    assertEquals(enrollmentRequestDTO.getCourseId(), enrollmentResponseDTO.getCourseId());
                    assertEquals(courseResponseDTO.getCourseName(), enrollmentResponseDTO.getCourseName());
                    assertEquals(courseResponseDTO.getCourseNumber(), enrollmentResponseDTO.getCourseNumber());
                });

    }
}


