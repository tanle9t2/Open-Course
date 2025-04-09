package com.tp.opencourse.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PageResponse<T> {

    private List<T> content;
    //current page
    private int page;
    //element per page
    private int size;
    //element quantity of the record
    private long totalElements;
    //page count
    private int totalPages;
    private boolean last;
}
