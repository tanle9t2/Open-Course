package com.tp.opencourse.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.tp.opencourse.dto.document.CourseDocument;
import com.tp.opencourse.dto.document.SectionDocument;
import com.tp.opencourse.entity.Category;
import com.tp.opencourse.entity.Content;
import com.tp.opencourse.entity.Course;
import com.tp.opencourse.entity.Section;
import com.tp.opencourse.mapper.CategoryMapper;
import com.tp.opencourse.mapper.CourseMapper;
import com.tp.opencourse.mapper.SectionMapper;
import com.tp.opencourse.repository.CategoryRepository;
import com.tp.opencourse.repository.ContentRepository;
import com.tp.opencourse.repository.CourseRepository;
import com.tp.opencourse.repository.SectionRepository;
import com.tp.opencourse.service.CourseDataSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseDataSyncServiceImpl implements CourseDataSyncService {

    private final CategoryMapper categoryMapper;
    private final CourseMapper courseMapper;
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final SectionRepository sectionRepository;
    private final ContentRepository contentRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final String INDEX_NAME = "course-index";

    @Override
    public void createCourse(String courseId) {
        IndexCoordinates indexCoordinates = IndexCoordinates.of(INDEX_NAME);
        IndexOperations indexOperations = elasticsearchOperations.indexOps(indexCoordinates);
        if (!indexOperations.exists()) {
            indexOperations.create();
            System.out.println("Index created: " + INDEX_NAME);
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("course with Id " + courseId + " not found"));
        CourseDocument courseDocument = courseMapper.convertEntityToDocument(course);
        elasticsearchOperations.save(courseDocument);
    }

    @Override
    public void updateCourse(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("course with Id " + courseId + " not found"));
        CourseDocument courseDocument = elasticsearchOperations.get(String.valueOf(courseId),
                CourseDocument.class, IndexCoordinates.of(INDEX_NAME));

        courseDocument.setName(course.getName());
        courseDocument.setPrice(course.getPrice());
        courseDocument.setBanner(course.getBanner());
        courseDocument.setPublish(course.isPublish());
        courseDocument.setDescription(course.getDescription());
        courseDocument.setLevel(course.getLevel());

        CourseDocument.CategoryDocument categoryDocument = categoryMapper.convertDocument(course.getCategories());
        categoryDocument.setCategoryIds(categoryRepository
                .getAllCategoryHierachyIds(
                        course.getCategories().getLft(),
                        course.getCategories().getRgt()
                ));

        courseDocument.setCategoryDocument(categoryDocument);
        courseDocument.setTeacherDocument(
                CourseDocument.TeacherDocument.builder()
                .id(course.getTeacher().getId())
                .name(course.getTeacher().getFullName())
                .build());
        elasticsearchOperations.save(courseDocument);
    }

    @Override
    public void deleteCourse(String courseId) {
        elasticsearchOperations.delete(String.valueOf(courseId), IndexCoordinates.of(INDEX_NAME));
    }

    @Override
    public void updateCategory(String categoryId) {
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        queryBuilder.withFilter(f -> f.bool(b -> b
                .should(s -> s.term(t -> t
                        .field("categoryDocument.id")
                        .value(categoryId)
                        .caseInsensitive(true)))
                .should(s -> s.term(t -> t
                        .field("categoryDocument.categoryIds")
                        .value(categoryId)
                        .caseInsensitive(true)))
        ));

        SearchHits<CourseDocument> searchHits = elasticsearchOperations
                .search(queryBuilder.build(), CourseDocument.class);

        List<SearchHit<CourseDocument>> hits = searchHits.getSearchHits();
        if(hits.isEmpty())
            return;

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("category with Id " + categoryId + " not found"));
        List<String> categoryIds = categoryRepository.getAllCategoryHierachyIds(category.getLft(), category.getRgt());

        for (SearchHit<CourseDocument> hit : hits) {
            CourseDocument courseDocument = hit.getContent();

            if(courseDocument.getCategoryDocument().getId().equals(categoryId)) {
                CourseDocument.CategoryDocument categoryDocument =  categoryMapper.convertDocument(category);
                categoryDocument.setCategoryIds(categoryIds);

                courseDocument.setCategoryDocument(categoryDocument);
            } else {
                Category hitCategory = categoryRepository.findById(courseDocument.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("category with Id " + categoryId + " not found"));
                List<String> hitCategoryIds = categoryRepository
                        .getAllCategoryHierachyIds(hitCategory.getLft(), hitCategory.getRgt());
                courseDocument.getCategoryDocument().setCategoryIds(hitCategoryIds);
            }
        }
    }

    @Override
    public void deleteCategory(String courseId) {
        //
    }

    @Override
    public void updateSection(String sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("section with Id " + sectionId + " not found"));

        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        queryBuilder.withFilter(f -> f.bool(b -> b
                .must(s -> s.term(t -> t
                        .field("sectionDocument.id")
                        .value(sectionId)
                        .caseInsensitive(true)))
        )).withMaxResults(1);

        SearchHits<CourseDocument> searchHits = elasticsearchOperations
                .search(queryBuilder.build(), CourseDocument.class);

        List<SearchHit<CourseDocument>> hits = searchHits.getSearchHits();
        if(hits.isEmpty())
            return;
        CourseDocument hit = hits.get(0).getContent();
        SectionDocument sectionDocument = hit.getSectionDocument().stream()
                .filter(s -> s.getId().equals(section.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("section with Id " + sectionId + " not found"));
        sectionDocument.setName(section.getName());
    }

    @Override
    public void deleteSection(String sectionId) {
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        queryBuilder.withFilter(f -> f.bool(b -> b
                .must(s -> s.term(t -> t
                        .field("sectionDocument.id")
                        .value(sectionId)
                        .caseInsensitive(true)))
        )).withMaxResults(1);

        SearchHits<CourseDocument> searchHits = elasticsearchOperations
                .search(queryBuilder.build(), CourseDocument.class);

        List<SearchHit<CourseDocument>> hits = searchHits.getSearchHits();
        if(hits.isEmpty())
            return;
        CourseDocument hit = hits.get(0).getContent();
        hit.getSectionDocument().removeIf(s -> s.getId().equals(sectionId));
        elasticsearchOperations.save(hit);
    }

    @Override
    public void updateContent(String contentId) {
        Content content = contentRepository.findContentById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("content with Id " + contentId + " not found"));
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        queryBuilder.withFilter(f -> f.bool(b -> b
                .must(s -> s.term(t -> t
                        .field("sectionDocument.contentDocumentList.id")
                        .value(contentId)
                        .caseInsensitive(true)))
        )).withMaxResults(1);

        SearchHits<CourseDocument> searchHits = elasticsearchOperations
                .search(queryBuilder.build(), CourseDocument.class);

        List<SearchHit<CourseDocument>> hits = searchHits.getSearchHits();
        if(hits.isEmpty())
            return;
        CourseDocument hit = hits.get(0).getContent();
        SectionDocument.ContentDocument contentDocument = hit.getSectionDocument().stream()
                .flatMap(section -> section.getContentDocumentList().stream())
                .filter(f -> f.getId().equals(contentId)).findFirst().get();

        contentDocument.setName(content.getName());
        elasticsearchOperations.save(hit);
    }

    @Override
    public void deleteContent(String contentId) {

        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        queryBuilder.withFilter(f -> f.bool(b -> b
                .must(s -> s.term(t -> t
                        .field("sectionDocument.contentDocumentList.id")
                        .value(contentId)
                        .caseInsensitive(true)))
        )).withMaxResults(1);

        SearchHits<CourseDocument> searchHits = elasticsearchOperations
                .search(queryBuilder.build(), CourseDocument.class);

        List<SearchHit<CourseDocument>> hits = searchHits.getSearchHits();
        if(hits.isEmpty())
            return;
        CourseDocument hit = hits.get(0).getContent();
        hit.getSectionDocument().stream()
                .map(s -> s.getContentDocumentList()
                        .removeIf(c -> c.getId().equals(contentId)));
        elasticsearchOperations.save(hit);
    }
}
