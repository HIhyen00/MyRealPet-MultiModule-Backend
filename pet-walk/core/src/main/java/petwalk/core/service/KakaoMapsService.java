package petwalk.core.service;

import reactor.core.publisher.Mono;
import petwalk.dto.ReverseGeocodingRequest;
import petwalk.dto.SearchRequest;
import petwalk.dto.SearchType;
import petwalk.dto.ReverseGeocodingResponse;
import petwalk.dto.SearchResponse;

public interface KakaoMapsService {
    Mono<SearchResponse> searchPlaces(SearchRequest query, SearchType searchType);

    Mono<ReverseGeocodingResponse> reverseGeocoding(ReverseGeocodingRequest query);
}