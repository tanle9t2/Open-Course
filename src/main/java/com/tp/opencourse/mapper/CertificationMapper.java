package com.tp.opencourse.mapper;


import com.tp.opencourse.dto.response.CertificationResponse;
import com.tp.opencourse.entity.Certification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CourseMapper.class})
public interface CertificationMapper {
    @Mapping(target = "createdAt", source = "certification.createdAt")
    CertificationResponse convertEntityToResponse(Certification certification);
}
