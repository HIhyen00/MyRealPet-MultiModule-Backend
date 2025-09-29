package petwalk.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SearchResponse {
    private SearchMeta meta;
    private List<SearchDocument> documents;
}