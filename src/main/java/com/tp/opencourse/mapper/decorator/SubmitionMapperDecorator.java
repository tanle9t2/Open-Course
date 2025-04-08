package com.tp.opencourse.mapper.decorator;

import com.tp.opencourse.dto.SubmitionDTO;
import com.tp.opencourse.entity.Submition;
import com.tp.opencourse.mapper.SubmitionMapper;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@NoArgsConstructor
@Mapper

public abstract class SubmitionMapperDecorator implements SubmitionMapper {
    @Autowired
    private SubmitionMapper delegate;

    @Override
    public SubmitionDTO convertDTO(Submition submition) {
        SubmitionDTO submitionDTO = delegate.convertDTO(submition);
        SubmitionDTO.StudentInfo studentInfo = SubmitionDTO.StudentInfo.builder()
                .id(submition.getStudent().getId())
                .name(submition.getStudent().getFullName())
                .avt(submition.getStudent().getAvt())
                .build();
        submitionDTO.setStudentInfo(studentInfo);
        return submitionDTO;
    }
}
