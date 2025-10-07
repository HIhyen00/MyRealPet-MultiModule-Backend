package petwalk.core.service;

import reactor.core.publisher.Mono;
import petwalk.dto.SearchRequest;
import petwalk.dto.SearchType;
import petwalk.dto.SearchResponse;

public interface KakaoMapsService {
    Mono<SearchResponse> searchPlaces(SearchRequest query, SearchType searchType);
}