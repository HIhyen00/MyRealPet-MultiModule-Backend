package petwalk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalkRouteListResponse {
    private Long id;
    private String name;
    private String description;
    private List<Coordinate> coordinates;
    private Double distance;
    private LocalDateTime createdAt;
}