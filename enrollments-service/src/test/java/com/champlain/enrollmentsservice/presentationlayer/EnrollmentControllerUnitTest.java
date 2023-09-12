package com.champlain.enrollmentsservice.presentationlayer;

import com.champlain.enrollmentsservice.businesslayer.EnrollmentService;
import com.champlain.enrollmentsservice.dataaccesslayer.Semester;
import com.champlain.enrollmentsservice.domainclientlayer.CourseResponseDTO;
import com.champlain.enrollmentsservice.domainclientlayer.StudentResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest(controllers = EnrollmentController.class)
class EnrollmentControllerUnitTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    EnrollmentService enrollmentService;

    @MockBean
    ConnectionFactoryInitializer connectionFactoryInitializer;

    @Test
    void getEnrollmentByEnrollmentId_ValidIdShouldSucceed(){
        //arrange
        CourseResponseDTO courseResponseDTO = CourseResponseDTO.builder()
                .courseId("c2db7b50-26b5-43f0-ab03-8dc5dab937fb")
                .courseName("Web Services")
                .courseNumber("420-N45-LA")
                .department("Computer Science")
                .numCredits(2.0)
                .numHours(60)
                .build();

        StudentResponseDTO studentResponseDTO = StudentResponseDTO.builder()
                .firstName("Donna")
                .lastName("Hornsby")
                .studentId("c2db7b50-26b5-43f0-ab03-8dc5dab937fb")
                .program("History")
                .build();

        EnrollmentResponseDTO enrollmentResponseDTO= EnrollmentResponseDTO.builder()
                .enrollmentId("12345")
                .semester(Semester.FALL)
                .studentId(studentResponseDTO.getStudentId())
                .courseId(courseResponseDTO.getCourseId())
                .build();

        when(enrollmentService.getEnrollmentById(enrollmentResponseDTO.getEnrollmentId()))
                .thenReturn(Mono.just(enrollmentResponseDTO));

        webTestClient
                .get()
                .uri("/enrollments/{enrollmentId}", enrollmentResponseDTO.getEnrollmentId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody(EnrollmentResponseDTO.class)
                .value(dto ->{
                    assertNotNull(dto);
                    assertEquals(enrollmentResponseDTO.getEnrollmentId(), dto.getEnrollmentId());
                    assertEquals(enrollmentResponseDTO.getEnrollmentYear(), dto.getEnrollmentYear());
                    assertEquals(enrollmentResponseDTO.getSemester(), dto.getSemester());
                    assertEquals(enrollmentResponseDTO.getStudentId(), dto.getStudentId());
                    assertEquals(enrollmentResponseDTO.getStudentFirstName(), dto.getStudentFirstName());
                    assertEquals(enrollmentResponseDTO.getStudentLastName(), dto.getStudentLastName());
                    assertEquals(enrollmentResponseDTO.getCourseId(), dto.getCourseId());
                    assertEquals(enrollmentResponseDTO.getCourseName(), dto.getCourseName());
                    assertEquals(enrollmentResponseDTO.getCourseNumber(), dto.getCourseNumber());

                });

        verify(enrollmentService, times(1))
                .getEnrollmentById(enrollmentResponseDTO.getEnrollmentId());

    }

    @Test
    void deleteEnrollmentByEnrollmentId_ValidIdShouldSucceed() {
        // Arrange
        String enrollmentId = "c2db7b50-26b5-43f0-ab03-8dc5dab937fb";

        when(enrollmentService.deleteEnrollmentById(enrollmentId))
                .thenReturn(Mono.empty());

        // Act & Assert
        webTestClient
                .delete()
                .uri("/enrollments/{enrollmentId}", enrollmentId)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();

        verify(enrollmentService, times(1))
                .deleteEnrollmentById(enrollmentId);
    }

    @Test
    void getEnrollmentByEnrollmentId_NotFoundId_should_ReturnNotFound() {
        // Arrange
        String notFoundId = "12345";

        when(enrollmentService.getEnrollmentById(notFoundId))
                .thenReturn(Mono.empty());

        // Act and Assert
        webTestClient
                .get()
                .uri("/enrollments/{enrollmentId}", notFoundId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        verify(enrollmentService, times(1))
                .getEnrollmentById(notFoundId);
    }
}