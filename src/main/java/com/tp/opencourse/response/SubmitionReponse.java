package com.tp.opencourse.response;

import com.tp.opencourse.dto.ContentDTO;
import com.tp.opencourse.dto.SectionDTO;
import com.tp.opencourse.dto.SubmitionDTO;
import com.tp.opencourse.entity.Section;
import com.tp.opencourse.entity.Submition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitionReponse {
    private ContentDTO contentDTO;
    private List<SubmitionDTO> submitions;
}
