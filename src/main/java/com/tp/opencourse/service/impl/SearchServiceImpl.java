package com.tp.opencourse.service.impl;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;

import com.tp.opencourse.dto.document.CourseDocument;
import com.tp.opencourse.dto.response.CourseResponse;
import com.tp.opencourse.dto.response.FilterSearchResponse;
import com.tp.opencourse.dto.response.PageResponse;
import com.tp.opencourse.entity.enums.CourseStatus;
import com.tp.opencourse.mapper.CourseMapper;
import com.tp.opencourse.service.SearchService;
import com.tp.opencourse.utils.FilterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.tp.opencourse.utils.FilterUtils.AGGS_VALUE;
import static com.tp.opencourse.utils.ValidationUtils.isNullOrEmpty;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final CourseMapper courseMapper;

    @Override
    public PageResponse<CourseResponse> searchCourse(String keyword, String page, String size, Map<String, String> params) {
        NativeQueryBuilder queryBuilder = this.extractQuery(keyword, page, size, params);
        SearchHits<CourseDocument> searchHits = elasticsearchOperations
                .search(queryBuilder.build(), CourseDocument.class);

        var responses = searchHits.getSearchHits()
                .stream()
                .map(hit ->
                        courseMapper.convertDocumentToResponse(hit.getContent())).toList();

        long totalElements = elasticsearchOperations.count(queryBuilder.build(), CourseDocument.class); //without the affect of pagination

        return PageResponse.<CourseResponse>builder()
                .content(responses)
                .page(Integer.parseInt(page))
                .size(Integer.parseInt(size))
                .totalElements(searchHits.getTotalHits())
                .totalPages((int) Math.ceil((double) totalElements / Integer.parseInt(size)))
                .build();
    }

    @Override
    public List<FilterSearchResponse> getAggregation(String keyword) {
        NativeQueryBuilder queryBuilder = this.extractQuery(keyword);
        NativeQuery query = queryBuilder
                .withAggregation("Rating", Aggregation.of(a -> a
                        .range(r -> r
                                .field("ratingDocument.average")
                                .ranges(
                                        AggregationRange.of(
                                                r1 -> r1
                                                        .from(0.0)
                                                        .to(null)
                                                        .key(">=0")),
                                        AggregationRange.of(
                                                r1 -> r1
                                                        .from(3.0)
                                                        .to(null)
                                                        .key(">=3")),
                                        AggregationRange.of(
                                                r2 -> r2
                                                        .from(3.5)
                                                        .to(null)
                                                        .key(">=3.5")),
                                        AggregationRange.of(
                                                r3 -> r3
                                                        .from(4.0)
                                                        .to(null)
                                                        .key(">=4")),
                                        AggregationRange.of(
                                                r4 -> r4
                                                        .from(4.5)
                                                        .to(null)
                                                        .key(">=4.5")
                                        )
                                )
                        )
                ))
                .withAggregation("Duration(Hours)", Aggregation.of(a -> a
                        .range(r -> r
                                .field("totalDuration")
                                .ranges(AggregationRange.of(r1 -> r1.from(0.0).to(3600.0).key("0-1")),
                                        AggregationRange.of(r2 -> r2.from(3600.0).to(10800.0).key("1-3")),
                                        AggregationRange.of(r2 -> r2.from(10800.0).to(21600.0).key("3-6")),
                                        AggregationRange.of(r3 -> r3.from(21600.0).to(null).key("6+"))
                                )
                        )
                ))
                .withAggregation("Level", Aggregation.of(a -> a
                        .terms(t -> t
                                .field("level")
                        )
                ))
                .withAggregation("Category", Aggregation.of(a -> a
                                .terms(t -> t
                                        .field("categoryDocument.id.keyword")
                                ).aggregations("name", subAggs ->
                                        subAggs.terms(t ->
                                                t.field("categoryDocument.name.keyword").size(1)))
                        )
                )
                .withAggregation("Teacher", Aggregation.of(a -> a
                        .terms(t -> t
                                .field("teacherDocument.id.keyword")
                        ).aggregations("name", subAggs ->
                                subAggs.terms(t ->
                                        t.field("teacherDocument.name.keyword").size(1)))
                ))
                .build();

        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(query, CourseDocument.class);
        List<org.springframework.data.elasticsearch.client.elc.Aggregation> aggregations = new ArrayList<>();

        if (searchHits.hasAggregations()) {
            ((List<ElasticsearchAggregation>) searchHits.getAggregations().aggregations()) //NOSONAR
                    .forEach(elsAgg -> aggregations.add(elsAgg.aggregation()));
        }


        List<FilterSearchResponse> responses = new ArrayList<>();

        for (org.springframework.data.elasticsearch.client.elc.Aggregation agg : aggregations) {
            String aggName = agg.getName();
            Aggregate innerAgg = agg.getAggregate();
            List<FilterSearchResponse.FilterSearchItem> filterResponseItems = new ArrayList<>();

            switch (innerAgg._kind()) {
                case Sterms:
                    StringTermsAggregate stringTermsAgg = (StringTermsAggregate) innerAgg._get();
                    List<StringTermsBucket> stringBuckets = (List<StringTermsBucket>) stringTermsAgg.buckets()._get();
                    for (StringTermsBucket bucket : stringBuckets) {
                        FilterSearchResponse.FilterSearchItem item = FilterSearchResponse.FilterSearchItem
                                .builder()
                                .label(bucket.key().stringValue())
                                .value(bucket.key().stringValue())
                                .count(bucket.docCount())
                                .build();
                        if (!bucket.aggregations().isEmpty()) {
                            StringTermsAggregate nameAgg = (StringTermsAggregate) bucket.aggregations().get("name")._get();
                            item.setLabel(nameAgg.buckets().array().get(0).key().stringValue());
                        }
                        filterResponseItems.add(item);
                    }
                    break;
                case Range:
                    List<RangeBucket> rangeBuckets = (List<RangeBucket>) innerAgg.range().buckets()._get();
                    for (RangeBucket bucket : rangeBuckets) {
                        filterResponseItems.add(FilterSearchResponse.FilterSearchItem
                                .builder()
                                .label(bucket.key())
                                .value(bucket.key())
                                .count(bucket.docCount())
                                .build());
                    }
                    break;
                default:
                    break;
            }
            responses.add(FilterSearchResponse
                    .builder()
                    .label(aggName)
                    .value(AGGS_VALUE.get(aggName))
                    .items(filterResponseItems)
                    .build());
        }

        return responses;
    }

    private NativeQueryBuilder extractQuery(String keyword) {
        return this.extractQuery(keyword, FilterUtils.PAGE, FilterUtils.PAGE_SIZE, null);
    }

    private NativeQueryBuilder extractQuery(String keyword, String page, String size, Map<String, String> params) {
        String categories = isValidSearchField(params, FilterUtils.CATEGORY) ? params.get(FilterUtils.CATEGORY) : null;
        String level = isValidSearchField(params, FilterUtils.LEVEL) ? params.get(FilterUtils.LEVEL) : null;
        Double rating = isValidSearchField(params, FilterUtils.RATING) ? Double.parseDouble(params.get(FilterUtils.RATING).substring(2)) : null;
        String teachers = isValidSearchField(params, FilterUtils.TEACHER) ? params.get(FilterUtils.TEACHER) : null;
        Double minPrice = isValidSearchField(params, FilterUtils.MIN_PRICE) ? Double.parseDouble(params.get(FilterUtils.MIN_PRICE)) : null;
        Double maxPrice = isValidSearchField(params, FilterUtils.MAX_PRICE) ? Double.parseDouble(params.get(FilterUtils.MAX_PRICE)) : null;
        String duration = isValidSearchField(params, FilterUtils.DURATION) ? params.get("duration") : null;

        String sortBy = params != null && !isNullOrEmpty(params.get("sortBy")) ? params.get("sortBy") : null;
        String order = params != null && !isNullOrEmpty(params.get("order")) ? params.get("order") : null;

        var boolQueryBuilder = QueryBuilders.bool();
        boolQueryBuilder
                .must(builder -> builder
                        .term(t -> t
                                .field("isPublish")
                                .value(true)))
                .must(builder -> builder
                        .term(t -> t
                                .field("status")
                                .value(CourseStatus.ACTIVE.toString())));

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
            orExtractedRange(duration, "totalDuration", b);
            extractedRange(rating, null, "ratingDocument.average", b);
            extractedRange(minPrice, maxPrice, "price", b);
            return b;
        }));

        if (sortBy != null) {
            SortOrder sortOrder = order != null && order.equals("desc") ? SortOrder.Desc : SortOrder.Asc;
            queryBuilder.withSort(s -> switch (sortBy) {
                case "highest-rated" -> s.field(f -> f.field("ratingDocument.average").order(SortOrder.Desc));
                case "newest" -> s.field(f -> f.field("createdAt").order(SortOrder.Desc));
                case "price" -> s.field(f -> f.field("price").order(sortOrder));
                default -> s.score(v -> v.order(SortOrder.Desc));
            });
        }

        return queryBuilder;
    }

    public static boolean isValidSearchField(Map<String, String> params, String keyword) {
        return params != null && !isNullOrEmpty(params.get(keyword));
    }

    public static void extractedTermsFilter(String fieldValues, String field, BoolQuery.Builder b) {
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

    //    0-1,2-3, 4-5,6+
    private void orExtractedRange(String durationRanges, String field, BoolQuery.Builder bool) {
        if (durationRanges == null || durationRanges.isBlank()) return;

        String[] ranges = durationRanges.split(",");

        bool.must(m -> {
            BoolQuery.Builder orBool = new BoolQuery.Builder();

            for (String range : ranges) {
                Pattern pattern = Pattern.compile("^(\\d+)(?:\\+|-(\\d+))?$");
                Matcher matcher = pattern.matcher(range.trim());

                if (matcher.matches()) {
                    Double from = matcher.group(1) != null ? Double.parseDouble(matcher.group(1)) * 3600 : null;
                    Double to = matcher.group(2) != null ? Double.parseDouble(matcher.group(2)) * 3600 : null;

                    orBool.should(s -> s.range(r -> {
                        r.term(t -> t.field(field).from(from != null ? from.toString() : null).to(to != null ? to.toString() : null));
//                        r.field(field).from(from != null ? from.toString() : null).to(to != null ? to.toString() : null);
                        return r;
                    }));
                }
            }

            return new Query.Builder().bool(orBool.build());
        });
    }

    private void extractedRange(Number min, Number max, String field, BoolQuery.Builder bool) {
        if (min != null || max != null) {
            bool.must(m -> m
                    .range(r -> r.term(t -> t.field(field)
                            .from(min != null ? min.toString() : null)
                            .to(max != null ? max.toString() : null))
                    )
            );
        }
    }
}
