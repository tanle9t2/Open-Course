package com.tp.opencourse.service.impl;

import com.tp.opencourse.dto.document.CourseDocument;
import com.tp.opencourse.mapper.CourseMapper;
import com.tp.opencourse.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestService {
    @Autowired
    private ElasticsearchOperations elasticsearchRestTemplate;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    CourseRepository courseRepository;

    @Transactional
    public void createIndex() {
        IndexCoordinates indexCoordinates = IndexCoordinates.of("restaurant_v001");
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(indexCoordinates);

        if (!indexOperations.exists()) {
            indexOperations.create();
        }
        boolean indexCreated = false;
        
        Class<CourseDocument> clazz = CourseDocument.class;
        if (!elasticsearchRestTemplate.indexOps(clazz).exists()) {
            elasticsearchRestTemplate.indexOps(clazz).create();
        }
        elasticsearchRestTemplate.indexOps(clazz).refresh();
        elasticsearchRestTemplate.indexOps(clazz).putMapping();

        List<CourseDocument> courseList = courseRepository
                .findAll().stream()
                .map(courseMapper::convertEntityToDocument).toList();
        elasticsearchRestTemplate.save(courseList);
    }
}
