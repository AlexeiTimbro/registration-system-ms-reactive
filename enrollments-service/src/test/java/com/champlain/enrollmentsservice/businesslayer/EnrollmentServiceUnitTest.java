package com.champlain.enrollmentsservice.businesslayer;

import com.champlain.enrollmentsservice.dataaccesslayer.Enrollment;
import com.champlain.enrollmentsservice.dataaccesslayer.EnrollmentRepository;
import com.champlain.enrollmentsservice.dataaccesslayer.Semester;
import com.champlain.enrollmentsservice.domainclientlayer.CourseClient;
import com.champlain.enrollmentsservice.domainclientlayer.CourseResponseDTO;
import com.champlain.enrollmentsservice.domainclientlayer.StudentClient;
import com.champlain.enrollmentsservice.domainclientlayer.StudentResponseDTO;
import com.champlain.enrollmentsservice.presentationlayer.EnrollmentRequestDTO;
import com.champlain.enrollmentsservice.presentationlayer.EnrollmentResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.champlain.enrollmentsservice.dataaccesslayer.Semester.SPRING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class EnrollmentServiceUnitTest {

    @Autowired
    EnrollmentService enrollmentService;

    @MockBean
    private EnrollmentRepository enrollmentRepository;

    @MockBean
    private StudentClient studentClient;

    @MockBean
    private CourseClient courseClient;

    StudentResponseDTO studentResponseDTO = StudentResponseDTO.builder()
            .firstName("Donna")
            .lastName("Hornsby")
            .studentId("c2db7b50-26b5-43f0-ab03-8dc5dab937fb")
            .program("History")
            .build();

    CourseResponseDTO courseResponseDTO = CourseResponseDTO.builder()
            .courseId("c2db7b50-26b5-43f0-ab03-8dc5dab937fb")
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

    Enrollment enrollment = Enrollment.builder()

            .enrollmentId("c2db7b50-26b5-43f0-ab03-8dc5dab937fb")
            .enrollmentYear(2023)
            .semester(SPRING)
            .studentId("c2db7b50-26b5-43f0-ab03-8dc5dab937fb")
            .studentFirstName("John")
            .studentLastName("Doe")
            .courseId("c2db7b50-26b5-43f0-ab03-8dc5dab937fb")
            .courseName("CourseName")
            .courseNumber("420-NA")
            .build();

    @Test
    void getEnrollmentByEnrollmentId_ValidId_shouldSucceed(){
        //arrange
        when(enrollmentRepository.findEnrollmentByEnrollmentId(anyString()))
                .thenReturn(Mono.just(enrollment));

        //act
        Mono<EnrollmentResponseDTO> enrollmentResponseDTOMono = enrollmentService
                .getEnrollmentById(enrollment.getEnrollmentId());

        //assert
        StepVerifier
                .create(enrollmentResponseDTOMono)
                .consumeNextWith(foundEnrollment ->{
                    assertNotNull(foundEnrollment);
                    assertEquals(enrollment.getEnrollmentId(), foundEnrollment.getEnrollmentId());
                    assertEquals(enrollment.getEnrollmentYear(), foundEnrollment.getEnrollmentYear());
                    assertEquals(enrollment.getSemester(), foundEnrollment.getSemester());
                    assertEquals(enrollment.getStudentId(), foundEnrollment.getStudentId());
                    assertEquals(enrollment.getStudentFirstName(), foundEnrollment.getStudentFirstName());
                    assertEquals(enrollment.getStudentLastName(), foundEnrollment.getStudentLastName());
                    assertEquals(enrollment.getCourseId(), foundEnrollment.getCourseId());
                    assertEquals(enrollment.getCourseName(), foundEnrollment.getCourseName());
                    assertEquals(enrollment.getCourseNumber(), foundEnrollment.getCourseNumber());

                })
                .verifyComplete();
    }
}