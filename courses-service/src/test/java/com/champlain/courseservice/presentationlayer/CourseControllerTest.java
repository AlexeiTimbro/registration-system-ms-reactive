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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
@AutoConfigureWebTestClient
class CourseControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    CourseRepository courseRepository;

    private final Long DB_SIZE =4L;
    Course course1= buildCourse(".net programming", "courseId_1");
    Course course2= buildCourse("Intro to English", "courseId_2");
    Course course3= buildCourse("Photoshop", "courseId_3");
    Course course4= buildCourse("Php", "courseId_4");



    @BeforeEach
    public void dbSetup(){
        Course course1= buildCourse(".net programming", "courseId_1");
        Course course2= buildCourse("Intro to English", "courseId_2");
        Course course3= buildCourse("Photoshop", "courseId_3");
        Course course4= buildCourse("Php", "courseId_4");

        Publisher<Course> setup = courseRepository.deleteAll()
                .thenMany(courseRepository.save(course1))
                .thenMany(courseRepository.save(course2))
                .thenMany(courseRepository.save(course3))
                .thenMany(courseRepository.save(course4));

        StepVerifier
                .create(setup)
                .expectNextCount(1)
                .verifyComplete();
    }


    @Test
    void getAllCourses_expect4(){
        webTestClient
                .get()
                .uri("/courses")
                .accept(MediaType.valueOf(TEXT_EVENT_STREAM_VALUE))
                .acceptCharset(StandardCharsets.UTF_8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(CourseResponseDTO.class)
                .value((list)->{
                    assertNotNull(list);
                    assertEquals(DB_SIZE, list.size());

                });



    }


    @Test
    public void getCourseByCourseId_withValidCourseId(){
        webTestClient.get()
                .uri("/courses/{courseId}" , course1.getCourseId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.courseId").isEqualTo(course1.getCourseId());
    }

    @Test
    void getCourseByCourseIdString_withInvalidCourseId_throwsNotFoundException(){
        String invalidCourseId= "123";

        webTestClient
                .get()
                .uri("/courses/{courseId}", invalidCourseId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Course with this Id wasn't found: " + invalidCourseId);
    }

    @Test
    public void addNewCourseWithValidValues_shouldSucceed(){
        CourseRequestDTO courseRequestDTO = CourseRequestDTO.builder()
                .courseName("Java one")
                .courseNumber("9908")
                .numHours(987)
                .numCredits(2.0)
                .department("CompSci")
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


                });


    }

    @Test
    public void updateCourse_withValidId() {
        String validCourseName = ".net programming";

        CourseRequestDTO courseRequestDTO = CourseRequestDTO.builder()

                .courseName(validCourseName)
                .courseNumber(course2.getCourseNumber())
                .numHours(course2.getNumHours())
                .numCredits(course2.getNumCredits())
                .department(course2.getDepartment()).build();



        webTestClient.put()
                .uri("/courses/{courseId}",course2.getCourseId())
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
                    assertThat(courseResponseDTO.getCourseName()).isEqualTo(validCourseName);
                    assertThat(courseResponseDTO.getCourseNumber()).isEqualTo(course2.getCourseNumber());
                    assertThat(courseResponseDTO.getNumCredits()).isEqualTo(course2.getNumCredits());
                    assertThat(courseResponseDTO.getDepartment()).isEqualTo(course2.getDepartment());
                    assertThat(courseResponseDTO.getNumHours()).isEqualTo(course2.getNumHours());


                }));;
    }


    @Test
    public void updateCourse_withInvalidId() {
        String validLastName = "Bedard";
        String courseId="9870";

        CourseRequestDTO courseRequestDTO = CourseRequestDTO.builder()
                .courseNumber(course2.getCourseNumber())
                .courseName(validLastName)
                .numHours(course2.getNumHours())
                .numHours(course2.getNumHours())
                .numCredits(course2.getNumCredits())
                .department(course2.getDepartment()).build();

        webTestClient.put()
                .uri("/courses/{courseId}",courseId)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(courseRequestDTO)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody();

    }

    @Test
    public void whenDeleteCourse_thenDeleteCourse() {


        webTestClient.delete()
                .uri("/courses/{courseId}",course2.getCourseId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();

    }

    @Test
    public void whenDeleteCourse_WithInvalidIdThrowNotFoundException() {

        String invalidId= "143";
        webTestClient.delete()
                .uri("/courses/{courseId}", invalidId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Course with this Id wasn't found: " + invalidId);

    }



    private Course buildCourse(String courseName, String courseId){
        return Course.builder()
                .courseId(courseId)
                .courseNumber("9807")
                .courseName("Java Web")
                .numCredits(76.0)
                .numHours(6)
                .department("Comp Sci")
                .build();
    }



}