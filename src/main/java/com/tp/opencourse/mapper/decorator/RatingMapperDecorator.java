package com.tp.opencourse.mapper.decorator;


import com.tp.opencourse.dto.response.RatingResponse;
import com.tp.opencourse.entity.Rating;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.mapper.NotificationMapper;
import com.tp.opencourse.mapper.RatingMapper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@NoArgsConstructor
public abstract class RatingMapperDecorator implements RatingMapper {
    @Autowired
    private RatingMapper delegate;

    @Override
    public RatingResponse convertEntityToResponse(Rating rating) {
        RatingResponse ratingResponse = delegate.convertEntityToResponse(rating);
        if(ratingResponse == null)
            return null;

        User user = rating.getRegisterDetail().getRegister().getStudent();
        RatingResponse.UserInfo userInfo = RatingResponse.UserInfo.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .avt(user.getAvt())
                .build();
        ratingResponse.setUserInfo(userInfo);
        return ratingResponse;
    }
}
