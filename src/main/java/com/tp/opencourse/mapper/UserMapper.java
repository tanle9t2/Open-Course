package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.UserAuthDTO;
import com.tp.opencourse.dto.response.UserAdminResponse;
import com.tp.opencourse.dto.response.UserProfileResponse;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.mapper.decorator.CommentMapperDecorator;
import com.tp.opencourse.mapper.decorator.UserMapperDecorator;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
@DecoratedWith(UserMapperDecorator.class)
public interface UserMapper {
    UserAuthDTO userToUserAuthDTO(User user);
    UserProfileResponse userToUserProfileResponse(User user);

    UserAdminResponse userToUserAdminResponse(User user);
}