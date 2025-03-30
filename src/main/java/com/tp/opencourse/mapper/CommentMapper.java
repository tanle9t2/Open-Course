package com.tp.opencourse.mapper;

import com.tp.opencourse.dto.CommentDTO;
import com.tp.opencourse.entity.Comment;
import com.tp.opencourse.mapper.decorator.CommentMapperDecorator;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
@DecoratedWith(CommentMapperDecorator.class)
public interface CommentMapper {

    CommentDTO convertDTO(Comment comment);

    Comment convertEntity(CommentDTO commentDTO);
}
