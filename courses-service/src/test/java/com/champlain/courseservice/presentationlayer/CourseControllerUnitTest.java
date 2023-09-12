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

    @Test
    void getCourseByCourseId_NotFoundId_should_ReturnNotFound() {
        // Arrange
        String notFoundId = "12345";

        when(courseService.getCourseById(notFoundId))
                .thenReturn(Mono.empty());

        // Act and Assert
        webTestClient
                .get()
                .uri("/courses/{courseId}", notFoundId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        verify(courseService, times(1))
                .getCourseById(notFoundId);
    }

}