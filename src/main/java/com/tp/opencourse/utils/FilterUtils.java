package com.tp.opencourse.utils;

import io.jsonwebtoken.impl.crypto.MacProvider;

import java.util.HashMap;
import java.util.Map;

public class FilterUtils {
    public static final String PAGE = "1";
    public static final String PAGE_SIZE = "3";
    public static final String PADDING_OFFSET = "0";
    public static final String CATEGORY = "categories";
    public static final String LEVEL = "level";
    public static final String RATING = "rating";
    public static final String TEACHER = "teacher";
    public static final String MIN_PRICE = "minPrice";
    public static final String MAX_PRICE = "maxPrice";
    public static final String DURATION = "duration";
    public static final Map<String, String> AGGS_VALUE = new HashMap<>() {{
        put("Rating", "rating");
        put("Duration(Hours)", "duration");
        put("Level", "level");
        put("Category", "category");
        put("Teacher", "teacher");
    }};
}
