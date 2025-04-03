package com.tp.opencourse.mapper.decorator;

import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.mapper.SubmitionMapper;
import com.tp.opencourse.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class UserMapperDecorator implements UserMapper {
    @Autowired
    private UserMapper delegate;

    @Override
    public UserAuthDTO userToUserAuthDTO(User user) {
        return delegate.userToUserAuthDTO(user);
    }
}