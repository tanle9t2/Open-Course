package com.tp.opencourse.mapper.decorator;

import com.tp.opencourse.dto.response.LearningResponse;
import com.tp.opencourse.entity.Register;
import com.tp.opencourse.entity.RegisterDetail;
import com.tp.opencourse.mapper.CourseMapper;
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

    @Override
    public List<LearningResponse> convertEntityToLearningResponse(List<RegisterDetail> registerDetails) {
        return registerDetails.stream().map(registerDetail ->
                LearningResponse.builder()
                        .id(registerDetail.getId())
                        .percentComplete(registerDetail.getPercentComplete())
                        .course(courseMapper.convertEntityToResponse(registerDetail.getCourse()))
                        .build()
        ).toList();
    }
}
