package com.champlain.enrollmentsservice.domainclientlayer;


import com.champlain.enrollmentsservice.utils.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class StudentClient {

    private final WebClient webClient;
    private final String studentClientServiceBaseURL;

    public StudentClient(@Value("${app.students-service.host}") String studentServiceHost,
                         @Value("${app.students-service.port}") String studentServicePort){

        studentClientServiceBaseURL = "http://" + studentServiceHost + ":" + studentServicePort + "/students";

        this.webClient = WebClient.builder()
                .baseUrl(studentClientServiceBaseURL)
                .build();

    }

    public Mono<StudentResponseDTO> getStudentByStudentId(final String studentId){
        return this.webClient
                .get()
                .uri("/{studentId}", studentId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, error -> {
                    HttpStatusCode statusCode = error.statusCode();
                    if(statusCode.equals(HttpStatus.NOT_FOUND))
                        return Mono.error(new NotFoundException("StudentId not found: " + studentId));
                    return Mono.error(new IllegalArgumentException("Something went wrong"));
                })
                .onStatus(HttpStatusCode::is5xxServerError, error ->
                     Mono.error(new IllegalArgumentException("Something went wrong"))
                )
                .bodyToMono(StudentResponseDTO.class);
    }

}
