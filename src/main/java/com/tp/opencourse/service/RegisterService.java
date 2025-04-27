package com.tp.opencourse.service;

import com.tp.opencourse.dto.response.LearningResponse;
import com.tp.opencourse.dto.response.RegisterResponse;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface RegisterService {

    Map<String, String> registerCourses(List<String> courseIds);
    List<RegisterResponse> findAllRegisteredCourses(String status);
    List<LearningResponse> findAllLearnings();

}
