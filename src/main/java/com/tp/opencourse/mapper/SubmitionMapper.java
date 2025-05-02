package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.SubmitionDTO;
import com.tp.opencourse.entity.Submition;
import com.tp.opencourse.mapper.decorator.SubmitionMapperDecorator;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ContentProcessMapper.class,CommentMapper.class})
@DecoratedWith(SubmitionMapperDecorator.class)
public interface SubmitionMapper {

    SubmitionDTO convertDTO(Submition submition);

    Submition convertEntity(SubmitionDTO submitionDTO);
}
