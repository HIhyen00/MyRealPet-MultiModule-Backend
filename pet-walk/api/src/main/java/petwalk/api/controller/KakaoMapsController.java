package petwalk.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import petwalk.dto.SearchRequest;
import petwalk.dto.SearchType;
import petwalk.dto.SearchResponse;
import petwalk.core.service.KakaoMapsService;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kakao-maps")
@Validated
public class KakaoMapsController {

    private final KakaoMapsService kakaoMapsService;

    /**
     * 장소 검색 API
     *
     * @param searchRequest 검색 요청 정보
     * @param searchType 검색 타입 (KEYWORD, CATEGORY)
     * @return 검색 결과
     */
    @GetMapping("/search")
    public Mono<ResponseEntity<SearchResponse>> searchPlaces(
            @Valid @ModelAttribute SearchRequest searchRequest,
            @RequestParam SearchType searchType
    ) {
        log.debug("장소 검색 요청: searchType={}, query={}", searchType, searchRequest.getQuery());

        return kakaoMapsService.searchPlaces(searchRequest, searchType)
                .map(ResponseEntity::ok)
                .doOnSuccess(result -> log.debug("장소 검색 성공: {} 건",
                    result.getBody() != null ? result.getBody().getMeta().getTotalCount() : 0))
                .doOnError(error -> log.error("장소 검색 실패: {}", error.getMessage()));
    }
}