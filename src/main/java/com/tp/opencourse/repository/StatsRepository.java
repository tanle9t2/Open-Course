package com.tp.opencourse.repository;

import java.util.List;

public interface StatsRepository {
    List<Object[]> getStatisticsByPeriod(String year, String periodType);
    List<Object[]> getCourseRevenuStatistics(Integer offset);

}
