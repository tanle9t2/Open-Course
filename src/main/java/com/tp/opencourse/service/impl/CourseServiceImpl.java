package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.CourseDTO;
import com.tp.opencourse.dto.response.CourseResponse;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.CourseMapper;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.service.CourseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Override
    public CourseDTO findById(String id) {
        return null;
    }

    @Override
    @Transactional
    public List<CourseResponse> findByIds(List<String> courseIds) {
        List<Course> courseResponses = courseRepository.findAllByIds(new HashSet<>(courseIds));
        if(courseResponses.size() != courseIds.size()) {
            throw new ResourceNotFoundExeption("Invalid course ids");
        }

        return courseResponses.stream().map(courseMapper::convertEntityToResponse)
                .collect(Collectors.toList());
    }
}
