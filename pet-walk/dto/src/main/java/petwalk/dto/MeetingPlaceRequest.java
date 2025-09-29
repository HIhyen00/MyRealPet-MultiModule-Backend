package petwalk.dto;

import lombok.Data;

import java.util.List;

@Data
public class MeetingPlaceRequest {
    private List<MeetingLocation> meetingLocations;
    private CategoryGroupCode categoryGroupCode;
    private Integer radius;
    private Integer size;


}