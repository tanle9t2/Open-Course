package com.tp.opencourse.service;

import com.tp.opencourse.entity.Category;

import java.util.List;
import java.util.Locale;

public interface CategoryService {
    List<Category> findByParentIdAndLevel(String parentName, Integer level);
}
