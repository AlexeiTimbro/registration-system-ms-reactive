package com.champlain.courseservice.utils;


import com.champlain.courseservice.dataaccesslayer.Course;
import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class DatabaseLoaderService implements CommandLineRunner {

    @Autowired
    CourseRepository courseRepository;

    @Override
    public void run(String... args) throws Exception{
        Course course1 = Course
                .builder()
                .courseId(UUID.randomUUID().toString())
                .courseNumber("123456")
                .courseName("NAVY SEALS")
                .department("THE DEPARTMENT")
                .numHours(80)
                .numCredits(8.00)
                .build();

        Course course2 = Course
                .builder()
                .courseId(UUID.randomUUID().toString())
                .courseNumber("234567")
                .courseName("THE COURSE")
                .department("THE DEPARTMENT")
                .numHours(50)
                .numCredits(6.00)
                .build();

        Course course3 = Course
                .builder()
                .courseId(UUID.randomUUID().toString())
                .courseNumber("345678")
                .courseName("MARINE CORP")
                .department("THE DEPARTMENT")
                .numHours(50)
                .numCredits(6.00)
                .build();

        Course course4 = Course
                .builder()
                .courseId(UUID.randomUUID().toString())
                .courseNumber("456789")
                .courseName("JAVA WEB")
                .department("COMPUTER SCIENCE")
                .numHours(50)
                .numCredits(6.00)
                .build();


        Flux.just(course1, course2, course3, course4)
                .flatMap(courseRepository::insert)
                .log()
                .subscribe();


    }

}
