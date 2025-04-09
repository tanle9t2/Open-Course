package com.tp.opencourse.service;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface RegisterService {

    void registerCourses(List<String> courseIds);

}
