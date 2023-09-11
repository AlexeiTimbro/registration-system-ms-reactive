package com.champlain.courseservice.presentationlayer;

import com.champlain.courseservice.businesslayer.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest(controllers = CourseController.class)
class CourseControllerUnitTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    CourseService courseService;


    @Test
    void getCourseByCourseId_ValidIdShouldSucceed(){
        //arrange
        CourseResponseDTO courseResponseDTO = CourseResponseDTO.builder()
                .courseId("c2db7b50-26b5-43f0-ab03-8dc5dab937fb")
                .courseName("Web Services")
                .courseNumber("420-N45-LA")
                .department("Computer Science")
                .numCredits(2.0)
                .numHours(60)
                .build();

        when(courseService.getCourseById(courseResponseDTO.getCourseId()))
                .thenReturn(Mono.just(courseResponseDTO));

        webTestClient
                .get()
                .uri("/courses/{courseId}", courseResponseDTO.getCourseId())
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody(CourseResponseDTO.class)
                .value(dto ->{
                    assertNotNull(dto);
                    assertEquals(courseResponseDTO.getCourseId(), dto.getCourseId());
                    assertEquals(courseResponseDTO.getCourseName(), dto.getCourseName());

                });

        verify(courseService, times(1))
                .getCourseById(courseResponseDTO.getCourseId());

    }

    /*
    @Test
    void addCourse_ValidCourseShouldSucceed() {
        // Arrange
        CourseRequestDTO courseRequestDTO = CourseRequestDTO.builder()
                .courseName("Web Services")
                .courseNumber("420-N45-LA")
                .department("Computer Science")
                .numCredits(2.0)
                .numHours(60)
                .build();

        CourseResponseDTO addedCourseResponse = CourseResponseDTO.builder()
                .courseId("c2db7b50-26b5-43f0-ab03-8dc5dab937fb")
                .courseName("Web Services")
                .courseNumber("420-N45-LA")
                .department("Computer Science")
                .numCredits(2.0)
                .numHours(60)
                .build();

        // Mock the courseService to return a non-null Mono
        when(courseService.addCourse(Mono.just(courseRequestDTO)))
                .thenReturn(Mono.just(addedCourseResponse));

        // Act and Assert
        webTestClient
                .post()
                .uri("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(courseRequestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseDTO.class)
                .value(dto -> {
                    assertNotNull(dto);
                    assertEquals(addedCourseResponse.getCourseId(), dto.getCourseId());
                    assertEquals(addedCourseResponse.getCourseName(), dto.getCourseName());
                });

        // Verify
        verify(courseService, times(1))
                .addCourse(Mono.just(courseRequestDTO));
    }

     */


/*
    @Test
    void getAllCourses_ValidIdShouldSucceed() {
        // Arrange
        CourseResponseDTO courseResponseDTO = CourseResponseDTO.builder()
                .courseId("c2db7b50-26b5-43f0-ab03-8dc5dab937fb")
                .courseName("Web Services")
                .courseNumber("420-N45-LA")
                .department("Computer Science")
                .numCredits(2.0)
                .numHours(60)
                .build();

        when(courseService.getCourseById(courseResponseDTO.getCourseId()))
                .thenAnswer(invocation -> Flux.just(courseResponseDTO));

        webTestClient
                .get()
                .uri("/courses")
                .accept(MediaType.parseMediaType("text/event-stream"))  // Specify the expected media type
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.parseMediaType("text/event-stream"))  // Specify the expected media type
                .expectBodyList(CourseResponseDTO.class)
                .value(dtoList -> {
                    assertNotNull(dtoList);
                    assertEquals(1, dtoList.size());
                    CourseResponseDTO dto = dtoList.get(0);
                    assertEquals(courseResponseDTO.getCourseId(), dto.getCourseId());
                    assertEquals(courseResponseDTO.getCourseName(), dto.getCourseName());
                });

        verify(courseService, times(1))
                .getCourseById(courseResponseDTO.getCourseId());

    }

 */

    @Test
    void deleteCourseByCourseId_ValidIdShouldSucceed() {
        // Arrange
        String courseId = "c2db7b50-26b5-43f0-ab03-8dc5dab937fb";

        when(courseService.removeCourse(courseId))
                .thenReturn(Mono.empty());

        // Act & Assert
        webTestClient
                .delete()
                .uri("/courses/{courseId}", courseId)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();

        verify(courseService, times(1))
                .removeCourse(courseId);
    }

}