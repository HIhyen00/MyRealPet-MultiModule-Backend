package petwalk.client.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import petwalk.dto.ReverseGeocodingRequest;
import petwalk.dto.SearchRequest;
import petwalk.dto.SearchType;
import petwalk.dto.ReverseGeocodingResponse;
import petwalk.dto.SearchResponse;
import petwalk.core.service.KakaoMapsService;

import jakarta.annotation.PostConstruct;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoMapsServiceImpl implements KakaoMapsService {
    @Qualifier("kakaoWebClient")
    private final WebClient kakaoWebClient;

    @Value("${app.kakao.api-key}")
    private String kakaoRestApiKey;

    @PostConstruct
    public void validateConfiguration() {
        if (kakaoRestApiKey == null || kakaoRestApiKey.trim().isEmpty()) {
            throw new IllegalStateException("Kakao API key is not configured");
        }
        log.info("KakaoMapsService initialized successfully");
    }

    @Override
    public Mono<SearchResponse> searchPlaces(SearchRequest request, SearchType searchType) {
        log.debug("Executing {} search with query: {}", searchType, request.getQuery());

        validateSearchRequest(request, searchType);
        String path = getSearchPath(searchType);

        return kakaoWebClient.get()
                .uri(uriBuilder -> buildSearchUri(uriBuilder, path, request, searchType))
                .header("Authorization", "KakaoAK " + kakaoRestApiKey)
                .retrieve()
                .bodyToMono(SearchResponse.class)
                .doOnSuccess(response -> log.debug("Search completed successfully, found {} results",
                        Optional.ofNullable(response)
                                .map(SearchResponse::getMeta)
                                .map(meta -> meta.getTotalCount())
                                .orElse(0)))
                .doOnError(error -> log.error("Search failed: {}", error.getMessage()));
    }

    private void validateSearchRequest(SearchRequest request, SearchType searchType) {
        switch (searchType) {
            case KEYWORD -> {
                if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
                    throw new IllegalArgumentException("키워드 검색 시 query는 필수입니다.");
                }
            }
            case CATEGORY -> {
                if (request.getCategoryGroupCode() == null) {
                    throw new IllegalArgumentException("카테고리 검색 시 category_group_code는 필수입니다.");
                }
                if (request.getX() == null || request.getY() == null) {
                    throw new IllegalArgumentException("카테고리 검색 시 x, y 좌표는 필수입니다.");
                }
                if (request.getRadius() == null) {
                    throw new IllegalArgumentException("카테고리 검색 시 radius는 필수입니다.");
                }
            }
        }
    }

    private String getSearchPath(SearchType searchType) {
        return switch (searchType) {
            case KEYWORD -> "/v2/local/search/keyword.json";
            case CATEGORY -> "/v2/local/search/category.json";
        };
    }

    private java.net.URI buildSearchUri(org.springframework.web.util.UriBuilder uriBuilder,
                                        String path, SearchRequest request, SearchType searchType) {
        uriBuilder.path(path);

        // Add search type specific parameters
        if (searchType == SearchType.KEYWORD && request.getQuery() != null) {
            uriBuilder.queryParam("query", request.getQuery());
        }
        if (searchType == SearchType.CATEGORY && request.getCategoryGroupCode() != null) {
            uriBuilder.queryParam("category_group_code", request.getCategoryGroupCode().name());
        }

        // Add common optional parameters
        Optional.ofNullable(request.getX()).ifPresent(x ->
                Optional.ofNullable(request.getY()).ifPresent(y -> {
                    uriBuilder.queryParam("x", x).queryParam("y", y);
                }));

        Optional.ofNullable(request.getRadius()).ifPresent(radius ->
                uriBuilder.queryParam("radius", radius));
        Optional.ofNullable(request.getRect()).ifPresent(rect ->
                uriBuilder.queryParam("rect", rect));
        Optional.ofNullable(request.getPage()).ifPresent(page ->
                uriBuilder.queryParam("page", page));
        Optional.ofNullable(request.getSize()).ifPresent(size ->
                uriBuilder.queryParam("size", size));
        Optional.ofNullable(request.getSort()).ifPresent(sort ->
                uriBuilder.queryParam("sort", sort));

        return uriBuilder.build();
    }

    @Override
    public Mono<ReverseGeocodingResponse> reverseGeocoding(ReverseGeocodingRequest request) {
        log.debug("Executing reverse geocoding for coordinates: x={}, y={}", request.getX(), request.getY());

        validateReverseGeocodingRequest(request);

        return kakaoWebClient.get()
                .uri(uriBuilder -> buildReverseGeocodingUri(uriBuilder, request))
                .header("Authorization", "KakaoAK " + kakaoRestApiKey)
                .retrieve()
                .bodyToMono(ReverseGeocodingResponse.class)
                .doOnSuccess(response -> log.debug("Reverse geocoding completed successfully"))
                .doOnError(error -> log.error("Reverse geocoding failed: {}", error.getMessage()));
    }

    private void validateReverseGeocodingRequest(ReverseGeocodingRequest request) {
        if (request.getX() == null || request.getY() == null) {
            throw new IllegalArgumentException("x, y 좌표는 필수입니다.");
        }
    }

    private java.net.URI buildReverseGeocodingUri(org.springframework.web.util.UriBuilder uriBuilder,
                                                  ReverseGeocodingRequest request) {
        uriBuilder.path("/v2/local/geo/coord2address.json")
                .queryParam("x", request.getX())
                .queryParam("y", request.getY());

        Optional.ofNullable(request.getInputCoord()).ifPresent(inputCoord ->
                uriBuilder.queryParam("input_coord", inputCoord));

        return uriBuilder.build();
    }

}
