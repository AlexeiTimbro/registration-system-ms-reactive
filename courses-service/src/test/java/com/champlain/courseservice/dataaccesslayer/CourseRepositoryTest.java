package com.champlain.courseservice.dataaccesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class CourseRepositoryTest {

    @Autowired
    CourseRepository courseRepository;

    Course course1;
    Course course2;

    @BeforeEach
    public void setupDB(){

        course1 = buildCourse("Web Services", "courseId_1");
        Publisher<Course> setup1 = courseRepository.deleteAll()
                .thenMany(courseRepository.save(course1));

        StepVerifier
                .create(setup1)
                .expectNextCount(1)
                .verifyComplete();

        course2 = buildCourse("Database", "studentId_2");
        Publisher<Course> setup2 = courseRepository.save(course2);

        StepVerifier
                .create(setup2)
                .expectNextCount(1)
                .verifyComplete();

    }

    @Test
    public void shouldSaveSingleCourse(){
        //arrange
        Course newCourse = buildCourse("Database", "studentId");
        Publisher<Course> setup = courseRepository.save(newCourse);

        //act and assert
        StepVerifier
                .create(setup)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void shouldGetAllCourses(){
        //arrange
        StepVerifier
                .create(courseRepository.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void findCourseByValidCourseId_shouldFindOne(){

        StepVerifier
                .create(courseRepository.findCourseByCourseId(course1.getCourseId()))
                .assertNext(course -> {
                    assertThat(course.getCourseName().equals(course1.getCourseName()));
                    assertThat(course.getCourseId()).isEqualTo(course1.getCourseId());
                })
                .verifyComplete();
    }

    @Test
    public void findCourseByCourseId_NoneReturned(){

        StepVerifier
                .create(courseRepository.findCourseByCourseId("123"))
                .expectNextCount(0)
                .verifyComplete();
    }


    private Course buildCourse(String courseName, String courseId){
        return Course.builder()
                .courseId(courseId)
                .courseNumber("420-NA")
                .courseName(courseName)
                .numHours(90)
                .numCredits(4.0)
                .department("Computer Science")
                .build();
    }
}