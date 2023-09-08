package com.champlain.studentsservice.dataaccesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class StudentRepositoryTest {

    @Autowired
    StudentRepository studentRepository;

    Student student1;
    Student student2;
    @BeforeEach
    public void setupDB(){

        student1 = buildStudent("Smith", "studentId_1");
        Publisher<Student> setup1 = studentRepository.deleteAll()
                .thenMany(studentRepository.save(student1));

        StepVerifier
                .create(setup1)
                .expectNextCount(1)
                .verifyComplete();

        student2 = buildStudent("Tang", "studentId_2");
        Publisher<Student> setup2 = studentRepository.save(student2);

        StepVerifier
                .create(setup2)
                .expectNextCount(1)
                .verifyComplete();

    }

    @Test
    public void shouldSaveSingleStudent(){
        //arrange
        Student newStudent = buildStudent("Micheal", "studentId_3");
        Publisher<Student> setup = studentRepository.save(newStudent);

        //act and assert
        StepVerifier
                .create(setup)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void shouldGetAllStudents(){
        //arrange
        StepVerifier
                .create(studentRepository.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void findStudentByValidStudentId_shouldFindOne(){

        StepVerifier
                .create(studentRepository.findStudentByStudentId(student1.getStudentId()))
                .assertNext(student -> {
                    assertThat(student.getLastName().equals(student1.getLastName()));
                    assertThat(student.getStudentId()).isEqualTo(student1.getStudentId());
                })
                .verifyComplete();
    }

    @Test
    public void findStudentByStudentId_NoneReturned(){

        StepVerifier
                .create(studentRepository.findStudentByStudentId("123"))
                .expectNextCount(0)
                .verifyComplete();
    }

    private Student buildStudent(String lastName, String studentId){
        return Student.builder()
                .studentId(studentId)
                .firstName("Mary")
                .lastName(lastName)
                .program("CompSci")
                .build();
    }
}