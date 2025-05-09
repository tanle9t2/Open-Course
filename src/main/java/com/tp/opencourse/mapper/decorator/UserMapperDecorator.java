package com.tp.opencourse.mapper.decorator;

import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.dto.response.TeacherRevenueResponse;
import com.tp.opencourse.dto.response.UserProfileResponse;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.mapper.SubmitionMapper;
import com.tp.opencourse.mapper.UserMapper;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.repository.RegisterDetailRepository;
import org.checkerframework.checker.units.qual.A;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class UserMapperDecorator implements UserMapper {
    @Autowired
    private UserMapper delegate;
    @Autowired
    private RegisterDetailRepository registerDetailRepository;


    @Override
    public UserAuthDTO userToUserAuthDTO(User user) {
        return delegate.userToUserAuthDTO(user);
    }

    @Override
    public UserProfileResponse userToUserProfileResponse(User user) {
        return delegate.userToUserProfileResponse(user);
    }

    @Override
    public TeacherRevenueResponse convertTeacherRevenueResponse(User user) {
        TeacherRevenueResponse response = delegate.convertTeacherRevenueResponse(user);
        Double totalRevenue = registerDetailRepository.sumRevenueOfTeacher(user.getId());
        response.setTotalRevenue(totalRevenue);
        response.setFullName(user.getFullName());
        return response;
    }
}