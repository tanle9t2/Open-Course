package com.tp.opencourse.service.impl;

import com.tp.opencourse.entity.Category;
import com.tp.opencourse.repository.CategoryRepository;
import com.tp.opencourse.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> findByParentIdAndLevel(String parentName, Integer level) {
        List<Category> categories = categoryRepository.findFollowLevel(parentName, level);
        return categories;
    }
}
