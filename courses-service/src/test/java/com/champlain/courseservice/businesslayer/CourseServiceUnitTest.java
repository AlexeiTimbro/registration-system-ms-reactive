package com.champlain.courseservice.businesslayer;

import com.champlain.courseservice.dataaccesslayer.Course;
import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import com.champlain.courseservice.presentationlayer.CourseRequestDTO;
import com.champlain.courseservice.presentationlayer.CourseResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
class CourseServiceUnitTest {

    @Autowired
    CourseService courseService;

    @MockBean
    private CourseRepository courseRepository;

    CourseResponseDTO courseResponseDTO = CourseResponseDTO.builder()
            .courseId("c2db7b50-26b5-43f0-ab03-8dc5dab937fb")
            .courseName("Web Services")
            .courseNumber("420-N45-LA")
            .department("Computer Science")
            .numCredits(2.0)
            .numHours(60)
            .build();

    Course course = Course.builder()
            .courseId("c2db7b50-26b5-43f0-ab03-8dc5dab937fb")
            .courseName("Web Services")
            .courseNumber("420-N45-LA")
            .department("Computer Science")
            .numCredits(2.0)
            .numHours(60)
            .build();

    @Test
    void getAllCourses_ValidId_shouldSucceed() {
        // Arrange
        when(courseRepository.findAll())
                .thenReturn(Flux.just(course));

        // Act
        Flux<CourseResponseDTO> courseResponseDTOFlux = courseService
                .getAllCourses();

        // Assert
        StepVerifier
                .create(courseResponseDTOFlux)
                .consumeNextWith(foundCourse -> {
                    assertNotNull(foundCourse);
                    assertEquals(course.getCourseId(), foundCourse.getCourseId());
                    assertEquals(course.getCourseName(), foundCourse.getCourseName());
                    assertEquals(course.getCourseNumber(), foundCourse.getCourseNumber());
                    assertEquals(course.getDepartment(), foundCourse.getDepartment());
                    assertEquals(course.getNumCredits(), foundCourse.getNumCredits());
                    assertEquals(course.getNumHours(), foundCourse.getNumHours());
                })
                .verifyComplete();
    }

    @Test
    void getCourseByCourseId_ValidId_shouldSucceed(){
        //arrange
        when(courseRepository.findCourseByCourseId(anyString()))
                .thenReturn(Mono.just(course));

        //act
        Mono<CourseResponseDTO> courseResponseDTOMono = courseService
                .getCourseById(course.getCourseId());

        //assert
        StepVerifier
                .create(courseResponseDTOMono)
                .consumeNextWith(foundCourse ->{
                    assertNotNull(foundCourse);
                    assertEquals(course.getCourseId(), foundCourse.getCourseId());
                    assertEquals(course.getCourseName(), foundCourse.getCourseName());
                    assertEquals(course.getCourseNumber(), foundCourse.getCourseNumber());
                    assertEquals(course.getDepartment(), foundCourse.getDepartment());
                    assertEquals(course.getNumCredits(), foundCourse.getNumCredits());
                    assertEquals(course.getNumHours(), foundCourse.getNumHours());
                })
                .verifyComplete();
    }


    @Test
    void deleteCourse_ValidCourseId_shouldSucceed() {
        // Arrange
        String courseIdToDelete = "c2db7b50-26b5-43f0-ab03-8dc5dab937fb";

        when(courseRepository.findCourseByCourseId(courseIdToDelete))
                .thenReturn(Mono.just(new Course()));

        when(courseRepository.deleteById(anyString()))
                .thenReturn(Mono.empty());

        // Act
        Mono<Void> deletionMono = courseService.removeCourse(courseIdToDelete);

        // Assert
        StepVerifier
                .create(deletionMono)
                .verifyComplete();
    }



    @Test
    void addCourse_ValidCourse_shouldSucceed() {
        // Arrange
        CourseRequestDTO courseRequestDTO = CourseRequestDTO.builder()
                .courseName("Web Services")
                .courseNumber("420-N45-LA")
                .department("Computer Science")
                .numCredits(2.0)
                .numHours(60)
                .build();

        Course courseEntity = Course.builder()
                .courseId("c2db7b50-26b5-43f0-ab03-8dc5dab937fb")
                .courseName("Web Services")
                .courseNumber("420-N45-LA")
                .department("Computer Science")
                .numCredits(2.0)
                .numHours(60)
                .build();

        when(courseRepository.insert(any(Course.class)))
                .thenReturn(Mono.just(courseEntity));

        // Act
        Mono<CourseResponseDTO> courseResponseDTOMono = courseService.addCourse(Mono.just(courseRequestDTO));

        // Assert
        StepVerifier
                .create(courseResponseDTOMono)
                .expectNextMatches(foundCourse -> {
                    assertNotNull(foundCourse);
                    assertEquals(course.getCourseId(), foundCourse.getCourseId());
                    assertEquals(course.getCourseName(), foundCourse.getCourseName());
                    assertEquals(course.getCourseNumber(), foundCourse.getCourseNumber());
                    assertEquals(course.getDepartment(), foundCourse.getDepartment());
                    assertEquals(course.getNumCredits(), foundCourse.getNumCredits());
                    assertEquals(course.getNumHours(), foundCourse.getNumHours());

                    return true;
                })
                .verifyComplete();
    }

    @Test
    void updateCourseById_ValidCourseIdAndRequest_ShouldUpdateAndReturnCourse() {
        // Arrange
        String validCourseId = "c2db7b50-26b5-43f0-ab03-8dc5dab937fb";
        CourseRequestDTO courseRequestDTO = CourseRequestDTO.builder()
                .build();

        Course existingCourse = Course.builder()
                .courseId(validCourseId)
                .id("123")
                .build();

        Course updatedCourseEntity = Course.builder()
                .build();

        when(courseRepository.findCourseByCourseId(validCourseId)).thenReturn(Mono.just(existingCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(Mono.just(updatedCourseEntity));

        // Act and Assert
        StepVerifier
                .create(courseService.updateCourse(Mono.just(courseRequestDTO), validCourseId))
                .expectNextMatches(updatedCourse -> {
                    return true;
                })
                .verifyComplete();
    }





}