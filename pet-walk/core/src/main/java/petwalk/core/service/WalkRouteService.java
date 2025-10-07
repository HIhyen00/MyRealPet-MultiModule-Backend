package petwalk.core.service;

import petwalk.dto.CreateWalkRouteRequest;
import petwalk.dto.WalkRouteListResponse;
import petwalk.dto.WalkRouteResponse;

import java.util.List;

public interface WalkRouteService {

    WalkRouteResponse createWalkRoute(Long userId, CreateWalkRouteRequest request);

    List<WalkRouteListResponse> getUserWalkRoutes(Long userId);

    WalkRouteResponse getWalkRoute(Long routeId, Long userId);

    void deleteWalkRoute(Long routeId, Long userId);
}