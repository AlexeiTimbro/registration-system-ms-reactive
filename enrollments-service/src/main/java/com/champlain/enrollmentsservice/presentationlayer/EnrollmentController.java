package com.champlain.enrollmentsservice.presentationlayer;

import com.champlain.enrollmentsservice.businesslayer.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("enrollments")
@Slf4j
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping(value = "", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<EnrollmentResponseDTO> getAllEnrollments(@RequestParam  Map<String, String> queryParams){
        return enrollmentService.getAllEnrollments(queryParams);
    }

    @GetMapping("/{enrollmentId}")
    public Mono<ResponseEntity<EnrollmentResponseDTO>> getEnrollmentById(@PathVariable String enrollmentId){
        return enrollmentService.getEnrollmentById(enrollmentId)
                .map(e -> ResponseEntity.ok().body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping()
    public Mono<ResponseEntity<EnrollmentResponseDTO>> addEnrollment(@RequestBody Mono<EnrollmentRequestDTO> enrollmentRequestDTOMono){
        return enrollmentService.addEnrollment(enrollmentRequestDTOMono)
                .map(e -> ResponseEntity.status(HttpStatus.CREATED).body(e))
                .doOnNext(i -> System.out.println(("Created new enrollment")))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{enrollmentId}")
    public Mono<ResponseEntity<EnrollmentResponseDTO>> updateEnrollment(@RequestBody Mono<EnrollmentRequestDTO> enrollmentRequestDTO,
                                                                        @PathVariable String enrollmentId){
        return enrollmentService.updateEnrollment(enrollmentRequestDTO, enrollmentId)
                .map(ResponseEntity::ok)
                .doOnNext(i -> System.out.println("Updated enrollment"))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{enrollmentId}")
    public Mono<ResponseEntity<Void>> deleteEnrollmentById(@PathVariable String enrollmentId){
        return enrollmentService.deleteEnrollmentById(enrollmentId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


}




