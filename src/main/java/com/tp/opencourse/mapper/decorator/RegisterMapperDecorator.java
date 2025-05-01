package com.tp.opencourse.mapper.decorator;

import com.tp.opencourse.dto.response.LearningResponse;
import com.tp.opencourse.entity.RegisterDetail;
import com.tp.opencourse.mapper.CourseMapper;
import com.tp.opencourse.mapper.RatingMapper;
import com.tp.opencourse.mapper.RegisterMapper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@NoArgsConstructor
public class RegisterMapperDecorator implements RegisterMapper {
    @Autowired
    private RegisterMapper delegate;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private RatingMapper ratingMapper;
    @Override
    public List<LearningResponse> convertEntityToLearningResponse(List<RegisterDetail> registerDetails) {
        return registerDetails.stream().map(registerDetail ->
                LearningResponse.builder()
                        .id(registerDetail.getId())
                        .percentComplete(registerDetail.getPercentComplete())
                        .isCompleted(registerDetail.getCertification() != null)
                        .purchasedAt(registerDetail.getRegister().getCreatedAt())
                        .course(courseMapper.convertEntityToResponse(registerDetail.getCourse()))
                        .rating(ratingMapper.convertEntityToResponse(registerDetail.getRating()))
                        .build()

        ).toList();
    }
}
