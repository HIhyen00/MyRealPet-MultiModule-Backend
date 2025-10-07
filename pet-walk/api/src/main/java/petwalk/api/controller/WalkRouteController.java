package petwalk.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import petwalk.core.service.WalkRouteService;
import petwalk.dto.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/walk-routes")
@RequiredArgsConstructor
public class WalkRouteController {

    private final WalkRouteService walkRouteService;

    /**
     * 산책로 생성
     * @param request 산책로 생성 요청
     * @param httpRequest HTTP 요청 (인터셉터에서 userId 설정)
     * @return 생성된 산책로 정보
     */
    @PostMapping
    public ResponseEntity<WalkRouteResponse> createWalkRoute(
            @RequestBody CreateWalkRouteRequest request,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        WalkRouteResponse response = walkRouteService.createWalkRoute(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 내 산책로 목록 조회
     * @param httpRequest HTTP 요청 (인터셉터에서 userId 설정)
     * @return 산책로 목록
     */
    @GetMapping
    public ResponseEntity<List<WalkRouteListResponse>> getUserWalkRoutes(
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        List<WalkRouteListResponse> routes = walkRouteService.getUserWalkRoutes(userId);
        return ResponseEntity.ok(routes);
    }

    /**
     * 특정 산책로 상세 조회
     * @param routeId 산책로 ID
     * @param httpRequest HTTP 요청 (인터셉터에서 userId 설정)
     * @return 산책로 상세 정보
     */
    @GetMapping("/{routeId}")
    public ResponseEntity<WalkRouteResponse> getWalkRoute(
            @PathVariable Long routeId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        WalkRouteResponse response = walkRouteService.getWalkRoute(routeId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 산책로 삭제
     * @param routeId 산책로 ID
     * @param httpRequest HTTP 요청 (인터셉터에서 userId 설정)
     * @return void
     */
    @DeleteMapping("/{routeId}")
    public ResponseEntity<Void> deleteWalkRoute(
            @PathVariable Long routeId,
            HttpServletRequest httpRequest) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        walkRouteService.deleteWalkRoute(routeId, userId);
        return ResponseEntity.ok().build();
    }
}