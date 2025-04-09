package com.tp.opencourse.service.impl;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;

import com.tp.opencourse.dto.document.CourseDocument;
import com.tp.opencourse.dto.response.CourseResponse;
import com.tp.opencourse.dto.response.PageResponse;
import com.tp.opencourse.mapper.CourseMapper;
import com.tp.opencourse.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import java.util.Map;

import static com.tp.opencourse.utils.ValidationUtils.isNullOrEmpty;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final CourseMapper courseMapper;

    @Override
    public PageResponse<CourseResponse> searchCourse(String keyword, String page, String size, Map<String, String> params) {
        String categories = !isNullOrEmpty(params.get("categories")) ? params.get("categories") : null;
        String level = !isNullOrEmpty(params.get("level")) ? params.get("level") : null;
        Double rating = !isNullOrEmpty(params.get("rating")) ? Double.valueOf(params.get("rating")) : null;
        String teachers = !isNullOrEmpty(params.get("teachers")) ? params.get("teachers") : null;
        Double minPrice = !isNullOrEmpty(params.get("minPrice")) ? Double.valueOf(params.get("minPrice")) : null;
        Double maxPrice = !isNullOrEmpty(params.get("maxPrice")) ? Double.valueOf(params.get("maxPrice")) : null;

        String sortBy = params.get("sortBy");
        String order = params.get("order");

        var boolQueryBuilder = QueryBuilders.bool();
        boolQueryBuilder
                .must(builder -> builder
                        .term(t -> t.field("isPublish").value(true)));
        if (keyword != null && !keyword.isEmpty()) {
            var shouldQuery = QueryBuilders.bool();
            boolQueryBuilder
                .should(QueryBuilders.multiMatch(m -> m
                        .fields("name",
                                "description",
                                "categoryDocument.name",
                                "sectionDocument.name",
                                "sectionDocument.contentDocumentList.name")
                        .query(keyword)
                        .fuzziness("AUTO")
                        .boost(1.2f)))
                .should(QueryBuilders.matchPhrasePrefix(m -> m
                        .field("name")
                        .query(keyword)
                        .maxExpansions(20)
                        .slop(2)))
                .should(QueryBuilders.match(m -> m
                        .field("teacherDocument.name")
                        .query(keyword)
                        .fuzziness("AUTO")
                        .maxExpansions(20)))
                .minimumShouldMatch("1");
            boolQueryBuilder.must(shouldQuery.build()._toQuery());
        }

        NativeQueryBuilder queryBuilder = new NativeQueryBuilder()
                .withQuery(boolQueryBuilder.build()._toQuery());

        queryBuilder.withPageable(PageRequest.of(
                Integer.parseInt(page) - 1,
                Integer.parseInt(size)));

        queryBuilder.withFilter(f -> f.bool(b -> {
            extractedTermsFilter(level, "level", b);
            extractedTermsFilter(teachers, "teacherDocument.id", b);
            extractedTermsFilter(categories, "categoryDocument.categoryIds", b);
            extractedRange(rating, null, "ratingDocument.average", b);
            extractedRange(minPrice, maxPrice, "price", b);
            return b;
        }));

        if (sortBy != null) {
            SortOrder sortOrder = order.equals("desc") ? SortOrder.Desc : SortOrder.Asc;
            queryBuilder.withSort(s -> switch (sortBy) {
                case "highest-rated" -> s.field(f -> f.field("ratingDocument.average").order(SortOrder.Desc));
                case "newest" -> s.field(f -> f.field("createdAt").order(SortOrder.Desc));
                case "price" -> s.field(f -> f.field("price").order(sortOrder));
                default -> s.score(v -> v.order(SortOrder.Desc));
            });
        }
        SearchHits<CourseDocument> searchHits = elasticsearchOperations
                .search(queryBuilder.build(), CourseDocument.class);

        var responses = searchHits.getSearchHits()
                .stream()
                .map(hit ->
                        courseMapper.convertDocumentToResponse(hit.getContent())).toList();

        long totalElements = elasticsearchOperations.count(queryBuilder.build(), CourseDocument.class);

        return PageResponse.<CourseResponse>builder()
                .content(responses)
                .page(Integer.parseInt(page))
                .size(Integer.parseInt(size))
                .totalElements(searchHits.getTotalHits())
                .totalPages((int) Math.ceil((double) totalElements / Integer.parseInt(size)))
                .build();
    }

    private void extractedTermsFilter(String fieldValues, String field, BoolQuery.Builder b) {
        if (fieldValues == null || fieldValues.isEmpty()) {
            return;
        }
        String[] valuesArray = fieldValues.split(",");
        b.must(m -> {
            BoolQuery.Builder innerBool = new BoolQuery.Builder();
            for (String value : valuesArray) {
                innerBool.should(s -> s
                        .term(t -> t
                                .field(field)
                                .value(value)
                                .caseInsensitive(true)
                        )
                );
            }
            return new Query.Builder().bool(innerBool.build());
        });
    }

    private void extractedRange(Number min, Number max, String field, BoolQuery.Builder bool) {
        if (min != null || max != null) {
            bool.must(m -> m
                    .range(r -> r
                            .field(field)
                            .from(min != null ? min.toString() : null)
                            .to(max != null ? max.toString() : null)
                    )
            );
        }
    }
}
