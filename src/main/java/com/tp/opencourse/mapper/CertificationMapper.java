package com.tp.opencourse.mapper;


import com.tp.opencourse.dto.response.CertificationResponse;
import com.tp.opencourse.entity.Certification;
import com.tp.opencourse.mapper.decorator.CertificationMapperDecorator;
import com.tp.opencourse.mapper.decorator.RatingMapperDecorator;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
@DecoratedWith(CertificationMapperDecorator.class)
public interface CertificationMapper {

    @Mapping(target = "createdAt", source = "certification.createdAt")
    CertificationResponse convertEntityToResponse(Certification certification);
}
