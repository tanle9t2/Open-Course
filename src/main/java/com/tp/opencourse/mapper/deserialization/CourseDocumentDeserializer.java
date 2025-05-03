package com.tp.opencourse.mapper.deserialization;

import com.google.gson.*;
import com.tp.opencourse.dto.document.CourseDocument;

import java.lang.reflect.Type;

import static com.tp.opencourse.utils.FieldElasticsearchConvert.*;

public class CourseDocumentDeserializer implements JsonDeserializer<CourseDocument> {
    @Override
    public CourseDocument deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        CourseDocument courseDocument = new CourseDocument();
        courseDocument.setId(getStringValue(obj, "product_id"));
        courseDocument.setName(getStringValue(obj, "name"));
        courseDocument.setDescription(getStringValue(obj, "description"));
        courseDocument.setPrice(getDoubleValue(obj, "price"));
        courseDocument.setBanner(getStringValue(obj, "description"));
        courseDocument.setPublish(getBooleanValue(obj, "is_publish"));
        courseDocument.setCategoryDocument(CourseDocument.CategoryDocument
                .builder()
                .id(getStringValue(obj, "category_id"))
                .build());
        courseDocument.setTeacherDocument(
                CourseDocument.TeacherDocument
                        .builder()
                        .id(getStringValue(obj, "teacher_id"))
                        .build()
        );

        return courseDocument;
    }
}
