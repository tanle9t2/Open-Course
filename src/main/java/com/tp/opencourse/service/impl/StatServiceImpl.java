package com.tp.opencourse.service.impl;

import com.tp.opencourse.design_pattern.stats.PeriodStrategy;
import com.tp.opencourse.design_pattern.stats.PeriodStrategyFactory;
import com.tp.opencourse.dto.Page;
import com.tp.opencourse.dto.response.CourseAdminResponse;
import com.tp.opencourse.dto.response.PeriodStatisticResponse;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.repository.RegisterRepository;
import com.tp.opencourse.repository.StatsRepository;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.service.StatService;
import com.tp.opencourse.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StatServiceImpl implements StatService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final RegisterRepository registerRepository;
    private final StatsRepository statsRepository;

    @Override
    public Object[] getOverview() {
        Long totalCoures = courseRepository.count();
        Long totalUsers = userRepository.count();
        Long totalRegistration = registerRepository.countTotalRegistration();
        Double totalRevenue = registerRepository.countTotalRevenue();

        return new Object[]{totalCoures, totalUsers, totalRegistration, totalRevenue};
    }

    @Override
    public List<PeriodStatisticResponse> getStatisticsByPeriod(String year, String periodType) {
        if (ValidationUtils.isNullOrEmpty(year)) {
            year = String.valueOf(LocalDate.now().getYear());
        }
        if (ValidationUtils.isNullOrEmpty(periodType)) {
            periodType = "Month";
        }
        List<Object[]> stats = statsRepository.getStatisticsByPeriod(year, periodType);

        List<PeriodStatisticResponse> periodStatisticResponses = new ArrayList<>();

        PeriodStrategy strategy = PeriodStrategyFactory.getStrategy(periodType);
        Map<Integer, String> labels = strategy.getLabels();
        Integer range = strategy.getRange();

        Map<Integer, Integer> values = new HashMap<>();
        for(int i = 1; i <= range; i++) {
            values.put(i, 0);
        }
        for (int i = 0; i < stats.size(); i++) {
            Integer period = ((Number)stats.get(i)[0]).intValue();
            Integer value = ((Number)stats.get(i)[1]).intValue();
            values.put(period, value);
        }
        for (int i = 1; i <= range; i++) {
            periodStatisticResponses.add(PeriodStatisticResponse
                    .builder()
                    .label(labels.get(i))
                    .value(values.get(i).toString())
                    .build());
        }

        return periodStatisticResponses;
    }

    @Override
    public List<CourseAdminResponse> getCourseStatistic(String page, String size) {
        Integer pageParam = !ValidationUtils.isNullOrEmpty(page) ? Integer.parseInt(page) : 1;
        Integer sizeParam = !ValidationUtils.isNullOrEmpty(size) ? Integer.parseInt(size) : 1;

        Page<Course> coursePages = courseRepository.findAll(null, pageParam, sizeParam, null, null);
        List<Course> courses = coursePages.getContent();

        List<Object[]> currentMonthRevenue = statsRepository.getCourseRevenuStatistics(0);
        List<Object[]> previousMonthRevenue = statsRepository.getCourseRevenuStatistics(1);

        Map<String, Integer> currentMapRevenue = currentMonthRevenue.stream()
                .collect(Collectors.toMap(o -> (String)o[0], x -> ((Number) x[1]).intValue()));

        Map<String, Integer> previousMapRevenue = previousMonthRevenue.stream()
                .collect(Collectors.toMap(o -> (String)o[0], x -> ((Number) x[1]).intValue()));
        return courses.stream().map(course -> {
            Integer currentRevenue = currentMapRevenue.get(course.getId());
            Integer previousRevenue = previousMapRevenue.get(course.getId());

            Double diff = 0.0;

            if(previousRevenue != null && currentRevenue != null)
                diff = (currentRevenue * 100.0 / previousRevenue) - 100.0;
            if(previousRevenue == null)
                diff = 100.0;
            if(currentRevenue == null)
                diff = 0.0;

            return CourseAdminResponse
                    .builder()
                    .name(course.getName())
                    .price(course.getPrice())
                    .isPublish(course.isPublish())
                    .status(course.getStatus())
                    .teacherName(course.getTeacher().getFullName())
                    .diff(diff)
                    .build();
        }).toList();
    }
}
