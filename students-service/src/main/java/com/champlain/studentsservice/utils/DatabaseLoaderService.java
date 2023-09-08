package com.champlain.studentsservice.utils;

import com.champlain.studentsservice.dataaccesslayer.Student;
import com.champlain.studentsservice.dataaccesslayer.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class DatabaseLoaderService implements CommandLineRunner {

    @Autowired
    StudentRepository studentRepository;

    @Override
    public void run(String... args) throws Exception{
        Student student1 = Student
                .builder()
                .studentId(UUID.randomUUID().toString())
                .firstName("Lebron")
                .lastName("James")
                .program("Political Science")
                .build();


        Student student2 = Student
                .builder()
                .studentId(UUID.randomUUID().toString())
                .firstName("Erling")
                .lastName("Haaland")
                .program("Political Science")
                .build();

        Student student3 = Student
                .builder()
                .studentId(UUID.randomUUID().toString())
                .firstName("Mike")
                .lastName("Tyson")
                .program("Political Science")
                .build();

        Student student4 = Student
                .builder()
                .studentId(UUID.randomUUID().toString())
                .firstName("Micheal")
                .lastName("Jordan")
                .program("Political Science")
                .build();


        Flux.just(student1, student2, student3, student4)
                .flatMap(s -> studentRepository.insert(Mono.just(s))
                .log(s.toString())).subscribe();


    }

}
