package com.tp.opencourse.service.impl;

import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.repository.RegisterRepository;
import com.tp.opencourse.repository.UserRepository;
import com.tp.opencourse.service.StatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StatServiceImpl implements StatService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final RegisterRepository registerRepository;

    @Override
    public Object[] getOverview() {
        Long totalCoures = courseRepository.count();
        Long totalUsers = userRepository.count();
        Long totalRegistration = registerRepository.countTotalRegistration();
        Double totalRevenue = registerRepository.countTotalRevenue();

        return new Object[] {totalCoures, totalUsers, totalRegistration, totalRevenue};
    }
}
