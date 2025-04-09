package com.tp.opencourse.dto.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionDocument {

    private String id;
    @Field(type = FieldType.Text)
    private String name;
    private List<ContentDocument> contentDocumentList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentDocument {
        private String id;
        @Field(type = FieldType.Text)
        private String name;
    }
}
