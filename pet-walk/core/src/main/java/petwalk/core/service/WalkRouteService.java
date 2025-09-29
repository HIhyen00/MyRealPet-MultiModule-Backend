package petwalk.core.service;

import petwalk.dto.CreateWalkRouteRequest;
import petwalk.dto.UpdateWalkRouteRequest;
import petwalk.dto.WalkRouteListResponse;
import petwalk.dto.WalkRouteResponse;

import java.util.List;

public interface WalkRouteService {

    WalkRouteResponse createWalkRoute(Long userId, CreateWalkRouteRequest request);

    List<WalkRouteListResponse> getUserWalkRoutes(Long userId);

    WalkRouteResponse getWalkRoute(Long routeId, Long userId);

    WalkRouteResponse updateWalkRoute(Long routeId, Long userId, UpdateWalkRouteRequest request);

    void deleteWalkRoute(Long routeId, Long userId);
}