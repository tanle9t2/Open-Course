package com.tp.opencourse.dto.document;

import com.tp.opencourse.dto.response.CategoryResponse;
import com.tp.opencourse.dto.response.CourseResponse;
import com.tp.opencourse.entity.enums.CourseStatus;
import com.tp.opencourse.entity.enums.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

@Document(indexName = "course-index")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Double)
    private double price;

    @Field(type = FieldType.Double)
    private long totalDuration;

    @Field(type = FieldType.Long)
    private long totalLecture;

    @Field(type = FieldType.Long)
    private long totalRegistration;

    @Field(type = FieldType.Keyword)
    private Level level;

    @Field(type = FieldType.Keyword)
    private String banner;

    @Field(type = FieldType.Boolean)
    private boolean isPublish;

    @Field(type = FieldType.Keyword)
    private CourseStatus status;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Object)
    private CategoryDocument categoryDocument;

    @Field(type = FieldType.Object)
    private TeacherDocument teacherDocument;

    @Field(type = FieldType.Object)
    private RatingDocument ratingDocument;

    @Field(type = FieldType.Object)
    private List<SectionDocument> sectionDocument;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDocument {
        @Field(type = FieldType.Keyword)
        private List<String> categoryIds;

        private String id;
        private String name;
        private Integer lft;
        private Integer rgt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeacherDocument {
        private String id;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RatingDocument {
        private double average;
        private long qty;
    }


}
