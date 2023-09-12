package com.champlain.studentsservice.businesslayer;

import com.champlain.studentsservice.dataaccesslayer.Student;
import com.champlain.studentsservice.dataaccesslayer.StudentRepository;
import com.champlain.studentsservice.presentationlayer.StudentRequestDTO;
import com.champlain.studentsservice.presentationlayer.StudentResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class StudentServiceUnitTest {

    @Autowired
    StudentService studentService;

    @MockBean
    private StudentRepository studentRepository;

    StudentResponseDTO studentResponseDTO = StudentResponseDTO.builder()
            .studentId("c2db7b50-26b5-43f0-ab03-8dc5dab937fb")
            .firstName("John")
            .lastName("low")
            .program("History")
            .build();

    Student student = Student.builder()
            .studentId("c2db7b50-26b5-43f0-ab03-8dc5dab937fb")
            .firstName("John")
            .lastName("low")
            .program("History")
            .build();


    @Test
    void getAllStudents_ValidId_shouldSucceed() {
        // Arrange
        when(studentRepository.findAll())
                .thenReturn(Flux.just(student));

        // Act
        Flux<StudentResponseDTO> studentResponseDTOFlux = studentService
                .getAllStudents();

        // Assert
        StepVerifier
                .create(studentResponseDTOFlux)
                .consumeNextWith(foundStudent -> {
                    assertNotNull(foundStudent);
                    assertEquals(student.getStudentId(), foundStudent.getStudentId());
                    assertEquals(student.getFirstName(), foundStudent.getFirstName());
                    assertEquals(student.getLastName(), foundStudent.getLastName());
                    assertEquals(student.getProgram(), foundStudent.getProgram());
                })
                .verifyComplete();
    }

    @Test
    void getStudentByStudentId_ValidId_shouldSucceed(){
        //arrange
        when(studentRepository.findStudentByStudentId(anyString()))
                .thenReturn(Mono.just(student));

        //act
        Mono<StudentResponseDTO> studentResponseDTOMono = studentService
                .getStudentById(student.getStudentId());

        //assert
        StepVerifier
                .create(studentResponseDTOMono)
                .consumeNextWith(foundStudent ->{
                    assertNotNull(foundStudent);
                    assertEquals(student.getStudentId(), foundStudent.getStudentId());
                    assertEquals(student.getFirstName(), foundStudent.getFirstName());
                    assertEquals(student.getLastName(), foundStudent.getLastName());
                    assertEquals(student.getProgram(), foundStudent.getProgram());
                })
                .verifyComplete();
    }
    @Test
    void addStudent_ValidCourse_shouldSucceed() {
        // Arrange
        StudentRequestDTO studentRequestDTO = StudentRequestDTO.builder()
                .firstName("John")
                .lastName("low")
                .program("History")
                .build();

        Student student = Student.builder()
                .studentId("c2db7b50-26b5-43f0-ab03-8dc5dab937fb")
                .firstName("John")
                .lastName("low")
                .program("History")
                .build();

        when(studentRepository.insert(any(Student.class)))
                .thenReturn(Mono.just(student));

        // Act
        Mono<StudentResponseDTO> studentResponseDTOMono = studentService.addStudent(Mono.just(studentRequestDTO));

        // Assert
        StepVerifier
                .create(studentResponseDTOMono)
                .expectNextMatches(foundStudent -> {
                    assertNotNull(foundStudent);
                    assertEquals(student.getStudentId(), foundStudent.getStudentId());
                    assertEquals(student.getFirstName(), foundStudent.getFirstName());
                    assertEquals(student.getLastName(), foundStudent.getLastName());
                    assertEquals(student.getProgram(), foundStudent.getProgram());

                    return true;
                })
                .verifyComplete();
    }

    @Test
    void updateStudentById_ValidStudentIdAndRequest_ShouldUpdateAndReturnStudent() {
        // Arrange
        String validStudentId = "c2db7b50-26b5-43f0-ab03-8dc5dab937fb";
        StudentRequestDTO studentRequestDTO = StudentRequestDTO.builder()
                .build();

        Student existingStudent = Student.builder()
                .studentId("c2db7b50-26b5-43f0-ab03-8dc5dab937fb")
                .id("12345")
                .build();

        Student updatedStudentEntity = Student.builder()
                .build();

        when(studentRepository.findStudentByStudentId(validStudentId)).thenReturn(Mono.just(existingStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(Mono.just(updatedStudentEntity));

        // Act and Assert
        StepVerifier
                .create(studentService.updateStudentById(Mono.just(studentRequestDTO), validStudentId))
                .expectNextMatches(updatedStudent -> {
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void removeStudent_ValidStudentId_shouldSucceed() {
        // Arrange
        String studentIdToDelete = "c2db7b50-26b5-43f0-ab03-8dc5dab937fb";

        when(studentRepository.findStudentByStudentId(studentIdToDelete))
                .thenReturn(Mono.just(new Student()));

        when(studentRepository.deleteById(anyString()))
                .thenReturn(Mono.empty());

        // Act
        Mono<Void> deletionMono = studentService.deleteStudentById(studentIdToDelete);

        // Assert
        StepVerifier
                .create(deletionMono)
                .verifyComplete();
    }

}