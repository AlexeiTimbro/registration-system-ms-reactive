package com.champlain.studentsservice.presentationlayer;

import com.champlain.studentsservice.dataaccesslayer.Student;
import com.champlain.studentsservice.dataaccesslayer.StudentRepository;
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
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
@AutoConfigureWebTestClient
class ControllerIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    StudentRepository studentRepository;

    private final Long DB_SIZE = 4L;

    Student student1 = buildStudent("Smith", "studentId_1");
    Student student2 = buildStudent("Jones", "studentId_2");
    Student student3 = buildStudent("Tremblay", "studentId_3");
    Student student4 = buildStudent("Chen", "studentId_4");

    @BeforeEach
    void dbSetup() {
        Student student1 = buildStudent("Smith", "studentId_1");
        Student student2 = buildStudent("Jones", "studentId_2");
        Student student3 = buildStudent("Tremblay", "studentId_3");
        Student student4 = buildStudent("Chen", "studentId_4");

        Publisher<Student> setup = studentRepository.deleteAll()
                .thenMany(studentRepository.save(student1))
                .thenMany(studentRepository.save(student2))
                .thenMany(studentRepository.save(student3))
                .thenMany(studentRepository.save(student4));

        StepVerifier
                .create(setup)
                .expectNextCount(1)
                .verifyComplete();

    }

    @Test
    void getAllStudents_expect4() {

        webTestClient
                .get()
                .uri("/students")
                .accept(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
                .acceptCharset(StandardCharsets.UTF_8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "text/event-stream;charset=UTF-8")
                .expectBodyList(StudentResponseDTO.class)
                .value((list) -> {
                    assertNotNull(list);
                    assertEquals(DB_SIZE, list.size());
                });
    }

    @Test
    public void getStudentByStudentId_withValidStudentId() {
        webTestClient
                .get()
                .uri("/students/{studentId}", student1.getStudentId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.studentId").isEqualTo(student1.getStudentId());

    }

    @Test
    void getStudentByStudentIdString_withInvalidStudentId_throwsNotFoundException() {
        String invalidStudentId = "123";

        webTestClient
                .get()
                .uri("/students/{studentId}", invalidStudentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("No student with this studentId was found: " + invalidStudentId);
    }

    @Test
    public void addStudentWithValidValues_ShouldSucceed() {
        StudentRequestDTO newStudent = StudentRequestDTO.builder()
                .firstName("Sandra")
                .lastName("Leduc")
                .program("Media")
                .build();

        webTestClient
                .post()
                .uri("/students")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newStudent)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(StudentResponseDTO.class)
                .value(studentResponseDTO -> {
                    assertNotNull(studentResponseDTO);
                    assertNotNull(studentResponseDTO.getStudentId());
                    assertThat(studentResponseDTO.getFirstName()).isEqualTo(newStudent.getFirstName());
                });
    }

    @Test
    public void updateStudent_withValidId() {
        String validLastName = "jose";

        StudentRequestDTO studentRequestDTO = StudentRequestDTO.builder()
                .firstName(student2.getFirstName())
                .lastName(validLastName)
                .program(student2.getProgram()).build();

        webTestClient.put()
                .uri("/students/{studentId}",student2.getStudentId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(studentRequestDTO)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(StudentResponseDTO.class)
                .value((studentResponseDTO -> {
                    assertNotNull(studentResponseDTO);
                    assertThat(studentResponseDTO.getStudentId()).isEqualTo(student2.getStudentId());
                    assertThat(studentResponseDTO.getFirstName()).isEqualTo(student2.getFirstName());
                    assertThat(studentResponseDTO.getLastName()).isEqualTo(validLastName);
                    assertThat(studentResponseDTO.getProgram()).isEqualTo(student2.getProgram());
                }));;
    }

    @Test
    public void updateStudent_withNotFoundId() {
        String validLastName = "jose";

        String studentId="123456";

        StudentRequestDTO studentRequestDTO = StudentRequestDTO.builder()
                .firstName(student2.getFirstName())
                .lastName(validLastName)
                .program(student2.getProgram()).build();

        webTestClient.put()
                .uri("/students/{studentId}",studentId)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(studentRequestDTO)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody();

    }


    @Test
    public void whenDeleteStudent_thenDeleteStudent() {


        webTestClient.delete()
                .uri("/students/{studentId}",student2.getStudentId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();

    }

    @Test
    public void whenDeleteStudent_WithInvalidIdThrowNotFoundException() {

        String invalidId= "123456";

        webTestClient.delete()
                .uri("/students/{studentId}", invalidId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Student with this Id wasn't found: " + invalidId);

    }


        private Student buildStudent (String lastName, String studentId){
            return Student.builder()
                    .studentId(studentId)
                    .firstName("Mary")
                    .lastName(lastName)
                    .program("CompSci")
                    .build();
        }

}
