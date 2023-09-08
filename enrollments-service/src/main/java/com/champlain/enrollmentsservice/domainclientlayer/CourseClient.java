package com.champlain.enrollmentsservice.domainclientlayer;



import com.champlain.enrollmentsservice.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class CourseClient {

        private final WebClient webClient;
        private final String courseClientServiceBaseURL;


    public CourseClient(@Value("${app.courses-service.host}") String coursesServiceHost,
                        @Value("${app.courses-service.port}") String coursesServicePort){

        courseClientServiceBaseURL = "http://" + coursesServiceHost + ":" + coursesServicePort + "/courses";

        this.webClient = WebClient.builder()
                .baseUrl(courseClientServiceBaseURL)
                .build();

    }

    public Mono<CourseResponseDTO> getCourseByCourseId(final String courseId){
            return this.webClient
                    .get()
                    .uri("/{courseId}", courseId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, error -> {
                        HttpStatusCode statusCode = error.statusCode();
                        if(statusCode.equals(HttpStatus.NOT_FOUND))
                            return Mono.error(new NotFoundException("CourseId not found: " + courseId));
                        return Mono.error(new IllegalArgumentException("Something went wrong"));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, error ->
                            Mono.error(new IllegalArgumentException("Something went wrong"))
                    )
                    .bodyToMono(CourseResponseDTO.class);
        }

        public static String generateUUIDString(){
            return UUID.randomUUID().toString();
        }
    }

