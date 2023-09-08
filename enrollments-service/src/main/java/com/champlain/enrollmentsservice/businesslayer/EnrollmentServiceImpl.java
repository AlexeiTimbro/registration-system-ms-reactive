package com.champlain.enrollmentsservice.businesslayer;

import com.champlain.enrollmentsservice.dataaccesslayer.Enrollment;
import com.champlain.enrollmentsservice.dataaccesslayer.EnrollmentRepository;
import com.champlain.enrollmentsservice.domainclientlayer.CourseClient;
import com.champlain.enrollmentsservice.domainclientlayer.StudentClient;
import com.champlain.enrollmentsservice.presentationlayer.EnrollmentRequestDTO;
import com.champlain.enrollmentsservice.presentationlayer.EnrollmentResponseDTO;
import com.champlain.enrollmentsservice.utils.EntityDTOUtils;
import com.champlain.enrollmentsservice.utils.exceptions.InvalidInputException;
import com.champlain.enrollmentsservice.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentClient studentClient;
    private final CourseClient courseClient;

    @Override
    public Flux<EnrollmentResponseDTO> getAllEnrollments(Map<String, String> queryParams) {
        Flux<Enrollment> enrollmentFlux = enrollmentRepository.findAll();

        String studentId = queryParams.get("studentId");
        String courseId = queryParams.get("courseId");
        String enrollmentYearParam = queryParams.get("enrollmentYear");

        Integer enrollmentYear = null;
        boolean invalidYear = false;

        if (enrollmentYearParam != null) {
            try {
                enrollmentYear = Integer.valueOf(enrollmentYearParam);
            } catch (NumberFormatException e) {
                invalidYear = true;
            }
        }
        if (invalidYear) {
            return Flux.error(new InvalidInputException("Invalid year"));
        }

        if (studentId != null) { enrollmentFlux = enrollmentFlux.filter(enrollments -> enrollments.getStudentId().equals(studentId)); }
        if (courseId != null) { enrollmentFlux = enrollmentFlux.filter(enrollments -> enrollments.getCourseId().equals(courseId)); }

        if (enrollmentYear != null) {
            final Integer finalEnrollmentYear = enrollmentYear;
            enrollmentFlux = enrollmentFlux.filter(enrollments -> enrollments.getEnrollmentYear().equals(finalEnrollmentYear));
        }
        return enrollmentFlux.map(EntityDTOUtils::toEnrollmentResponseDTO);
    }


    @Override
    public Mono<EnrollmentResponseDTO> getEnrollmentById(String enrollmentId) {

        if(enrollmentId.length() != 36){
            return Mono.error(new InvalidInputException("The enrollment ID needs to be 36 characters: " + enrollmentId));
        }

        return enrollmentRepository.findEnrollmentByEnrollmentId(enrollmentId)
                .switchIfEmpty(Mono.error(new NotFoundException("No enrollment with this enrollmentId was found: " + enrollmentId)))
                .map(EntityDTOUtils::toEnrollmentResponseDTO);
    }

    @Override
    public Mono<EnrollmentResponseDTO> addEnrollment(Mono<EnrollmentRequestDTO> enrollmentRequestDTO) {
        return enrollmentRequestDTO
                .map(RequestContextAdd::new)
                .flatMap(this::studentRequestResponse)
                .flatMap(this::courseRequestResponse)
                .map(EntityDTOUtils::toEnrollmentEntity)
                .map(enrollmentRepository::save)
                .flatMap(entity -> entity)
                .map(EntityDTOUtils::toEnrollmentResponseDTO);

    }

    @Override
    public Mono<EnrollmentResponseDTO> updateEnrollment(Mono<EnrollmentRequestDTO> enrollmentRequestDTO, String enrollmentId) {

        if(enrollmentId.length() != 36){
            return Mono.error(new InvalidInputException("The enrollment ID needs to be 36 characters: " + enrollmentId));
        }

        return enrollmentRequestDTO
                .switchIfEmpty(Mono.error(new NotFoundException("No enrollment with this enrollmentId was found: " + enrollmentId)))
                .flatMap(updateEnrollmentRequestDTO -> {

                    return enrollmentRepository.findEnrollmentByEnrollmentId(enrollmentId)
                            .flatMap(existingEnrollment -> {
                                RequestContextUpdate rcu = new RequestContextUpdate(updateEnrollmentRequestDTO, enrollmentId);
                                rcu.setEnrollment(existingEnrollment);
                                return Mono.just(rcu);
                            });
                })
                .flatMap(this::updatedStudentRequestResponse)
                .flatMap(this::updatedCourseRequestResponse)
                .map(EntityDTOUtils::toUpdatedEnrollmentEntity)
                .map(enrollmentRepository::save)
                .flatMap(entity -> entity)
                .map(EntityDTOUtils::toEnrollmentResponseDTO);
    }

    @Override
    public Mono<Void> deleteEnrollmentById(String enrollmentId) {

        if(enrollmentId.length() != 36){
            return Mono.error(new InvalidInputException("The enrollment ID needs to be 36 characters: " + enrollmentId));
        }

        return enrollmentRepository.findEnrollmentByEnrollmentId(enrollmentId)
                .switchIfEmpty(Mono.error(new NotFoundException("No enrollment with this enrollmentId was found: " + enrollmentId)))
                .flatMap(enrollmentRepository::delete);
    }


    private Mono<RequestContextAdd> courseRequestResponse(RequestContextAdd rc) {
        return this.courseClient.getCourseByCourseId(rc.getEnrollmentRequestDTO().getCourseId())
                .doOnNext(rc::setCourseResponseDTO)
                .thenReturn(rc);
    }

    private Mono<RequestContextAdd> studentRequestResponse(RequestContextAdd rc){
        return this.studentClient.getStudentByStudentId(rc.getEnrollmentRequestDTO().getStudentId())
                .doOnNext(rc::setStudentResponseDTO)
                .thenReturn(rc);
    }

    private Mono<RequestContextUpdate> updatedCourseRequestResponse(RequestContextUpdate rcu){
        return this.courseClient.getCourseByCourseId(rcu.getEnrollmentRequestDTO().getCourseId())
                .doOnNext(rcu::setCourseResponseDTO)
                .thenReturn(rcu);
    }

    private Mono<RequestContextUpdate> updatedStudentRequestResponse(RequestContextUpdate rcu){
        return this.studentClient.getStudentByStudentId(rcu.getEnrollmentRequestDTO().getStudentId())
                .doOnNext(rcu::setStudentResponseDTO)
                .thenReturn(rcu);
    }


}