package petwalk.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateWalkRouteRequest {
    private String name;
    private String description;
    private List<Coordinate> coordinates;
}