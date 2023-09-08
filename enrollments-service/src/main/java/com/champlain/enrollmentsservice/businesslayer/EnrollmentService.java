package com.champlain.enrollmentsservice.businesslayer;

import com.champlain.enrollmentsservice.presentationlayer.EnrollmentResponseDTO;
import com.champlain.enrollmentsservice.presentationlayer.EnrollmentRequestDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface EnrollmentService {

    Flux<EnrollmentResponseDTO> getAllEnrollments(Map<String, String> queryParams);
    Mono<EnrollmentResponseDTO> getEnrollmentById(String enrollmentId);Mono<EnrollmentResponseDTO> addEnrollment(Mono<EnrollmentRequestDTO> enrollmentRequestDTO);
    Mono<EnrollmentResponseDTO> updateEnrollment(Mono<EnrollmentRequestDTO> enrollmentRequestDTO, String enrollmentId);
    Mono<Void> deleteEnrollmentById(String enrollmentId);
}
