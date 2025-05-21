package com.tp.opencourse.service;

import com.tp.opencourse.dto.response.CourseAdminResponse;
import com.tp.opencourse.dto.response.PeriodStatisticResponse;

import java.util.List;

public interface StatService {

    Object[] getOverview();
    List<PeriodStatisticResponse> getStatisticsByPeriod(String year, String periodType);
    List<CourseAdminResponse> getCourseStatistic(String page, String size);
}
