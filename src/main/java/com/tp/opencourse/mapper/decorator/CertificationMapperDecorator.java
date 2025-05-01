package com.tp.opencourse.mapper.decorator;


import com.tp.opencourse.dto.response.CertificationResponse;
import com.tp.opencourse.entity.Certification;
import com.tp.opencourse.mapper.CertificationMapper;
import com.tp.opencourse.mapper.CourseMapper;
import com.tp.opencourse.mapper.RatingMapper;
import com.tp.opencourse.mapper.UserMapper;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@NoArgsConstructor
@Mapper
public abstract class CertificationMapperDecorator implements CertificationMapper {

    @Autowired
    private CertificationMapper delegate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public CertificationResponse convertEntityToResponse(Certification certification) {
        CertificationResponse certificationResponse = delegate.convertEntityToResponse(certification);
        certificationResponse.setCourse(courseMapper.convertEntityToResponse(certification.getRegisterDetail().getCourse()));
        certificationResponse.setUser(userMapper.userToUserProfileResponse(certification.getRegisterDetail().getRegister().getStudent()));
        return certificationResponse;
    }
}
