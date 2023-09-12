package com.champlain.enrollmentsservice.domainclientlayer;

import com.champlain.enrollmentsservice.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class CourseClientTest {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        String baseUrl = "http://localhost:" + port + "/courses";
        webTestClient = WebTestClient.bindToServer().baseUrl(baseUrl).build();
    }

    /*
    @Test
    public void testGetCourseByCourseId() {
        webTestClient.get()
                .uri("/courses/{courseId}")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseDTO.class)
                .isEqualTo(CourseResponseDTO.builder()
                        .courseId("course123")
                        .courseNumber("CSCI101")
                        .courseName("Introduction to Computer Science")
                        .numHours(3)
                        .numCredits(3.0)
                        .department("Computer Science")
                        .build());

        CourseClient courseClient = new CourseClient("localhost", Integer.toString(port));

        Mono<CourseResponseDTO> courseMono = courseClient.getCourseByCourseId("course123");

        CourseResponseDTO courseResponseDTO = courseMono.block();
        assertNotNull(courseResponseDTO);


    }

    @Test
    public void testGetCourseByCourseIdNotFound() {
        webTestClient.get().uri("/courses/{courseId}")
                .exchange()
                .expectStatus().isNotFound();

        CourseClient courseClient = new CourseClient("localhost", Integer.toString(port));

        Mono<CourseResponseDTO> courseMono = courseClient.getCourseByCourseId("nonexistentCourse");

        assertThrows(NotFoundException.class, () -> {
            courseMono.block();
        });
    }


     */
}