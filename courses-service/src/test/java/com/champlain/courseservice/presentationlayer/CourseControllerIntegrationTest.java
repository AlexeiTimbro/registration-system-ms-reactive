package com.champlain.courseservice.presentationlayer;

import com.champlain.courseservice.dataaccesslayer.Course;
import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port= 0"})
@AutoConfigureWebTestClient
class CourseControllerIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    CourseRepository courseRepository;

    private final Long DB_SIZE = 5L;

    String uuid1 = UUID.randomUUID().toString();
    String uuid2 = UUID.randomUUID().toString();
    String uuid3 = UUID.randomUUID().toString();
    String uuid4 = UUID.randomUUID().toString();
    String uuid5 = UUID.randomUUID().toString();

    Course course1 = buildCourse("Web Services", uuid1);
    Course course2 = buildCourse("Database", uuid2);
    Course course3 = buildCourse("PHP", uuid3);
    Course course4 = buildCourse("IOT 2", uuid4);
    Course course5 = buildCourse("Java 1", uuid5);

    @BeforeEach
    public void dbSetUp(){

        Publisher<Course> setup = courseRepository.deleteAll()
                .thenMany(courseRepository.save(course1))
                .thenMany(courseRepository.save(course2))
                .thenMany(courseRepository.save(course3))
                .thenMany(courseRepository.save(course4))
                .thenMany(courseRepository.save(course5));

        StepVerifier.create(setup).expectNextCount(1).verifyComplete();
    }

    @Test
    void getAllCourses_expected(){
        webTestClient.get()
                .uri("/courses")
                .accept(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
                .acceptCharset(StandardCharsets.UTF_8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(CourseResponseDTO.class).value((list) -> {
                    assertNotNull(list);
                    assertEquals(DB_SIZE, list.size());});
    }

    @Test
    public void getCourseByCourseId_withValidCourseID(){
        webTestClient.get()
                .uri("/courses/{courseId}", course1.getCourseId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.courseId").isEqualTo(course1.getCourseId());
    }

    @Test
    public void getCourseByCourseId_withInvalidCourseId_throwsNotFoundException(){
        UUID uuidTest= UUID.randomUUID();
        webTestClient.get()
                .uri("/courses/{courseId}", uuidTest)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Course with this Id wasn't found: " + uuidTest);
    }

    @Test
    public void getCourseByCourseId_withInvalidCourseId_throwsInvalidInputException(){
        String invalidIdTest = "12345";
        webTestClient.get()
                .uri("/courses/{courseId}", invalidIdTest)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("The course ID needs to be 36 characters: " + invalidIdTest);
    }

    @Test
    public void addNewCourseWithValidValues_ShouldSucceed(){
        CourseRequestDTO courseRequestDTO = CourseRequestDTO.builder()
                .courseName("DATABASE")
                .courseNumber("420-NA")
                .numHours(80)
                .numCredits(3.0)
                .department("COMPUTER SCIENCE")
                .build();

        webTestClient.post()
                .uri("/courses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(courseRequestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseDTO.class)
                .value(courseResponseDTO -> {
                    assertNotNull(courseResponseDTO);
                    assertNotNull(courseResponseDTO.getCourseId());
                    assertThat(courseResponseDTO.getCourseName()).isEqualTo(courseResponseDTO.getCourseName());
                    assertThat(courseResponseDTO.getCourseNumber()).isEqualTo(courseResponseDTO.getCourseNumber());
                    assertThat(courseResponseDTO.getDepartment()).isEqualTo(courseResponseDTO.getDepartment());
                });
    }

    @Test
    public void updateCourse_withValidId() {
        String validCourseNumber = "12345";

        CourseRequestDTO courseRequestDTO = CourseRequestDTO.builder()
                .courseName(course2.getCourseName())
                .courseNumber(validCourseNumber)
                .department(course2.getDepartment()).build();

        webTestClient.put()
                .uri("/courses/{courseId}", course2.getCourseId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(courseRequestDTO)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(CourseResponseDTO.class)
                .value((courseResponseDTO -> {
                    assertNotNull(courseResponseDTO);
                    assertThat(courseResponseDTO.getCourseId()).isEqualTo(course2.getCourseId());
                    assertThat(courseResponseDTO.getCourseName()).isEqualTo(course2.getCourseName());
                    assertThat(courseResponseDTO.getCourseNumber()).isEqualTo(validCourseNumber);
                    assertThat(courseResponseDTO.getDepartment()).isEqualTo(course2.getDepartment());
                }));;
    }

    @Test
    public void deleteCourse_withValidId(){
        webTestClient.delete()
                .uri("/courses/{courseId}", course1.getCourseId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void deleteCourse_throwsInvalidInputException(){
        String invalidIdTest = "12345";
        webTestClient.delete()
                .uri("/courses/{courseId}",invalidIdTest)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("The course ID needs to be 36 characters: " + invalidIdTest);
    }

    private Course buildCourse(String courseName, String courseId){
        return Course.builder()
                .courseName(courseName)
                .department("COMPUTER SCIENCE")
                .courseId(courseId)
                .build();
    }

}