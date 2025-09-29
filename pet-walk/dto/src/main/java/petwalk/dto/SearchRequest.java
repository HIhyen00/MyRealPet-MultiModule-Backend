package petwalk.dto;

import lombok.Data;

@Data
public class SearchRequest {
    private String query;

    private CategoryGroupCode categoryGroupCode;

    private String x;
    private String y;
    private Integer radius;
    private String rect;
    private Integer page;
    private Integer size;
    private String sort;
}