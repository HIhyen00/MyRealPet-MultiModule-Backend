package petwalk.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petwalk.core.WalkRoute;
import petwalk.core.WalkRouteRepository;
import petwalk.core.service.WalkRouteService;
import petwalk.dto.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WalkRouteServiceImpl implements WalkRouteService {

    private final WalkRouteRepository walkRouteRepository;
    private final ObjectMapper objectMapper;

    @Override
    public WalkRouteResponse createWalkRoute(Long userId, CreateWalkRouteRequest request) {
        try {
            String coordinatesJson = objectMapper.writeValueAsString(request.getCoordinates());
            double distance = calculateDistance(request.getCoordinates());

            WalkRoute walkRoute = WalkRoute.builder()
                    .userId(userId)
                    .name(request.getName())
                    .description(request.getDescription())
                    .coordinatesJson(coordinatesJson)
                    .distance(distance)
                    .build();

            WalkRoute saved = walkRouteRepository.save(walkRoute);
            return convertToResponse(saved);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize coordinates", e);
            throw new RuntimeException("Failed to create walk route", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalkRouteListResponse> getUserWalkRoutes(Long userId) {
        return walkRouteRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToListResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WalkRouteResponse getWalkRoute(Long routeId, Long userId) {
        WalkRoute walkRoute = walkRouteRepository.findById(routeId)
                .filter(route -> route.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Walk route not found"));

        return convertToResponse(walkRoute);
    }

    @Override
    public WalkRouteResponse updateWalkRoute(Long routeId, Long userId, UpdateWalkRouteRequest request) {
        try {
            WalkRoute walkRoute = walkRouteRepository.findById(routeId)
                    .filter(route -> route.getUserId().equals(userId))
                    .orElseThrow(() -> new RuntimeException("Walk route not found"));

            String coordinatesJson = objectMapper.writeValueAsString(request.getCoordinates());
            double distance = calculateDistance(request.getCoordinates());

            walkRoute.updateRoute(request.getName(), request.getDescription(), coordinatesJson, distance);
            WalkRoute updated = walkRouteRepository.save(walkRoute);

            return convertToResponse(updated);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize coordinates", e);
            throw new RuntimeException("Failed to update walk route", e);
        }
    }

    @Override
    public void deleteWalkRoute(Long routeId, Long userId) {
        WalkRoute walkRoute = walkRouteRepository.findById(routeId)
                .filter(route -> route.getUserId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Walk route not found"));

        walkRouteRepository.delete(walkRoute);
    }

    private WalkRouteResponse convertToResponse(WalkRoute walkRoute) {
        try {
            List<Coordinate> coordinates = objectMapper.readValue(
                    walkRoute.getCoordinatesJson(),
                    new TypeReference<List<Coordinate>>() {}
            );

            return WalkRouteResponse.builder()
                    .id(walkRoute.getId())
                    .name(walkRoute.getName())
                    .description(walkRoute.getDescription())
                    .coordinates(coordinates)
                    .distance(walkRoute.getDistance())
                    .createdAt(walkRoute.getCreatedAt())
                    .updatedAt(walkRoute.getUpdatedAt())
                    .build();

        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize coordinates", e);
            throw new RuntimeException("Failed to convert walk route", e);
        }
    }

    private WalkRouteListResponse convertToListResponse(WalkRoute walkRoute) {
        return WalkRouteListResponse.builder()
                .id(walkRoute.getId())
                .name(walkRoute.getName())
                .description(walkRoute.getDescription())
                .distance(walkRoute.getDistance())
                .createdAt(walkRoute.getCreatedAt())
                .build();
    }

    private double calculateDistance(List<Coordinate> coordinates) {
        if (coordinates == null || coordinates.size() < 2) {
            return 0.0;
        }

        double totalDistance = 0.0;
        for (int i = 0; i < coordinates.size() - 1; i++) {
            Coordinate from = coordinates.get(i);
            Coordinate to = coordinates.get(i + 1);
            totalDistance += haversineDistance(from.getLat(), from.getLng(), to.getLat(), to.getLng());
        }

        return Math.round(totalDistance * 100.0) / 100.0; // 소수점 2자리까지
    }

    private double haversineDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371.0; // 지구 반지름 (km)

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }
}