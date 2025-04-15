package com.tp.opencourse.mapper.decorator;

import com.tp.opencourse.dto.CommentDTO;
import com.tp.opencourse.entity.Comment;
import com.tp.opencourse.entity.User;
import com.tp.opencourse.exceptions.ResourceNotFoundExeption;
import com.tp.opencourse.mapper.CommentMapper;
import com.tp.opencourse.repository.UserRepository;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@NoArgsConstructor
@Mapper
public abstract class CommentMapperDecorator implements CommentMapper {
    @Autowired
    private CommentMapper delegate;
    @Autowired
    private UserRepository userRepository;

    @Override
    public CommentDTO convertDTO(Comment comment) {
        CommentDTO commentDTO = delegate.convertDTO(comment);
        CommentDTO.UserInfo userInfo = CommentDTO.UserInfo.builder()
                .id(comment.getUser().getId())
                .name(comment.getUser().getFullName())
                .avt(comment.getUser().getAvt())
                .build();

        commentDTO.setUserInfo(userInfo);
        return commentDTO;
    }

    @Override
    public Comment convertEntity(CommentDTO commentDTO) {
        Comment comment = delegate.convertEntity(commentDTO);
        Optional.ofNullable(commentDTO.getUserInfo()).ifPresent(u -> {
            User user = userRepository.findById(u.getId())
                    .orElseThrow(() -> new ResourceNotFoundExeption("Not found user"));
            comment.setUser(user);
        });
        return comment;
    }
}
