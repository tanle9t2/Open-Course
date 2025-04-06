package com.tp.opencourse.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PageResponse<T>{
    private List<T> data;
    private int page;
    private int totalElement;
    private Long count;
    private int totalPages;
    private HttpStatus status;

}
