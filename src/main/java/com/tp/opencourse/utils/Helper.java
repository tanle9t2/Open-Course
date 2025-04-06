package com.tp.opencourse.utils;

import com.tp.opencourse.exceptions.BadRequestException;

import java.util.Map;

public class Helper {
    public static void validateRequiredFields(Map<String, String> map, String... fields) {
        for (String field : fields) {
            if (map.get(field) == null) {
                throw new BadRequestException("Required field '" + field + "' is missing");
            }
        }
    }
}
