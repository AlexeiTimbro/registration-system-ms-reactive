package com.champlain.enrollmentsservice.utils;

import com.champlain.enrollmentsservice.businesslayer.RequestContextAdd;
import com.champlain.enrollmentsservice.businesslayer.RequestContextUpdate;
import com.champlain.enrollmentsservice.dataaccesslayer.Enrollment;
import com.champlain.enrollmentsservice.presentationlayer.EnrollmentResponseDTO;

import org.springframework.beans.BeanUtils;

import java.util.UUID;

public class EntityDTOUtils {


        public static EnrollmentResponseDTO toEnrollmentResponseDTO(Enrollment enrollment){
            EnrollmentResponseDTO enrollmentResponseDTO = new EnrollmentResponseDTO();
            BeanUtils.copyProperties(enrollment, enrollmentResponseDTO);
            return enrollmentResponseDTO;

        }

        public static Enrollment toEnrollmentEntity(RequestContextAdd rc){
            return Enrollment.builder()
                    .enrollmentId(generateUUIDString())
                    .courseId(rc.getCourseResponseDTO().getCourseId())
                    .studentId(rc.getStudentResponseDTO().getStudentId())
                    .enrollmentYear(rc.getEnrollmentRequestDTO().getEnrollmentYear())
                    .courseNumber(rc.getCourseResponseDTO().getCourseNumber())
                    .courseName(rc.getCourseResponseDTO().getCourseName())
                    .semester(rc.getEnrollmentRequestDTO().getSemester())
                    .studentFirstName(rc.getStudentResponseDTO().getFirstName())
                    .studentLastName(rc.getStudentResponseDTO().getLastName())
                    .build();
        }

        public static Enrollment toUpdatedEnrollmentEntity(RequestContextUpdate rcu){
            Enrollment existingEnrollment = rcu.getEnrollment();
            return Enrollment.builder()
                    .enrollmentId(existingEnrollment.getEnrollmentId())
                    .courseId(rcu.getCourseResponseDTO().getCourseId())
                    .studentId(rcu.getStudentResponseDTO().getStudentId())
                    .enrollmentYear(rcu.getEnrollmentRequestDTO().getEnrollmentYear())
                    .courseNumber(rcu.getCourseResponseDTO().getCourseNumber())
                    .courseName(rcu.getCourseResponseDTO().getCourseName())
                    .semester(rcu.getEnrollmentRequestDTO().getSemester())
                    .studentFirstName(rcu.getStudentResponseDTO().getFirstName())
                    .studentLastName(rcu.getStudentResponseDTO().getLastName())
                    .id(existingEnrollment.getId())
                    .build();
        }
        public static String generateUUIDString(){
            return UUID.randomUUID().toString();

        }

}
