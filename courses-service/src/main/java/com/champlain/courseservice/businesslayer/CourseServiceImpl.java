package com.champlain.courseservice.businesslayer;

import com.champlain.courseservice.dataaccesslayer.CourseRepository;
import com.champlain.courseservice.presentationlayer.CourseRequestDTO;
import com.champlain.courseservice.presentationlayer.CourseResponseDTO;
import com.champlain.courseservice.utils.EntityDTOUtils;
import com.champlain.courseservice.utils.exceptions.InvalidInputException;
import com.champlain.courseservice.utils.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService{

    private final CourseRepository courseRepository;

    @Override
    public Flux<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll()
                .map(EntityDTOUtils::toCourseResponseDTO);
    }

    @Override
    public Mono<CourseResponseDTO> getCourseById(String courseId) {


        if(courseId.length() != 36){
            return Mono.error(new InvalidInputException("The course ID needs to be 36 characters: " + courseId));
        }

        return courseRepository.findCourseByCourseId(courseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Course with this Id wasn't found: " + courseId)))
                .map(EntityDTOUtils::toCourseResponseDTO);
    }

    @Override
    public Mono<CourseResponseDTO> addCourse(Mono<CourseRequestDTO> courseRequestDTO) {
        return courseRequestDTO
                .map(EntityDTOUtils::toCourseEntity)
                .doOnNext(e -> e.setCourseId(EntityDTOUtils.generateUUIDString()))
                .flatMap(courseRepository::insert)
                .map(EntityDTOUtils::toCourseResponseDTO);
    }

    @Override
    public Mono<CourseResponseDTO> updateCourse(Mono<CourseRequestDTO> courseRequestDTO, String courseId) {

        if(courseId.length() != 36){
            return Mono.error(new InvalidInputException("The course ID needs to be 36 characters: " + courseId));
        }

        return courseRepository.findCourseByCourseId(courseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Course with this Id wasn't found: " + courseId)))
                .flatMap(course ->
            courseRequestDTO
                    .map(EntityDTOUtils::toCourseEntity)
                    .doOnNext(e -> {
                        e.setCourseId(course.getCourseId());
                        e.setId(course.getId());

        }))
                .flatMap(courseRepository::save)
                .map(EntityDTOUtils::toCourseResponseDTO);
    }

    @Override
    public Mono<Void> removeCourse(String courseId) {

        if(courseId.length() != 36){
            return Mono.error(new InvalidInputException("The course ID needs to be 36 characters: " + courseId));
        }

        return courseRepository.findCourseByCourseId(courseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Course with this Id wasn't found: " + courseId)))
                .flatMap(course -> {
                    courseRepository.delete(course);
                    return Mono.empty();
                });
    }

}

